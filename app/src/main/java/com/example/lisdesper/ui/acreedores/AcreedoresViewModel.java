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
    private final CBaseDatos db;
    private String currentAcreedoresId;
    private String currentAcreedoresNombre = "Acreedores Principal";
    private ListenerRegistration itemsListener;

    public AcreedoresViewModel() {
        acreedores = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(true);
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
                            newItemAcreedores.add(itemAcreedores);
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

    public LiveData<List<Acreedores>> getAcreedores() {
        return acreedores;
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

    @Override
    protected void onCleared() {
        if (itemsListener != null) {
            itemsListener.remove();
        }
        super.onCleared();
    }
}