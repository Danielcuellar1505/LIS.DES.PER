package com.example.lisdesper.ui.listas;

import java.util.ArrayList;
import java.util.List;

public class Lista {
    private String nombre;
    private List<Item> items;

    public Lista(String nombre) {
        this.nombre = nombre;
        this.items = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void agregarItem(Item item) {
        items.add(item);
    }
}
