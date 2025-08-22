package com.example.lisdesper.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.lisdesper.ui.deudores.ItemDeudores;

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
    public final CollectionReference deudoresCollection;

    private CBaseDatos() {
        db = FirebaseFirestore.getInstance();
        deudoresCollection = db.collection("BD_LIS_DES_PER");
    }

    public static synchronized CBaseDatos getInstance() {
        if (instance == null) {
            instance = new CBaseDatos();
        }
        return instance;
    }

    public void agregarItem(String deudorId, ItemDeudores itemDeudores, OnCompleteListener<ItemDeudores> listener) {
        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("nombre", itemDeudores.getNombre());
        itemData.put("detalle", itemDeudores.getDetalle());
        itemData.put("monto", itemDeudores.getMonto());
        itemData.put("cancelado", itemDeudores.isCancelado());
        itemData.put("fecha", fechaStr);

        if (deudorId == null || deudorId.isEmpty()) {
            Map<String, Object> nuevaDeudor = new HashMap<>();
            nuevaDeudor.put("nombre", "Deudores Principal");
            nuevaDeudor.put("fechaCreacion", FieldValue.serverTimestamp());

            deudoresCollection.add(nuevaDeudor)
                    .addOnSuccessListener(documentReference -> {
                        agregarItem(documentReference.getId(), itemDeudores, listener);
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onComplete(null, e);
                    });
        } else {
            deudoresCollection.document(deudorId).collection("items").add(itemData)
                    .addOnSuccessListener(documentReference -> {
                        itemDeudores.setId(documentReference.getId());
                        if (listener != null) listener.onComplete(itemDeudores, null);
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onComplete(null, e);
                    });
        }
    }

    public void actualizarItem(String deudorId, String itemId, ItemDeudores itemDeudores, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", itemDeudores.getNombre());
        updates.put("detalle", itemDeudores.getDetalle());
        updates.put("monto", itemDeudores.getMonto());
        updates.put("cancelado", itemDeudores.isCancelado());
        updates.put("fecha", itemDeudores.getFecha());

        deudoresCollection.document(deudorId).collection("items").document(itemId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onComplete(null, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }

    public void obtenerDeudoresPrincipal(OnCompleteListener<String> listener) {
        deudoresCollection.limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        if (listener != null) listener.onComplete(document.getId(), null);
                    } else {
                        Map<String, Object> nuevoDeudor = new HashMap<>();
                        nuevoDeudor.put("nombre", "Deudores Principal");
                        nuevoDeudor.put("fechaCreacion", FieldValue.serverTimestamp());

                        deudoresCollection.add(nuevoDeudor)
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

    private void obtenerItemsDeDeudores(String deudorId, OnCompleteListener<List<ItemDeudores>> listener) {
        deudoresCollection.document(deudorId).collection("items").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ItemDeudores> listaDeudores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ItemDeudores item = new ItemDeudores(
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
                        listaDeudores.add(item);
                    }
                    if (listener != null) listener.onComplete(listaDeudores, null);

                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }

    public interface OnCompleteListener<T> {
        void onComplete(T result, Exception e);
    }
}