package com.example.lisdesper.ui.listas;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListasViewModel extends ViewModel {
    private final MutableLiveData<List<Lista>> listas;
    public ListasViewModel() {
        listas = new MutableLiveData<>(new ArrayList<>());
    }
    public LiveData<List<Lista>> getListas() {
        return listas;
    }
    public void agregarLista(String nombreLista) {
        List<Lista> current = new ArrayList<>(listas.getValue());
        current.add(new Lista(nombreLista));
        listas.setValue(current);
    }
    public void agregarItem(int posicionLista, Item item) {
        List<Lista> current = new ArrayList<>(listas.getValue());

        if (current.isEmpty()) {
            current.add(new Lista("Lista Principal"));
        }
        int pos = (posicionLista >= 0 && posicionLista < current.size()) ? posicionLista : 0;
        Lista listaSeleccionada = current.get(pos);

        List<Item> nuevosItems = new ArrayList<>(listaSeleccionada.getItems());
        nuevosItems.add(item);
        listaSeleccionada.setItems(nuevosItems);
        current.set(pos, listaSeleccionada);
        listas.setValue(current);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", item.getNombre());
        data.put("detalle", item.getDetalle());
        data.put("monto", item.getMonto());
        data.put("cancelado", item.isCancelado());
        data.put("fecha", item.getFecha());

        db.collection("Lista")
                .add(data)
                .addOnSuccessListener(docRef ->
                        Log.d("Firestore", "Item guardado con ID: " + docRef.getId())
                )
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error guardando item", e)
                );
    }
    public void cargarDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Lista")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Error escuchando cambios", error);
                        return;
                    }
                    if (querySnapshot != null) {
                        List<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String id = doc.getId();
                            String nombre = doc.getString("nombre");
                            String detalle = doc.getString("detalle");
                            Double monto = doc.getDouble("monto");
                            Boolean cancelado = doc.getBoolean("cancelado");
                            String fecha = doc.getString("fecha");
                            if (nombre != null && detalle != null && monto != null && cancelado != null) {
                                Item item = new Item(id, nombre, detalle, monto, cancelado, fecha);
                                items.add(item);
                            }
                        }
                        List<Item> itemsNoCancelados = new ArrayList<>();
                        for (Item it : items) {
                            if (!it.isCancelado()) {
                                itemsNoCancelados.add(it);
                            }
                        }
                        Lista listaPrincipal = new Lista("Lista Principal");
                        listaPrincipal.setItems(itemsNoCancelados);
                        listas.setValue(List.of(listaPrincipal));
                    }
                });
    }
    public void actualizarItem(int posicionLista, int posicionItem, Item itemActualizado) {
        List<Lista> current = new ArrayList<>(listas.getValue());
        if (current.isEmpty()) return;
        Lista lista = current.get(Math.max(0, Math.min(posicionLista, current.size()-1)));
        List<Item> items = new ArrayList<>(lista.getItems());
        if (posicionItem < 0 || posicionItem >= items.size()) return;
        items.set(posicionItem, itemActualizado);
        lista.setItems(items);
        current.set(Math.max(0, posicionLista), lista);
        listas.setValue(current);
        if (itemActualizado.getIdDocumento() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Lista")
                    .document(itemActualizado.getIdDocumento())
                    .update("cancelado", itemActualizado.isCancelado())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Estado actualizado"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar estado", e));
        }
    }
}
