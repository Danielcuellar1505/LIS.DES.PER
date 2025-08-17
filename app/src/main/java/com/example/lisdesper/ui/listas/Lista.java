package com.example.lisdesper.ui.listas;

import java.util.ArrayList;
import java.util.List;

public class Lista {
    private String nombre;
    private List<ItemLista> itemListas = new ArrayList<>();
    public Lista(String nombre) {
        this.nombre = nombre;
    }
    public List<ItemLista> getItems() {
        return itemListas;
    }
    public void setItems(List<ItemLista> itemListas) {
        this.itemListas = itemListas;
    }
}
