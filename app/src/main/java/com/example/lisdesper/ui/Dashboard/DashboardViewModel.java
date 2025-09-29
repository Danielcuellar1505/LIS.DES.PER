package com.example.lisdesper.ui.Dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lisdesper.firebase.CBaseDatos;
import com.example.lisdesper.ui.deudores.ItemDeudores;
import com.example.lisdesper.ui.acreedores.ItemAcreedores;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<PieChartData> deudoresChartData;
    private final MutableLiveData<List<ItemDeudores>> todayDeudoresItems;
    private final MutableLiveData<PieChartData> acreedoresChartData;
    private final MutableLiveData<List<ItemAcreedores>> todayAcreedoresItems;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;
    private final CBaseDatos db;
    private ListenerRegistration deudoresListener;
    private ListenerRegistration acreedoresListener;
    private int loadCounter = 0;
    private String selectedDateFilter = null;

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
        deudoresChartData = new MutableLiveData<>();
        todayDeudoresItems = new MutableLiveData<>(new ArrayList<>());
        acreedoresChartData = new MutableLiveData<>();
        todayAcreedoresItems = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(true);
        errorMessage = new MutableLiveData<>();
        db = CBaseDatos.getInstance();
        cargarDatos();
    }

    public void filterByDate(String fecha) {
        selectedDateFilter = fecha;
        cargarDatos();
    }
    public void clearDateFilter() {
        selectedDateFilter = null;
        cargarDatos();
    }
    private void cargarDatos() {
        isLoading.setValue(true);
        loadCounter = 0;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = monthFormat.format(calendar.getTime());

        db.obtenerDeudoresPrincipal((deudorId, e) -> {
            if (e != null || deudorId == null) {
                errorMessage.postValue("Error al cargar deudores: " + (e != null ? e.getMessage() : "ID nulo"));
                deudoresChartData.postValue(new PieChartData(0, 0));
                todayDeudoresItems.postValue(new ArrayList<>());
                checkLoadingComplete();
                return;
            }
            deudoresListener = db.deudoresCollection.document(deudorId).collection("items")
                    .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                        if (error != null || querySnapshot == null) {
                            errorMessage.postValue("Error al cargar ítems de deudores: " + (error != null ? error.getMessage() : "Datos nulos"));
                            deudoresChartData.postValue(new PieChartData(0, 0));
                            todayDeudoresItems.postValue(new ArrayList<>());
                            checkLoadingComplete();
                            return;
                        }
                        List<ItemDeudores> itemDeudores = new ArrayList<>();
                        List<ItemDeudores> filteredDeudoresItems = new ArrayList<>();
                        String filterDate = selectedDateFilter != null ? selectedDateFilter : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        int canceladosMes = 0;
                        int noCanceladosMes = 0;

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
                            if (fechaStr != null && fechaStr.equals(filterDate)) {
                                filteredDeudoresItems.add(item);
                            }

                            if (fechaStr != null && fechaStr.startsWith(currentMonth)) {
                                if (item.isCancelado()) {
                                    canceladosMes++;
                                } else {
                                    noCanceladosMes++;
                                }
                            }
                        }

                        deudoresChartData.postValue(new PieChartData(canceladosMes, noCanceladosMes));
                        todayDeudoresItems.postValue(filteredDeudoresItems);
                        checkLoadingComplete();
                    });
        });

        db.obtenerAcreedoresPrincipal((acreedorId, e) -> {
            if (e != null || acreedorId == null) {
                errorMessage.postValue("Error al cargar acreedores: " + (e != null ? e.getMessage() : "ID nulo"));
                acreedoresChartData.postValue(new PieChartData(0, 0));
                todayAcreedoresItems.postValue(new ArrayList<>());
                checkLoadingComplete();
                return;
            }
            acreedoresListener = db.deudoresCollection.document(acreedorId).collection("items")
                    .addSnapshotListener((QuerySnapshot querySnapshot, FirebaseFirestoreException error) -> {
                        if (error != null || querySnapshot == null) {
                            errorMessage.postValue("Error al cargar ítems de acreedores: " + (error != null ? error.getMessage() : "Datos nulos"));
                            acreedoresChartData.postValue(new PieChartData(0, 0));
                            todayAcreedoresItems.postValue(new ArrayList<>());
                            checkLoadingComplete();
                            return;
                        }
                        List<ItemAcreedores> itemAcreedores = new ArrayList<>();
                        List<ItemAcreedores> filteredAcreedoresItems = new ArrayList<>();
                        String filterDate = selectedDateFilter != null ? selectedDateFilter : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        int canceladosMes = 0;
                        int noCanceladosMes = 0;

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            double monto = doc.getDouble("monto") != null ? doc.getDouble("monto") : 0.0;
                            ItemAcreedores item = new ItemAcreedores(
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
                            itemAcreedores.add(item);
                            if (fechaStr != null && fechaStr.equals(filterDate)) {
                                filteredAcreedoresItems.add(item);
                            }

                            if (fechaStr != null && fechaStr.startsWith(currentMonth)) {
                                if (item.isCancelado()) {
                                    canceladosMes++;
                                } else {
                                    noCanceladosMes++;
                                }
                            }
                        }

                        acreedoresChartData.postValue(new PieChartData(canceladosMes, noCanceladosMes));
                        todayAcreedoresItems.postValue(filteredAcreedoresItems);
                        checkLoadingComplete();
                    });
        });
    }

    private void checkLoadingComplete() {
        loadCounter++;
        if (loadCounter >= 2) {
            isLoading.postValue(false);
        }
    }
    public LiveData<PieChartData> getDeudoresChartData() {
        return deudoresChartData;
    }
    public LiveData<List<ItemDeudores>> getTodayDeudoresItems() {
        return todayDeudoresItems;
    }
    public LiveData<PieChartData> getAcreedoresChartData() {
        return acreedoresChartData;
    }
    public LiveData<List<ItemAcreedores>> getTodayAcreedoresItems() {
        return todayAcreedoresItems;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (deudoresListener != null) {
            deudoresListener.remove();
        }
        if (acreedoresListener != null) {
            acreedoresListener.remove();
        }
    }
}