package com.example.lisdesper.ui.deudores;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lisdesper.firebase.CBaseDatos;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeudoresViewModel extends ViewModel {
    private final MutableLiveData<List<Deudores>> deudores;
    private final MutableLiveData<Boolean> isLoading;
    private final CBaseDatos db;
    private String currentDeudoresId;
    private String currentDeudoresNombre = "Deudores Principal";
    private ListenerRegistration itemsListener;

    public DeudoresViewModel() {
        deudores = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(true);
        db = CBaseDatos.getInstance();
        cargarDeudoresPrincipal();
    }

    private void cargarDeudoresPrincipal() {
        isLoading.setValue(true);
        db.obtenerDeudoresPrincipal((deudorId, e) -> {
            if (e != null || deudorId == null) {
                isLoading.postValue(false);
                return;
            }
            this.currentDeudoresId = deudorId;

            List<Deudores> current = new ArrayList<>();
            current.add(new Deudores(currentDeudoresNombre));
            deudores.postValue(current);

            itemsListener = db.deudoresCollection.document(deudorId).collection("items")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null || querySnapshot == null) {
                            isLoading.postValue(false);
                            return;
                        }
                        List<ItemDeudores> newItemDeudores = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            double monto = doc.getDouble("monto") != null ? doc.getDouble("monto") : 0.0;
                            ItemDeudores itemDeudores = new ItemDeudores(
                                    doc.getId(),
                                    doc.getString("nombre"),
                                    doc.getString("detalle"),
                                    monto,
                                    Boolean.TRUE.equals(doc.getBoolean("cancelado"))
                            );
                            String fechaStr = doc.getString("fecha");
                            if (fechaStr != null) {
                                itemDeudores.setFecha(fechaStr);
                            }
                            newItemDeudores.add(itemDeudores);
                        }
                        Collections.sort(newItemDeudores, (a, b) -> {
                            if (a.getFecha() == null) return 1;
                            if (b.getFecha() == null) return -1;
                            return b.getFecha().compareTo(a.getFecha());
                        });
                        List<Deudores> updated = deudores.getValue();
                        if (updated != null && !updated.isEmpty()) {
                            updated.get(0).setItems(newItemDeudores);
                            deudores.postValue(updated);
                        }
                        isLoading.postValue(false);
                    });
        });
    }

    public LiveData<List<Deudores>> getDeudores() {
        return deudores;
    }

    public void agregarItem(int posicionDeudores, ItemDeudores itemDeudores) {
        db.agregarItem(currentDeudoresId, itemDeudores, null);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void actualizarItem(int posicionDeudores, int posicionItem, ItemDeudores itemDeudoresActualizado) {
        if (itemDeudoresActualizado.getId() == null || itemDeudoresActualizado.getId().isEmpty()) {
            return;
        }
        db.actualizarItem(currentDeudoresId, itemDeudoresActualizado.getId(), itemDeudoresActualizado, null);
    }
}