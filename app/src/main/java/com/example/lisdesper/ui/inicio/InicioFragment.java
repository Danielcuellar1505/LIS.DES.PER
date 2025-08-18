package com.example.lisdesper.ui.inicio;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lisdesper.R;
import com.example.lisdesper.databinding.FragmentInicioBinding;
import com.example.lisdesper.ui.listas.ItemLista;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private InicioViewModel homeViewModel;
    private AlertDialog loadingDialog;
    private ItemsAdapterInicio adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(InicioViewModel.class);
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupPieChart();
        setupRecyclerView();

        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        binding.tvTodayItemsTitle.setText("Deudas de hoy (" + todayDate + ")");

        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    showLoadingDialog();
                } else {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            }
        });

        homeViewModel.getChartData().observe(getViewLifecycleOwner(), chartData -> {
            updatePieChart(chartData.getCancelados(), chartData.getNoCancelados());
        });

        homeViewModel.getTodayItems().observe(getViewLifecycleOwner(), items -> {
            updateRecyclerView(items);
        });

        return root;
    }
    private void setupPieChart() {
        PieChart pieChart = binding.pieChart;
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(true);
        pieChart.setEntryLabelTextSize(12f);
    }
    private void updatePieChart(int cancelados, int noCancelados) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(cancelados, "Cancelados"));
        entries.add(new PieEntry(noCancelados, "No Cancelados"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                requireContext().getResources().getColor(R.color.colorPrimary),
                requireContext().getResources().getColor(R.color.colorSecondary)
        });
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(android.R.color.white);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter(binding.pieChart));

        binding.pieChart.setData(data);
        binding.pieChart.invalidate();
    }
    private void setupRecyclerView() {
        adapter = new ItemsAdapterInicio(new ArrayList<>());
        binding.recyclerViewTodayItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewTodayItems.setAdapter(adapter);
    }

    private void updateRecyclerView(List<ItemLista> items) {
        adapter.setItems(items);
    }

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reutilizable, null);
        ViewSwitcher viewSwitcher = dialogView.findViewById(R.id.viewSwitcher);
        viewSwitcher.setDisplayedChild(0);

        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        binding = null;
    }
}
