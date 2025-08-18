package com.example.lisdesper.ui.inicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;
import com.example.lisdesper.ui.listas.ItemLista;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapterInicio extends RecyclerView.Adapter<ItemsAdapterInicio.ItemViewHolder> {
    private List<ItemLista> items;
    public ItemsAdapterInicio(List<ItemLista> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
    public void setItems(List<ItemLista> nuevos) {
        this.items = nuevos != null ? nuevos : new ArrayList<>();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_row, parent, false);
        return new ItemViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemLista itemLista = items.get(position);
        holder.tvNombre.setText(itemLista.getNombre());
        holder.tvDetalle.setText(itemLista.getDetalle());
        holder.tvMonto.setText(String.format("%.2f", itemLista.getMonto()));
        holder.cbCancelado.setChecked(itemLista.isCancelado());
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
