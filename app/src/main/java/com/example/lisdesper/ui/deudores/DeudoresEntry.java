package com.example.lisdesper.ui.deudores;

public class DeudoresEntry {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String fecha;
    private ItemDeudores itemDeudores;
    private int originalIndex;
    private DeudoresEntry(int type) {
        this.type = type;
    }
    public static DeudoresEntry header(String fecha) {
        DeudoresEntry e = new DeudoresEntry(TYPE_HEADER);
        e.fecha = fecha;
        return e;
    }
    public static DeudoresEntry item(ItemDeudores itemDeudores, int originalIndex) {
        DeudoresEntry e = new DeudoresEntry(TYPE_ITEM);
        e.itemDeudores = itemDeudores;
        e.originalIndex = originalIndex;
        return e;
    }
    public int getType() { return type; }
    public String getFecha() { return fecha; }
    public ItemDeudores getItem() { return itemDeudores; }
    public int getOriginalIndex() { return originalIndex; }
}