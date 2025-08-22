package com.example.lisdesper.ui.acreedores;

import java.util.ArrayList;
import java.util.List;

public class Acreedores {
    private String nombre;
    private List<ItemAcreedores> itemAcreedores = new ArrayList<>();

    public Acreedores(String nombre) {
        this.nombre = nombre;
    }

    public List<ItemAcreedores> getItems() {
        return itemAcreedores;
    }

    public void setItems(List<ItemAcreedores> itemAcreedores) {
        this.itemAcreedores = itemAcreedores;
    }
}