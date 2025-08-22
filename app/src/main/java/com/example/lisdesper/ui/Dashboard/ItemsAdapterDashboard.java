package com.example.lisdesper.ui.Dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;
import com.example.lisdesper.ui.deudores.ItemDeudores;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapterDashboard extends RecyclerView.Adapter<ItemsAdapterDashboard.ItemViewHolder> {
    private List<ItemDeudores> items;
    public ItemsAdapterDashboard(List<ItemDeudores> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
    public void setItems(List<ItemDeudores> nuevos) {
        this.items = nuevos != null ? nuevos : new ArrayList<>();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deudores_item_row, parent, false);
        return new ItemViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemDeudores itemDeudores = items.get(position);
        holder.tvNombre.setText(itemDeudores.getNombre());
        holder.tvDetalle.setText(itemDeudores.getDetalle());
        holder.tvMonto.setText(String.format("%.2f", itemDeudores.getMonto()));
        holder.cbCancelado.setChecked(itemDeudores.isCancelado());
        holder.cbCancelado.setEnabled(false);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalle, tvMonto;
        CheckBox cbCancelado;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDetalle = itemView.findViewById(R.id.tvDetalle);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            cbCancelado = itemView.findViewById(R.id.cbCancelado);
        }
    }
}
