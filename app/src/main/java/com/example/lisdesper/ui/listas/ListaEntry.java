package com.example.lisdesper.ui.listas;

public class ListaEntry {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String fecha;
    private ItemLista itemLista;
    private int originalIndex;
    private ListaEntry(int type) {
        this.type = type;
    }
    public static ListaEntry header(String fecha) {
        ListaEntry e = new ListaEntry(TYPE_HEADER);
        e.fecha = fecha;
        return e;
    }
    public static ListaEntry item(ItemLista itemLista, int originalIndex) {
        ListaEntry e = new ListaEntry(TYPE_ITEM);
        e.itemLista = itemLista;
        e.originalIndex = originalIndex;
        return e;
    }
    public int getType() { return type; }
    public String getFecha() { return fecha; }
    public ItemLista getItem() { return itemLista; }
    public int getOriginalIndex() { return originalIndex; }
}
