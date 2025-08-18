package com.example.lisdesper.ui.inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lisdesper.firebase.CBaseDatos;
import com.example.lisdesper.ui.listas.ItemLista;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InicioViewModel extends ViewModel {
    private final MutableLiveData<PieChartData> chartData;
    private final MutableLiveData<List<ItemLista>> todayItems;
    private final MutableLiveData<Boolean> isLoading;
    private final CBaseDatos db;
    private ListenerRegistration itemsListener;

    public static class PieChartData {
        private final int cancelados;
        private final int noCancelados;

        public PieChartData(int cancelados, int noCancelados) {
            this.cancelados = cancelados;
            this.noCancelados = noCancelados;
        }
        public int getCancelados() {
            return cancelados;
        }
        public int getNoCancelados() {
            return noCancelados;
        }
    }
    public InicioViewModel() {
        chartData = new MutableLiveData<>();
        todayItems = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(true);
        db = CBaseDatos.getInstance();
        cargarDatos();
    }
    private void cargarDatos() {
        isLoading.setValue(true);
        db.obtenerListaPrincipal((listaId, e) -> {
            if (e != null || listaId == null) {
                isLoading.postValue(false);
                chartData.postValue(new PieChartData(0, 0));
                todayItems.postValue(new ArrayList<>());
                return;
            }
            itemsListener = db.listasCollection.document(listaId).collection("items")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null || querySnapshot == null) {
                            isLoading.postValue(false);
                            chartData.postValue(new PieChartData(0, 0));
                            todayItems.postValue(new ArrayList<>());
                            return;
                        }
                        List<ItemLista> itemListas = new ArrayList<>();
                        List<ItemLista> todayItemListas = new ArrayList<>();
                        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

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
                            itemListas.add(itemLista);

                            if (fechaStr != null && fechaStr.equals(todayDate)) {
                                todayItemListas.add(itemLista);
                            }
                        }

                        int cancelados = 0;
                        int noCancelados = 0;
                        for (ItemLista item : itemListas) {
                            if (item.isCancelado()) {
                                cancelados++;
                            } else {
                                noCancelados++;
                            }
                        }

                        chartData.postValue(new PieChartData(cancelados, noCancelados));
                        todayItems.postValue(todayItemListas);
                        isLoading.postValue(false);
                    });
        });
    }
    public LiveData<PieChartData> getChartData() {
        return chartData;
    }
    public LiveData<List<ItemLista>> getTodayItems() {
        return todayItems;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (itemsListener != null) {
            itemsListener.remove();
        }
    }
}