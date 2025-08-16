package com.example.lisdesper.firebase;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.lisdesper.ui.listas.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CBaseDatos {
    private static CBaseDatos instance;
    private final FirebaseFirestore db;
    public final CollectionReference listasCollection;
    private CBaseDatos() {
        db = FirebaseFirestore.getInstance();
        listasCollection = db.collection("BD_LIS_DES_PER");
    }
    public static synchronized CBaseDatos getInstance() {
        if (instance == null) {
            instance = new CBaseDatos();
        }
        return instance;
    }
    public void agregarItem(String listaId, Item item, OnCompleteListener<Item> listener) {
        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("nombre", item.getNombre());
        itemData.put("detalle", item.getDetalle());
        itemData.put("monto", item.getMonto());
        itemData.put("cancelado", item.isCancelado());
        itemData.put("fecha", fechaStr);

        if (listaId == null || listaId.isEmpty()) {
            Map<String, Object> nuevaLista = new HashMap<>();
            nuevaLista.put("nombre", "Lista Principal");
            nuevaLista.put("fechaCreacion", FieldValue.serverTimestamp());

            listasCollection.add(nuevaLista)
                    .addOnSuccessListener(documentReference -> {
                        agregarItem(documentReference.getId(), item, listener);
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onComplete(null, e);
                    });
        } else {
            listasCollection.document(listaId).collection("items").add(itemData)
                    .addOnSuccessListener(documentReference -> {
                        item.setId(documentReference.getId());
                        if (listener != null) listener.onComplete(item, null);
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onComplete(null, e);
                    });
        }
    }
    public void actualizarItem(String listaId, String itemId, Item item, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", item.getNombre());
        updates.put("detalle", item.getDetalle());
        updates.put("monto", item.getMonto());
        updates.put("cancelado", item.isCancelado());
        updates.put("fecha", item.getFecha());

        listasCollection.document(listaId).collection("items").document(itemId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onComplete(null, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }
    public void obtenerListaPrincipal(OnCompleteListener<String> listener) {
        listasCollection.limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        if (listener != null) listener.onComplete(document.getId(), null);
                    } else {
                        Map<String, Object> nuevaLista = new HashMap<>();
                        nuevaLista.put("nombre", "Lista Principal");
                        nuevaLista.put("fechaCreacion", FieldValue.serverTimestamp());

                        listasCollection.add(nuevaLista)
                                .addOnSuccessListener(documentReference -> {
                                    if (listener != null) listener.onComplete(documentReference.getId(), null);
                                })
                                .addOnFailureListener(e -> {
                                    if (listener != null) listener.onComplete(null, e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }
    private void obtenerItemsDeLista(String listaId, OnCompleteListener<List<Item>> listener) {
        listasCollection.document(listaId).collection("items").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Item> items = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Item item = new Item(
                                document.getId(),
                                document.getString("nombre"),
                                document.getString("detalle"),
                                document.getDouble("monto"),
                                Boolean.TRUE.equals(document.getBoolean("cancelado"))
                        );
                        String fechaStr = document.getString("fecha");
                        if (fechaStr != null) {
                            item.setFecha(fechaStr);
                        }
                        items.add(item);
                    }
                    if (listener != null) listener.onComplete(items, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }
    public interface OnCompleteListener<T> {
        void onComplete(T result, Exception e);
    }
}