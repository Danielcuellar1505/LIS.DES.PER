package com.example.lisdesper.ui.deudores;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;
import com.example.lisdesper.databinding.FragmentDeudoresBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeudoresFragment extends Fragment {
    private FragmentDeudoresBinding binding;
    private DeudoresViewModel deudoresViewModel;
    private ItemsAdapterDeudores adapter;
    private boolean mostrarCancelados = false;
    private AlertDialog loadingDialog;
    private AlertDialog cancellationDialog;
    private String selectedDateFilter = null;
    private String selectedNameFilter = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deudoresViewModel = new ViewModelProvider(this).get(DeudoresViewModel.class);
        binding = FragmentDeudoresBinding.inflate(inflater, container, false);

        adapter = new ItemsAdapterDeudores(new ArrayList<>(),
                (originalIndex, isChecked, item) -> {
                    List<Deudores> deudores = deudoresViewModel.getDeudores().getValue();
                    if (deudores != null && !deudores.isEmpty()) {
                        Deudores primera = deudores.get(0);
                        if (originalIndex >= 0 && originalIndex < primera.getItems().size()) {
                            ItemDeudores it = primera.getItems().get(originalIndex);
                            it.setCancelado(isChecked);
                            binding.recyclerViewDeudores.post(() ->
                                    deudoresViewModel.actualizarItem(0, originalIndex, it)
                            );
                            if (isChecked) {
                                showReusableDialog(false, item.getNombre(), item.getMonto());
                            }
                        }
                    }
                },
                (originalIndex, item) -> {
                    Boolean isLoading = deudoresViewModel.getIsLoading().getValue();
                    if (isLoading == null || !isLoading) {
                        mostrarDialogoEditarItem(item, originalIndex);
                    }
                },
                (originalIndex, item) -> {
                    Boolean isLoading = deudoresViewModel.getIsLoading().getValue();
                    if (isLoading == null || !isLoading) {
                        mostrarDialogoEliminarItem(item, originalIndex);
                    }
                },
                (fecha) -> {
                    Boolean isLoading = deudoresViewModel.getIsLoading().getValue();
                    if (isLoading == null || !isLoading) {
                        mostrarDialogoUnificados(fecha);
                    }
                });

        binding.recyclerViewDeudores.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDeudores.setAdapter(adapter);

        deudoresViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    showReusableDialog(true, null, 0.0);
                } else {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
                binding.fabAgregarDeudores.setEnabled(!isLoading);
                binding.btnToggleCancelados.setEnabled(!isLoading);
            }
        });

        deudoresViewModel.getDeudores().observe(getViewLifecycleOwner(), deudores -> {
            actualizarDeudores();
        });

        deudoresViewModel.getNombresParaAutocompletado().observe(getViewLifecycleOwner(), nombres -> {
        });

        binding.fabAgregarDeudores.setOnClickListener(v -> {
            Boolean isLoading = deudoresViewModel.getIsLoading().getValue();
            if (isLoading == null || !isLoading) {
                mostrarDialogoAgregarItem();
            }
        });

        binding.btnToggleCancelados.setOnClickListener(v -> {
            Boolean isLoading = deudoresViewModel.getIsLoading().getValue();
            if (isLoading == null || !isLoading) {
                mostrarCancelados = !mostrarCancelados;
                setButtonIconAndText(mostrarCancelados);
                actualizarDeudores();
            }
        });

        setButtonIconAndText(mostrarCancelados);

        return binding.getRoot();
    }

    public void filterByDate(String fecha) {
        selectedDateFilter = fecha;
        actualizarDeudores();
    }

    public void clearDateFilter() {
        selectedDateFilter = null;
        actualizarDeudores();
    }

    public void filterByName(String nombre) {
        selectedNameFilter = nombre;
        actualizarDeudores();
    }

    public void clearNameFilter() {
        selectedNameFilter = null;
        actualizarDeudores();
    }

    private void setButtonIconAndText(boolean mostrarCancelados) {
        int drawableRes = mostrarCancelados ? R.drawable.ic_close_eye_black_24dp : R.drawable.ic_open_eye_black_24dp;
        binding.btnToggleCancelados.setIcon(ContextCompat.getDrawable(requireContext(), drawableRes));
        binding.btnToggleCancelados.setText(mostrarCancelados ? "Ocultar" : "Mostrar");
    }

    private void actualizarDeudores() {
        List<Deudores> deudores = deudoresViewModel.getDeudores().getValue();
        List<DeudoresEntry> flattened = new ArrayList<>();
        if (deudores != null && !deudores.isEmpty()) {
            Deudores primera = deudores.get(0);
            List<ItemDeudores> itemDeudores = primera.getItems();
            String lastFecha = null;
            double totalPorFecha = 0.0;
            for (int i = 0; i < itemDeudores.size(); i++) {
                ItemDeudores it = itemDeudores.get(i);
                if ((selectedDateFilter == null || it.getFecha().equals(selectedDateFilter)) &&
                        (selectedNameFilter == null || it.getNombre().equalsIgnoreCase(selectedNameFilter))) {
                    if (mostrarCancelados || !it.isCancelado()) {
                        String fecha = it.getFecha();
                        if (lastFecha == null || !lastFecha.equals(fecha)) {
                            if (lastFecha != null) {
                                flattened.add(DeudoresEntry.total(totalPorFecha));
                            }
                            flattened.add(DeudoresEntry.header(formatearFechaParaMostrar(fecha)));
                            lastFecha = fecha;
                            totalPorFecha = 0.0;
                        }
                        flattened.add(DeudoresEntry.item(it, i));
                        totalPorFecha += it.getMonto();
                    }
                }
            }
            if (lastFecha != null && totalPorFecha > 0.0) {
                flattened.add(DeudoresEntry.total(totalPorFecha));
            }
        }
        adapter.setItems(flattened);
    }

    private void mostrarDialogoUnificados(String fechaFormatted) {
        String fechaIso;
        try {
            if (fechaFormatted.startsWith("HOY — ")) {
                fechaIso = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            } else {
                SimpleDateFormat parse = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date d = parse.parse(fechaFormatted.replace("HOY — ", ""));
                fechaIso = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(d);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al procesar la fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Deudores> deudores = deudoresViewModel.getDeudores().getValue();
        if (deudores == null || deudores.isEmpty()) return;

        List<ItemDeudores> items = deudores.get(0).getItems();
        Map<String, UnifiedItemsAdapter.UnifiedItem> unifiedMap = new HashMap<>();

        for (ItemDeudores item : items) {
            if (item.getFecha().equals(fechaIso) && (mostrarCancelados || !item.isCancelado()) &&
                    (selectedNameFilter == null || item.getNombre().equalsIgnoreCase(selectedNameFilter))) {
                String detalle = item.getDetalle();
                double monto = item.getMonto();
                UnifiedItemsAdapter.UnifiedItem unifiedItem = unifiedMap.get(detalle);
                if (unifiedItem == null) {
                    unifiedItem = new UnifiedItemsAdapter.UnifiedItem(1, detalle, monto);
                    unifiedMap.put(detalle, unifiedItem);
                } else {
                    unifiedItem.cantidad += 1;
                    unifiedItem.montoTotal += monto;
                }
            }
        }

        List<UnifiedItemsAdapter.UnifiedItem> unifiedItems = new ArrayList<>(unifiedMap.values());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.deudores_unified_dialog, null);
        RecyclerView rvUnifiedItems = dialogView.findViewById(R.id.rvUnifiedItems);
        final TextView tvTotalGeneral = dialogView.findViewById(R.id.tvTotalGeneral);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        ImageButton btnCopy = dialogView.findViewById(R.id.btnCopy);

        tvDialogTitle.setText("Lista de pedidos - " + fechaFormatted);

        double initialTotal = 0.0;
        for (UnifiedItemsAdapter.UnifiedItem item : unifiedItems) {
            initialTotal += item.getMontoTotal();
        }
        tvTotalGeneral.setText(String.format(Locale.getDefault(), "%.2f", initialTotal));

        btnCopy.setOnClickListener(v -> {
            StringBuilder clipboardText = new StringBuilder();
            for (UnifiedItemsAdapter.UnifiedItem item : unifiedItems) {
                clipboardText.append(String.format(Locale.getDefault(), "%d %s\n", item.getCantidad(), item.getDetalle()));
            }
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Ítems Unificados", clipboardText.toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Lista copiada a portapapeles", Toast.LENGTH_SHORT).show();
        });

        final UnifiedItemsAdapter[] adapterHolder = new UnifiedItemsAdapter[1];
        adapterHolder[0] = new UnifiedItemsAdapter(unifiedItems, position -> {
            unifiedItems.remove(position);
            adapterHolder[0].notifyItemRemoved(position);
            adapterHolder[0].notifyItemRangeChanged(position, unifiedItems.size());

            double newTotal = 0.0;
            for (UnifiedItemsAdapter.UnifiedItem item : unifiedItems) {
                newTotal += item.getMontoTotal();
            }
            tvTotalGeneral.setText(String.format(Locale.getDefault(), "%.2f", newTotal));
        });
        rvUnifiedItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUnifiedItems.setAdapter(adapterHolder[0]);

        builder.setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void mostrarDialogoAgregarItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.deudores_dialog_agregar_item, null);

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

                    ItemDeudores nuevo = new ItemDeudores(nombre, detalle, monto, false);
                    deudoresViewModel.agregarItem(0, nuevo);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarItem(ItemDeudores item, int originalIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.deudores_dialog_agregar_item, null);

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

                    ItemDeudores actualizado = new ItemDeudores(item.getId(), nombre, detalle, monto, item.isCancelado());
                    actualizado.setFecha(item.getFecha());
                    deudoresViewModel.actualizarItem(0, originalIndex, actualizado);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEliminarItem(ItemDeudores item, int originalIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Ítem")
                .setMessage("¿Estás seguro de que quieres eliminar este ítem: " + item.getNombre() + "? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    deudoresViewModel.eliminarItem(0, item.getId());
                    Toast.makeText(getContext(), "Ítem eliminado", Toast.LENGTH_SHORT).show();
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
            tvMensajeCancelacion.setText(String.format(Locale.getDefault(), "Cancelé %.2f a %s", monto, nombre));
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