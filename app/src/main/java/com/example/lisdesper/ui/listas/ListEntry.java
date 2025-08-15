package com.example.lisdesper.ui.listas;

public class ListEntry {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String fecha;
    private Item item;
    private int originalIndex;

    private ListEntry(int type) {
        this.type = type;
    }

    public static ListEntry header(String fecha) {
        ListEntry e = new ListEntry(TYPE_HEADER);
        e.fecha = fecha;
        return e;
    }

    public static ListEntry item(Item item, int originalIndex) {
        ListEntry e = new ListEntry(TYPE_ITEM);
        e.item = item;
        e.originalIndex = originalIndex;
        return e;
    }

    public int getType() { return type; }
    public String getFecha() { return fecha; }
    public Item getItem() { return item; }
    public int getOriginalIndex() { return originalIndex; }
}
