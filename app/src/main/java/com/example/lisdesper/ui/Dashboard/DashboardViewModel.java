package com.example.lisdesper.ui.Dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lisdesper.firebase.CBaseDatos;
import com.example.lisdesper.ui.deudores.ItemDeudores;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<PieChartData> chartData;
    private final MutableLiveData<List<ItemDeudores>> todayItems;
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
    public DashboardViewModel() {
        chartData = new MutableLiveData<>();
        todayItems = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(true);
        db = CBaseDatos.getInstance();
        cargarDatos();
    }
    private void cargarDatos() {
        isLoading.setValue(true);
        db.obtenerDeudoresPrincipal((deudorId, e) -> {
            if (e != null || deudorId == null) {
                isLoading.postValue(false);
                chartData.postValue(new PieChartData(0, 0));
                todayItems.postValue(new ArrayList<>());
                return;
            }
            itemsListener = db.deudoresCollection.document(deudorId).collection("items")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null || querySnapshot == null) {
                            isLoading.postValue(false);
                            chartData.postValue(new PieChartData(0, 0));
                            todayItems.postValue(new ArrayList<>());
                            return;
                        }
                        List<ItemDeudores> itemDeudores = new ArrayList<>();
                        List<ItemDeudores> todayItemDeudores = new ArrayList<>();
                        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            double monto = doc.getDouble("monto") != null ? doc.getDouble("monto") : 0.0;
                            ItemDeudores item = new ItemDeudores(
                                    doc.getId(),
                                    doc.getString("nombre"),
                                    doc.getString("detalle"),
                                    monto,
                                    Boolean.TRUE.equals(doc.getBoolean("cancelado"))
                            );

                            String fechaStr = doc.getString("fecha");
                            if (fechaStr != null) {
                                item.setFecha(fechaStr);
                            }
                            itemDeudores.add(item);

                            if (fechaStr != null && fechaStr.equals(todayDate)) {
                                todayItemDeudores.add(item);
                            }
                        }

                        int cancelados = 0;
                        int noCancelados = 0;
                        for (ItemDeudores item : itemDeudores) {
                            if (item.isCancelado()) {
                                cancelados++;
                            } else {
                                noCancelados++;
                            }
                        }

                        chartData.postValue(new PieChartData(cancelados, noCancelados));
                        todayItems.postValue(todayItemDeudores);
                        isLoading.postValue(false);
                    });
        });
    }
    public LiveData<PieChartData> getChartData() {
        return chartData;
    }
    public LiveData<List<ItemDeudores>> getTodayItems() {
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