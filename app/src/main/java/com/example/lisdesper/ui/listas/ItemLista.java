package com.example.lisdesper.ui.listas;

import com.google.firebase.firestore.FieldValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemLista {
    private String id;
    private String nombre;
    private String detalle;
    private double monto;
    private boolean cancelado;
    private String fecha;
    public ItemLista(String id, String nombre, String detalle, double monto, boolean cancelado) {
        this.id = id;
        this.nombre = nombre;
        this.detalle = detalle;
        this.monto = monto;
        this.cancelado = cancelado;
        this.fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
    public ItemLista(String nombre, String detalle, double monto, boolean cancelado) {
        this("", nombre, detalle, monto, cancelado);
    }
    public String getId() { return id; }
    public String getNombre() {
        return nombre;
    }
    public String getDetalle() {
        return detalle;
    }
    public double getMonto() {
        return monto;
    }
    public boolean isCancelado() {
        return cancelado;
    }
    public String getFecha() {
        return fecha;
    }
    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public void setId(String id) { this.id = id; }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}