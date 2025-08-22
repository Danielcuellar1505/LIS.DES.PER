package com.example.lisdesper.ui.listas;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lisdesper.R;
import com.example.lisdesper.databinding.FragmentListasBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListasFragment extends Fragment {
    private FragmentListasBinding binding;
    private ListasViewModel listasViewModel;
    private ItemsAdapterLista adapter;
    private boolean mostrarCancelados = false;
    private AlertDialog loadingDialog;
    private AlertDialog cancellationDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listasViewModel = new ViewModelProvider(this).get(ListasViewModel.class);
        binding = FragmentListasBinding.inflate(inflater, container, false);

        adapter = new ItemsAdapterLista(new ArrayList<>(),
                (originalIndex, isChecked, item) -> {
                    List<Lista> listas = listasViewModel.getListas().getValue();
                    if (listas != null && !listas.isEmpty()) {
                        Lista primera = listas.get(0);
                        if (originalIndex >= 0 && originalIndex < primera.getItems().size()) {
                            ItemLista it = primera.getItems().get(originalIndex);
                            it.setCancelado(isChecked);
                            binding.recyclerViewListas.post(() ->
                                    listasViewModel.actualizarItem(0, originalIndex, it)
                            );
                            if (isChecked) {
                                showReusableDialog(false, item.getNombre(), item.getMonto());
                            }
                        }
                    }
                },
                (originalIndex, item) -> {
                    Boolean isLoading = listasViewModel.getIsLoading().getValue();
                    if (isLoading == null || !isLoading) {
                        mostrarDialogoEditarItem(item, originalIndex);
                    }
                });

        binding.recyclerViewListas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewListas.setAdapter(adapter);

        listasViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    showReusableDialog(true, null, 0.0);
                } else {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
                binding.fabAgregarLista.setEnabled(!isLoading);
                binding.btnToggleCancelados.setEnabled(!isLoading);
            }
        });

        listasViewModel.getListas().observe(getViewLifecycleOwner(), listas -> {
            actualizarLista();
        });

        binding.fabAgregarLista.setOnClickListener(v -> {
            Boolean isLoading = listasViewModel.getIsLoading().getValue();
            if (isLoading == null || !isLoading) {
                mostrarDialogoAgregarItem();
            }
        });

        binding.btnToggleCancelados.setOnClickListener(v -> {
            Boolean isLoading = listasViewModel.getIsLoading().getValue();
            if (isLoading == null || !isLoading) {
                mostrarCancelados = !mostrarCancelados;
                setButtonIconAndText(mostrarCancelados);
                actualizarLista();
            }
        });

        setButtonIconAndText(mostrarCancelados);

        return binding.getRoot();
    }

    private void setButtonIconAndText(boolean mostrarCancelados) {
        int drawableRes = mostrarCancelados ? R.drawable.ic_close_eye : R.drawable.ic_open_eye;
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableRes);
        if (drawable != null) {
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            drawable.setBounds(0, 0, size, size);
            binding.btnToggleCancelados.setCompoundDrawables(drawable, null, null, null);
        }
        binding.btnToggleCancelados.setText(mostrarCancelados ? "Ocultar" : "Mostrar");
    }

    private void actualizarLista() {
        List<Lista> listas = listasViewModel.getListas().getValue();
        List<ListaEntry> flattened = new ArrayList<>();
        if (listas != null && !listas.isEmpty()) {
            Lista primera = listas.get(0);
            List<ItemLista> itemListas = primera.getItems();
            String lastFecha = null;
            for (int i = 0; i < itemListas.size(); i++) {
                ItemLista it = itemListas.get(i);
                if (mostrarCancelados || !it.isCancelado()) {
                    String fecha = it.getFecha();
                    if (lastFecha == null || !lastFecha.equals(fecha)) {
                        flattened.add(ListaEntry.header(formatearFechaParaMostrar(fecha)));
                        lastFecha = fecha;
                    }
                    flattened.add(ListaEntry.item(it, i));
                }
            }
        }
        adapter.setItems(flattened);
    }

    private void mostrarDialogoAgregarItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.lista_dialog_agregar_item, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etDetalle = dialogView.findViewById(R.id.etDetalle);
        EditText etMonto = dialogView.findViewById(R.id.etMonto);
        etMonto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setView(dialogView)
                .setTitle("Nuevo")
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String detalle = etDetalle.getText().toString().trim();
                    String montoStr = etMonto.getText().toString().trim();
                    if (nombre.isEmpty() || detalle.isEmpty() || montoStr.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor ingresa nombre, detalle y monto", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double monto;
                    try {
                        monto = Double.parseDouble(montoStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Monto inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ItemLista nuevo = new ItemLista(nombre, detalle, monto, false);
                    listasViewModel.agregarItem(0, nuevo);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarItem(ItemLista item, int originalIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.lista_dialog_agregar_item, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etDetalle = dialogView.findViewById(R.id.etDetalle);
        EditText etMonto = dialogView.findViewById(R.id.etMonto);
        etMonto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        etNombre.setText(item.getNombre());
        etDetalle.setText(item.getDetalle());
        etMonto.setText(String.format(Locale.getDefault(), "%.2f", item.getMonto()));

        builder.setView(dialogView)
                .setTitle("Editar Ítem")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String detalle = etDetalle.getText().toString().trim();
                    String montoStr = etMonto.getText().toString().trim();
                    if (nombre.isEmpty() || detalle.isEmpty() || montoStr.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor ingresa nombre, detalle y monto", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double monto;
                    try {
                        monto = Double.parseDouble(montoStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Monto inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ItemLista actualizado = new ItemLista(item.getId(), nombre, detalle, monto, item.isCancelado());
                    actualizado.setFecha(item.getFecha());
                    listasViewModel.actualizarItem(0, originalIndex, actualizado);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showReusableDialog(boolean isLoadingMode, String nombre, double monto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reutilizable, null);
        ViewSwitcher viewSwitcher = dialogView.findViewById(R.id.viewSwitcher);
        TextView tvMensajeCancelacion = dialogView.findViewById(R.id.tvMensajeCancelacion);

        if (isLoadingMode) {
            viewSwitcher.setDisplayedChild(0);
            builder.setCancelable(false);
        } else {
            viewSwitcher.setDisplayedChild(1);
            tvMensajeCancelacion.setText(String.format(Locale.getDefault(), "%s canceló %.2f Bs", nombre, monto));
            builder.setCancelable(true);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (cancellationDialog != null && cancellationDialog.isShowing()) {
                    cancellationDialog.dismiss();
                }
            }, 1440);
        }

        AlertDialog dialog = builder.setView(dialogView).create();
        if (isLoadingMode) {
            loadingDialog = dialog;
        } else {
            cancellationDialog = dialog;
        }
        dialog.show();
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
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        if (cancellationDialog != null && cancellationDialog.isShowing()) {
            cancellationDialog.dismiss();
        }
        binding = null;
    }
}