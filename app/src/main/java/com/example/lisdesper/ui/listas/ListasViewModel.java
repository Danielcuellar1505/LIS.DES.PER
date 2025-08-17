package com.example.lisdesper.ui.listas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lisdesper.firebase.CBaseDatos;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListasViewModel extends ViewModel {
    private final MutableLiveData<List<Lista>> listas;
    private final MutableLiveData<Boolean> isLoading;
    private final CBaseDatos db;
    private String currentListaId;
    private String currentListaNombre = "Lista Principal";
    private ListenerRegistration itemsListener;
    public ListasViewModel() {
        listas = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(true);
        db = CBaseDatos.getInstance();
        cargarListaPrincipal();
    }
    private void cargarListaPrincipal() {
        isLoading.setValue(true);
        db.obtenerListaPrincipal((listaId, e) -> {
            if (e != null || listaId == null) {
                isLoading.postValue(false);
                return;
            }
            this.currentListaId = listaId;

            List<Lista> current = new ArrayList<>();
            current.add(new Lista(currentListaNombre));
            listas.postValue(current);

            itemsListener = db.listasCollection.document(listaId).collection("items")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null || querySnapshot == null) {
                            isLoading.postValue(false);
                            return;
                        }
                        List<ItemLista> newItemListas = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            double monto = doc.getDouble("monto") != null ? doc.getDouble("monto") : 0.0;
                            ItemLista itemLista = new ItemLista(
                                    doc.getId(),
                                    doc.getString("nombre"),
                                    doc.getString("detalle"),
                                    monto,
                                    Boolean.TRUE.equals(doc.getBoolean("cancelado"))
                            );
                            String fechaStr = doc.getString("fecha");
                            if (fechaStr != null) {
                                itemLista.setFecha(fechaStr);
                            }
                            newItemListas.add(itemLista);
                        }
                        Collections.sort(newItemListas, (a, b) -> {
                            if (a.getFecha() == null) return 1;
                            if (b.getFecha() == null) return -1;
                            return b.getFecha().compareTo(a.getFecha());
                        });
                        List<Lista> updated = listas.getValue();
                        if (updated != null && !updated.isEmpty()) {
                            updated.get(0).setItems(newItemListas);
                            listas.postValue(updated);
                        }
                        isLoading.postValue(false);
                    });
        });
    }
    public LiveData<List<Lista>> getListas() {
        return listas;
    }
    public void agregarItem(int posicionLista, ItemLista itemLista) {
        db.agregarItem(currentListaId, itemLista, null);
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public void actualizarItem(int posicionLista, int posicionItem, ItemLista itemListaActualizado) {
        if (itemListaActualizado.getId() == null || itemListaActualizado.getId().isEmpty()) {
            return;
        }
        db.actualizarItem(currentListaId, itemListaActualizado.getId(), itemListaActualizado, null);
    }
}