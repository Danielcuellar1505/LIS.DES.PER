package com.example.lisdesper.ui.Dashboard;

import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lisdesper.R;
import com.example.lisdesper.databinding.FragmentDashboardBinding;
import com.example.lisdesper.ui.deudores.ItemDeudores;
import com.example.lisdesper.ui.acreedores.ItemAcreedores;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel homeViewModel;
    private AlertDialog loadingDialog;
    private ItemsAdapterDashboard adapterDeudores;
    private ItemsAdapterDashboard adapterAcreedores;
    private boolean isDeudoresExpanded = false;
    private boolean isAcreedoresExpanded = false;
    private String selectedDateFilter = null; // Almacena la fecha seleccionada para filtrar

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupPieChart(binding.pieChartDeudores);
        setupPieChart(binding.pieChartAcreedores);
        setupRecyclerViewDeudores();
        setupRecyclerViewAcreedores();
        updateTitles(); // Actualizar títulos inicialmente

        // Configurar botones de acordeón
        binding.btnToggleDeudores.setOnClickListener(v -> toggleDeudores());
        binding.btnToggleAcreedores.setOnClickListener(v -> toggleAcreedores());

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

        homeViewModel.getDeudoresChartData().observe(getViewLifecycleOwner(), chartData -> {
            updatePieChart(binding.pieChartDeudores, chartData.getCancelados(), chartData.getNoCancelados());
        });

        homeViewModel.getTodayDeudoresItems().observe(getViewLifecycleOwner(), items -> {
            updateRecyclerViewDeudores(items);
        });

        homeViewModel.getAcreedoresChartData().observe(getViewLifecycleOwner(), chartData -> {
            updatePieChart(binding.pieChartAcreedores, chartData.getCancelados(), chartData.getNoCancelados());
        });

        homeViewModel.getTodayAcreedoresItems().observe(getViewLifecycleOwner(), items -> {
            updateRecyclerViewAcreedores(items);
        });

        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }
        public void filterByDate(String fecha) {
        selectedDateFilter = fecha;
        updateTitles();
        homeViewModel.filterByDate(fecha);
    }
    public void clearDateFilter() {
        selectedDateFilter = null;
        updateTitles();
        homeViewModel.clearDateFilter();
    }
    private void updateTitles() {
        String displayDate;
        if (selectedDateFilter == null) {
            displayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            binding.tvTodayDeudoresItemsTitle.setText("Deudas de hoy (" + displayDate + ")");
            binding.tvTodayAcreedoresItemsTitle.setText("Acreedores de hoy (" + displayDate + ")");
        } else {
            try {
                SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = parse.parse(selectedDateFilter);
                SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                displayDate = out.format(date);
                Calendar calNow = Calendar.getInstance();
                Calendar calThen = Calendar.getInstance();
                calThen.setTime(date);
                if (isSameDay(calNow, calThen)) {
                    displayDate = "HOY — " + displayDate;
                }
                binding.tvTodayDeudoresItemsTitle.setText("Deudas de (" + displayDate + ")");
                binding.tvTodayAcreedoresItemsTitle.setText("Acreedores de (" + displayDate + ")");
            } catch (Exception e) {
                binding.tvTodayDeudoresItemsTitle.setText("Deudas de (" + selectedDateFilter + ")");
                binding.tvTodayAcreedoresItemsTitle.setText("Acreedores de (" + selectedDateFilter + ")");
            }
        }
    }
    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
    private void toggleDeudores() {
        isDeudoresExpanded = !isDeudoresExpanded;
        binding.deudoresContent.setVisibility(isDeudoresExpanded ? View.VISIBLE : View.GONE);
        binding.btnToggleDeudores.setText(isDeudoresExpanded
                ? "Grafico de deudores \uD83D\uDD3C"
                : "Grafico de deudores \uD83D\uDD3D");
    }

    private void toggleAcreedores() {
        isAcreedoresExpanded = !isAcreedoresExpanded;
        binding.acreedoresContent.setVisibility(isAcreedoresExpanded ? View.VISIBLE : View.GONE);
        binding.btnToggleAcreedores.setText(isAcreedoresExpanded
                ? "Grafico de Acreedores \uD83D\uDD3C"
                : "Grafico de Acreedores \uD83D\uDD3D");
    }

    private void setupPieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawEntryLabels(true);
        TypedArray typedArray = requireContext().getTheme().obtainStyledAttributes(
                new int[]{com.google.android.material.R.attr.colorOnPrimary});
        int colorOnPrimary = typedArray.getColor(0, 0);
        typedArray.recycle();
        pieChart.setEntryLabelColor(colorOnPrimary);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextColor(colorOnPrimary);
        pieChart.setDrawEntryLabels(false);
    }

    private void updatePieChart(PieChart pieChart, int cancelados, int noCancelados) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(cancelados, "Cancelados"));
        entries.add(new PieEntry(noCancelados, "No Cancelados"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                requireContext().getResources().getColor(R.color.colorPrimary),
                requireContext().getResources().getColor(R.color.colorSecondary)
        });
        dataSet.setValueTextSize(14f);
        TypedArray typedArray = requireContext().getTheme().obtainStyledAttributes(
                new int[]{com.google.android.material.R.attr.colorOnPrimary});
        int colorOnPrimary = typedArray.getColor(0, 0);
        typedArray.recycle();
        dataSet.setValueTextColor(colorOnPrimary);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return String.format(Locale.getDefault(), "%.0f", value);
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setupRecyclerViewDeudores() {
        adapterDeudores = new ItemsAdapterDashboard(new ArrayList<>());
        binding.recyclerViewTodayDeudoresItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewTodayDeudoresItems.setAdapter(adapterDeudores);
    }

    private void setupRecyclerViewAcreedores() {
        adapterAcreedores = new ItemsAdapterDashboard(new ArrayList<>());
        binding.recyclerViewTodayAcreedoresItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewTodayAcreedoresItems.setAdapter(adapterAcreedores);
    }

    private void updateRecyclerViewDeudores(List<ItemDeudores> items) {
        adapterDeudores.setItems(items);
    }

    private void updateRecyclerViewAcreedores(List<ItemAcreedores> items) {
        adapterAcreedores.setItems(items);
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