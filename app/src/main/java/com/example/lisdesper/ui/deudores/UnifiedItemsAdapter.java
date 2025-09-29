package com.example.lisdesper.ui.deudores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;

import java.util.List;
import java.util.Locale;

public class UnifiedItemsAdapter extends RecyclerView.Adapter<UnifiedItemsAdapter.UnifiedItemViewHolder> {
    private List<UnifiedItem> unifiedItems;

    public UnifiedItemsAdapter(List<UnifiedItem> unifiedItems) {
        this.unifiedItems = unifiedItems;
    }

    @NonNull
    @Override
    public UnifiedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deudores_unified_item_row, parent, false);
        return new UnifiedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnifiedItemViewHolder holder, int position) {
        UnifiedItem item = unifiedItems.get(position);
        holder.tvCantidad.setText(String.valueOf(item.getCantidad()));
        holder.tvDetalle.setText(item.getDetalle());
        holder.tvMontoTotal.setText(String.format(Locale.getDefault(), "%.2f", item.getMontoTotal()));
    }

    @Override
    public int getItemCount() {
        return unifiedItems != null ? unifiedItems.size() : 0;
    }

    static class UnifiedItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvCantidad, tvDetalle, tvMontoTotal;

        UnifiedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvDetalle = itemView.findViewById(R.id.tvDetalle);
            tvMontoTotal = itemView.findViewById(R.id.tvMontoTotal);
        }
    }

    public static class UnifiedItem {
        public int cantidad;
        private String detalle;
        public double montoTotal;

        public UnifiedItem(int cantidad, String detalle, double montoTotal) {
            this.cantidad = cantidad;
            this.detalle = detalle;
            this.montoTotal = montoTotal;
        }

        public int getCantidad() { return cantidad; }
        public String getDetalle() { return detalle; }
        public double getMontoTotal() { return montoTotal; }
    }
}