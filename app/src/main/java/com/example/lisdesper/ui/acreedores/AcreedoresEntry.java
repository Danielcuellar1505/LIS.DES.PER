package com.example.lisdesper.ui.acreedores;

public class AcreedoresEntry {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String fecha;
    private ItemAcreedores itemAcreedores;
    private int originalIndex;

    private AcreedoresEntry(int type) {
        this.type = type;
    }

    public static AcreedoresEntry header(String fecha) {
        AcreedoresEntry e = new AcreedoresEntry(TYPE_HEADER);
        e.fecha = fecha;
        return e;
    }

    public static AcreedoresEntry item(ItemAcreedores itemAcreedores, int originalIndex) {
        AcreedoresEntry e = new AcreedoresEntry(TYPE_ITEM);
        e.itemAcreedores = itemAcreedores;
        e.originalIndex = originalIndex;
        return e;
    }

    public int getType() { return type; }
    public String getFecha() { return fecha; }
    public ItemAcreedores getItem() { return itemAcreedores; }
    public int getOriginalIndex() { return originalIndex; }
}