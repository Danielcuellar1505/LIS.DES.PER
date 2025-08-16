package com.example.lisdesper.ui.listas;

import com.google.firebase.firestore.FieldValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Item {
    private String id;
    private String nombre;
    private String detalle;
    private double monto;
    private boolean cancelado;
    private String fecha;
    public Item(String id, String nombre, String detalle, double monto, boolean cancelado) {
        this.id = id;
        this.nombre = nombre;
        this.detalle = detalle;
        this.monto = monto;
        this.cancelado = cancelado;
        this.fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombre);
        map.put("detalle", detalle);
        map.put("monto", monto);
        map.put("cancelado", cancelado);
        map.put("fecha", FieldValue.serverTimestamp());
        return map;
    }
    public Item(String nombre, String detalle, double monto, boolean cancelado) {
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
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}