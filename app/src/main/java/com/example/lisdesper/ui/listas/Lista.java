package com.example.lisdesper.ui.listas;

import java.util.ArrayList;
import java.util.List;

public class Lista {
    private String nombre;
    private List<Item> items = new ArrayList<>();
    public Lista(String nombre) {
        this.nombre = nombre;
    }
    public Lista(String nombre, List<Item> items) {
        this.nombre = nombre;
        this.items = items;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }
}
