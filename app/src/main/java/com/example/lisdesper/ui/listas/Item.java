package com.example.lisdesper.ui.listas;

public class Item {
    private String nombre;
    private double monto;
    private boolean checked;

    public Item(String nombre, double monto) {
        this.nombre = nombre;
        this.monto = monto;
        this.checked = false;
    }

    public String getNombre() {
        return nombre;
    }

    public double getMonto() {
        return monto;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
