package com.example.lisdesper.ui.deudores;

import java.util.ArrayList;
import java.util.List;

public class Deudores {
    private String nombre;
    private List<ItemDeudores> itemDeudores = new ArrayList<>();
    public Deudores(String nombre) {
        this.nombre = nombre;
    }
    public List<ItemDeudores> getItems() {
        return itemDeudores;
    }
    public void setItems(List<ItemDeudores> itemDeudores) {
        this.itemDeudores = itemDeudores;
    }
}