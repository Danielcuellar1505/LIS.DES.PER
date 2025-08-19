package com.example.lisdesper.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.lisdesper.ui.listas.ItemLista;

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

    public void agregarItem(String listaId, ItemLista itemLista, OnCompleteListener<ItemLista> listener) {
        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("nombre", itemLista.getNombre());
        itemData.put("telefono", itemLista.getTelefono()); // Nuevo campo
        itemData.put("detalle", itemLista.getDetalle());
        itemData.put("monto", itemLista.getMonto());
        itemData.put("cancelado", itemLista.isCancelado());
        itemData.put("fecha", fechaStr);

        if (listaId == null || listaId.isEmpty()) {
            Map<String, Object> nuevaLista = new HashMap<>();
            nuevaLista.put("nombre", "Lista Principal");
            nuevaLista.put("fechaCreacion", FieldValue.serverTimestamp());

            listasCollection.add(nuevaLista)
                    .addOnSuccessListener(documentReference -> {
                        agregarItem(documentReference.getId(), itemLista, listener);
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onComplete(null, e);
                    });
        } else {
            listasCollection.document(listaId).collection("items").add(itemData)
                    .addOnSuccessListener(documentReference -> {
                        itemLista.setId(documentReference.getId());
                        if (listener != null) listener.onComplete(itemLista, null);
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onComplete(null, e);
                    });
        }
    }

    public void actualizarItem(String listaId, String itemId, ItemLista itemLista, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();


        updates.put("nombre", itemLista.getNombre());
        updates.put("telefono", itemLista.getTelefono()); // Nuevo campo
        updates.put("detalle", itemLista.getDetalle());
        updates.put("monto", itemLista.getMonto());
        updates.put("cancelado", itemLista.isCancelado());
        updates.put("fecha", itemLista.getFecha());

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

    private void obtenerItemsDeLista(String listaId, OnCompleteListener<List<ItemLista>> listener) {
        listasCollection.document(listaId).collection("items").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ItemLista> itemListas = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ItemLista itemLista = new ItemLista(
                                document.getId(),
                                document.getString("nombre"),
                                document.getString("telefono"), // Nuevo campo
                                document.getString("detalle"),
                                document.getDouble("monto"),
                                Boolean.TRUE.equals(document.getBoolean("cancelado"))
                        );
                        String fechaStr = document.getString("fecha");
                        if (fechaStr != null) {
                            itemLista.setFecha(fechaStr);
                        }
                        itemListas.add(itemLista);
                    }
                    if (listener != null) listener.onComplete(itemListas, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }

    public interface OnCompleteListener<T> {
        void onComplete(T result, Exception e);
    }
}