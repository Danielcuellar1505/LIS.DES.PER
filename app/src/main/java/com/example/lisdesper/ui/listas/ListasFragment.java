package com.example.lisdesper.ui.listas;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lisdesper.R;
import com.example.lisdesper.databinding.FragmentListasBinding;
import com.example.lisdesper.utils.ModalHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListasFragment extends Fragment {
    private FragmentListasBinding binding;
    private ListasViewModel listasViewModel;
    private ItemsAdapter adapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        listasViewModel = new ViewModelProvider(this).get(ListasViewModel.class);
        listasViewModel.cargarDesdeFirestore();

        binding = FragmentListasBinding.inflate(inflater, container, false);

        adapter = new ItemsAdapter(new ArrayList<>(), (originalIndex, isChecked) -> {
            List<Lista> listas = listasViewModel.getListas().getValue();
            if (listas != null && !listas.isEmpty()) {
                Lista primera = listas.get(0);
                if (originalIndex >= 0 && originalIndex < primera.getItems().size()) {
                    Item it = primera.getItems().get(originalIndex);
                    if (isChecked) {
                        mostrarModalConfirmacion(it.getNombre(), it.getMonto());
                    }
                    binding.recyclerViewListas.post(() ->
                            listasViewModel.actualizarItem(0, originalIndex, it)
                    );
                }
            }
        });

        binding.recyclerViewListas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewListas.setAdapter(adapter);

        listasViewModel.getListas().observe(getViewLifecycleOwner(), listas -> {
            List<ListEntry> flattened = new ArrayList<>();
            if (listas != null && !listas.isEmpty()) {
                Lista primera = listas.get(0);
                List<Item> items = primera.getItems();
                String lastFecha = null;
                for (int i = 0; i < items.size(); i++) {
                    Item it = items.get(i);
                    String fecha = it.getFecha();
                    if (lastFecha == null || !lastFecha.equals(fecha)) {
                        flattened.add(ListEntry.header(formatearFechaParaMostrar(fecha)));
                        lastFecha = fecha;
                    }
                    flattened.add(ListEntry.item(it, i));
                }
            }
            adapter.setItems(flattened);
        });
        binding.fabAgregarLista.setOnClickListener(v -> {
            mostrarDialogoAgregarItem();
        });
        return binding.getRoot();
    }
    private void mostrarModalConfirmacion(String nombre, double monto) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View vista = inflater.inflate(R.layout.dialog_confirmacion_cancelacion, null);
        ImageView imgCheck = vista.findViewById(R.id.imgCheck);
        TextView txtMensaje = vista.findViewById(R.id.tvMensajeCancelacion);
        String mensaje = nombre + " canceló " + String.format("%.2f Bs", monto);
        txtMensaje.setText(mensaje);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(vista)
                .create();

        if (dialog.getWindow() != null) {
            int colorOnSecondary  = requireContext().getResources().getColor(R.color.colorOnSecondary);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(colorOnSecondary ));
        }
        dialog.show();

        new Handler().postDelayed(dialog::dismiss, 2000);
    }
    private void mostrarDialogoAgregarItem() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_agregar_item, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etDetalle = dialogView.findViewById(R.id.etDetalle);
        EditText etMonto = dialogView.findViewById(R.id.etMonto);

        ModalHelper.showCustomDialog(
                requireContext(),
                R.layout.dialog_agregar_item,
                "Nuevo",
                "Agregar", () -> {
                    String nombre = etNombre.getText().toString().trim();
                    String detalle = etDetalle.getText().toString().trim();
                    String montoStr = etMonto.getText().toString().trim();
                    if (!nombre.isEmpty() && !detalle.isEmpty() && !montoStr.isEmpty()) {
                        double monto = Double.parseDouble(montoStr);
                        listasViewModel.agregarItem(0, new Item(nombre, detalle, monto, false));
                    }
                },
                "Cancelar", null
        );
    }
    private String formatearFechaParaMostrar(String fechaIso) {
        if (fechaIso == null) return "";
        try {
            SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date d = parse.parse(fechaIso);
            if (d == null) return fechaIso;

            Calendar calNow = Calendar.getInstance();
            Calendar calThen = Calendar.getInstance();
            calThen.setTime(d);

            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (isSameDay(calNow, calThen)) {
                return "HOY — " + out.format(d);
            } else {
                return out.format(d);
            }
        } catch (Exception e) {
            return fechaIso;
        }
    }
    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
