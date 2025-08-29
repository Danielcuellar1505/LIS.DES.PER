package com.example.lisdesper.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.lisdesper.ui.deudores.ItemDeudores;
import com.example.lisdesper.ui.acreedores.ItemAcreedores;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void agregarItemAcreedor(String acreedorId, ItemAcreedores itemAcreedores, OnCompleteListener<ItemAcreedores> listener) {
        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("nombre", itemAcreedores.getNombre());
        itemData.put("detalle", itemAcreedores.getDetalle());
        itemData.put("monto", itemAcreedores.getMonto());
        itemData.put("cancelado", itemAcreedores.isCancelado());
        itemData.put("fecha", fechaStr);

        if (acreedorId == null || acreedorId.isEmpty()) {
            Map<String, Object> nuevoAcreedor = new HashMap<>();
            nuevoAcreedor.put("nombre", "Acreedores Principal");

            deudoresCollection.add(nuevoAcreedor)
                    .addOnSuccessListener(documentReference -> {
                        agregarItemAcreedor(documentReference.getId(), itemAcreedores, listener);
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onComplete(null, e);
                    });
        } else {
            deudoresCollection.document(acreedorId).collection("items").add(itemData)
                    .addOnSuccessListener(documentReference -> {
                        itemAcreedores.setId(documentReference.getId());
                        if (listener != null) listener.onComplete(itemAcreedores, null);
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
    public void actualizarItemAcreedor(String acreedorId, String itemId, ItemAcreedores itemAcreedores, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", itemAcreedores.getNombre());
        updates.put("detalle", itemAcreedores.getDetalle());
        updates.put("monto", itemAcreedores.getMonto());
        updates.put("cancelado", itemAcreedores.isCancelado());
        updates.put("fecha", itemAcreedores.getFecha());

        deudoresCollection.document(acreedorId).collection("items").document(itemId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onComplete(null, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }
    public void eliminarItem(String deudorId, String itemId, OnCompleteListener<Void> listener) {
        deudoresCollection.document(deudorId).collection("items").document(itemId).delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onComplete(null, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }
    public void eliminarItemAcreedor(String acreedorId, String itemId, OnCompleteListener<Void> listener) {
        deudoresCollection.document(acreedorId).collection("items").document(itemId).delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onComplete(null, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }
    public void obtenerDeudoresPrincipal(OnCompleteListener<String> listener) {
        deudoresCollection.whereEqualTo("nombre", "Deudores Principal").limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        if (listener != null) listener.onComplete(document.getId(), null);
                    } else {
                        Map<String, Object> nuevoDeudor = new HashMap<>();
                        nuevoDeudor.put("nombre", "Deudores Principal");

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
    public void obtenerAcreedoresPrincipal(OnCompleteListener<String> listener) {
        deudoresCollection.whereEqualTo("nombre", "Acreedores Principal").limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        if (listener != null) listener.onComplete(document.getId(), null);
                    } else {
                        Map<String, Object> nuevoAcreedor = new HashMap<>();
                        nuevoAcreedor.put("nombre", "Acreedores Principal");

                        deudoresCollection.add(nuevoAcreedor)
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
    private void obtenerItemsDeAcreedores(String acreedorId, OnCompleteListener<List<ItemAcreedores>> listener) {
        deudoresCollection.document(acreedorId).collection("items").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ItemAcreedores> listaAcreedores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ItemAcreedores item = new ItemAcreedores(
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
                        listaAcreedores.add(item);
                    }
                    if (listener != null) listener.onComplete(listaAcreedores, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }
    public void buscarItemsDeudoresPorNombre(String deudorId, String query, OnCompleteListener<List<ItemDeudores>> listener) {
        if (query == null || query.isEmpty()) {
            obtenerItemsDeDeudores(deudorId, listener);
            return;
        }
        String queryLower = query.toLowerCase(Locale.getDefault());
        String queryUpper = queryLower + "\uf8ff"; // Carácter Unicode alto para rango
        deudoresCollection.document(deudorId).collection("items")
                .whereGreaterThanOrEqualTo("nombre", queryLower)
                .whereLessThanOrEqualTo("nombre", queryUpper)
                .limit(10)
                .get()
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
    public void buscarItemsAcreedoresPorNombre(String acreedorId, String query, OnCompleteListener<List<ItemAcreedores>> listener) {
        if (query == null || query.isEmpty()) {
            obtenerItemsDeAcreedores(acreedorId, listener);
            return;
        }
        String queryLower = query.toLowerCase(Locale.getDefault());
        String queryUpper = queryLower + "\uf8ff";
        deudoresCollection.document(acreedorId).collection("items")
                .whereGreaterThanOrEqualTo("nombre", queryLower)
                .whereLessThanOrEqualTo("nombre", queryUpper)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ItemAcreedores> listaAcreedores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ItemAcreedores item = new ItemAcreedores(
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
                        listaAcreedores.add(item);
                    }
                    if (listener != null) listener.onComplete(listaAcreedores, null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onComplete(null, e);
                });
    }
    public void obtenerNombresParaAutocompletado(String deudorId, String acreedorId, OnCompleteListener<List<String>> listener) {
        AtomicInteger pendingTasks = new AtomicInteger(2);
        List<String> nombres = Collections.synchronizedList(new ArrayList<>());
        deudoresCollection.document(deudorId).collection("items").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String nombre = document.getString("nombre");
                        if (nombre != null && !nombres.contains(nombre)) {
                            nombres.add(nombre);
                        }
                    }
                    if (pendingTasks.decrementAndGet() == 0) {
                        if (listener != null) listener.onComplete(nombres, null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (pendingTasks.decrementAndGet() == 0) {
                        if (listener != null) listener.onComplete(nombres, e);
                    }
                });
        deudoresCollection.document(acreedorId).collection("items").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String nombre = document.getString("nombre");
                        if (nombre != null && !nombres.contains(nombre)) {
                            nombres.add(nombre);
                        }
                    }
                    if (pendingTasks.decrementAndGet() == 0) {
                        if (listener != null) listener.onComplete(nombres, null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (pendingTasks.decrementAndGet() == 0) {
                        if (listener != null) listener.onComplete(nombres, e);
                    }
                });
    }
    public interface OnCompleteListener<T> {
        void onComplete(T result, Exception e);
    }
}