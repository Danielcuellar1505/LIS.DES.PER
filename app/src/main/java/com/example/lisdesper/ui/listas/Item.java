package com.example.lisdesper.ui.listas;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item {
    private String idDocumento;
    private String nombre;
    private String detalle;
    private double monto;
    private boolean cancelado;
    private String fecha;

    public Item(String nombre, String detalle, double monto, boolean cancelado) {
        this.nombre = nombre;
        this.detalle = detalle;
        this.monto = monto;
        this.cancelado = cancelado;
        this.fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
    public Item(String idDocumento, String nombre, String detalle, double monto, boolean cancelado, String fecha) {
        this.idDocumento = idDocumento;
        this.nombre = nombre;
        this.detalle = detalle;
        this.monto = monto;
        this.cancelado = cancelado;
        this.fecha = fecha;
    }

    public String getIdDocumento() {
        return idDocumento;
    }
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
    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }
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
