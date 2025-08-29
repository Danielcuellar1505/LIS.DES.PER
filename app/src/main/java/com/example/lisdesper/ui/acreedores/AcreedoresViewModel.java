package com.example.lisdesper.ui.acreedores;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lisdesper.firebase.CBaseDatos;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AcreedoresViewModel extends ViewModel {
    private final MutableLiveData<List<Acreedores>> acreedores;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<List<String>> nombresParaAutocompletado;
    private final CBaseDatos db;
    private String currentAcreedoresId;
    private String currentAcreedoresNombre = "Acreedores Principal";
    private ListenerRegistration itemsListener;
    private String selectedNameFilter = null;

    public AcreedoresViewModel() {
        acreedores = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(true);
        nombresParaAutocompletado = new MutableLiveData<>(new ArrayList<>());
        db = CBaseDatos.getInstance();
        cargarAcreedoresPrincipal();
    }

    private void cargarAcreedoresPrincipal() {
        isLoading.setValue(true);
        db.obtenerAcreedoresPrincipal((acreedorId, e) -> {
            if (e != null || acreedorId == null) {
                isLoading.postValue(false);
                return;
            }
            this.currentAcreedoresId = acreedorId;

            List<Acreedores> current = new ArrayList<>();
            current.add(new Acreedores(currentAcreedoresNombre));
            acreedores.postValue(current);

            db.obtenerNombresParaAutocompletado(acreedorId, acreedorId, (nombres, error) -> {
                if (error == null && nombres != null) {
                    nombresParaAutocompletado.postValue(nombres);
                }
            });

            itemsListener = db.deudoresCollection.document(acreedorId).collection("items")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null || querySnapshot == null) {
                            isLoading.postValue(false);
                            return;
                        }
                        List<ItemAcreedores> newItemAcreedores = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            double monto = doc.getDouble("monto") != null ? doc.getDouble("monto") : 0.0;
                            ItemAcreedores itemAcreedores = new ItemAcreedores(
                                    doc.getId(),
                                    doc.getString("nombre"),
                                    doc.getString("detalle"),
                                    monto,
                                    Boolean.TRUE.equals(doc.getBoolean("cancelado"))
                            );
                            String fechaStr = doc.getString("fecha");
                            if (fechaStr != null) {
                                itemAcreedores.setFecha(fechaStr);
                            }
                            if (selectedNameFilter == null || itemAcreedores.getNombre().equalsIgnoreCase(selectedNameFilter)) {
                                newItemAcreedores.add(itemAcreedores);
                            }
                        }
                        Collections.sort(newItemAcreedores, (a, b) -> {
                            if (a.getFecha() == null) return 1;
                            if (b.getFecha() == null) return -1;
                            return b.getFecha().compareTo(a.getFecha());
                        });
                        List<Acreedores> updated = acreedores.getValue();
                        if (updated != null && !updated.isEmpty()) {
                            updated.get(0).setItems(newItemAcreedores);
                            acreedores.postValue(updated);
                        }
                        isLoading.postValue(false);
                    });
        });
    }

    public void filterByName(String nombre) {
        selectedNameFilter = nombre;
        cargarAcreedoresPrincipal();
    }

    public void clearNameFilter() {
        selectedNameFilter = null;
        cargarAcreedoresPrincipal();
    }

    public LiveData<List<Acreedores>> getAcreedores() {
        return acreedores;
    }

    public LiveData<List<String>> getNombresParaAutocompletado() {
        return nombresParaAutocompletado;
    }

    public void agregarItem(int posicionAcreedores, ItemAcreedores itemAcreedores) {
        db.agregarItemAcreedor(currentAcreedoresId, itemAcreedores, null);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void actualizarItem(int posicionAcreedores, int posicionItem, ItemAcreedores itemAcreedoresActualizado) {
        if (itemAcreedoresActualizado.getId() == null || itemAcreedoresActualizado.getId().isEmpty()) {
            return;
        }
        db.actualizarItemAcreedor(currentAcreedoresId, itemAcreedoresActualizado.getId(), itemAcreedoresActualizado, null);
    }
    public void eliminarItem(int posicionAcreedores, String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return;
        }
        db.eliminarItemAcreedor(currentAcreedoresId, itemId, null);
    }

    @Override
    protected void onCleared() {
        if (itemsListener != null) {
            itemsListener.remove();
        }
        super.onCleared();
    }
}