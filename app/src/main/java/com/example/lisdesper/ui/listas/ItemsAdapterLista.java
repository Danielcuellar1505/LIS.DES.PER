package com.example.lisdesper.ui.listas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;

import java.util.List;

public class ItemsAdapterLista extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(int originalItemIndex, boolean isChecked);
    }
    private List<ListaEntry> entries;
    private OnItemCheckedChangeListener checkedListener;
    public ItemsAdapterLista(List<ListaEntry> entries, OnItemCheckedChangeListener checkedListener) {
        this.entries = entries;
        this.checkedListener = checkedListener;
    }
    public void setItems(List<ListaEntry> nuevos) {
        this.entries = nuevos;
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        return entries.get(position).getType();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListaEntry.TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_header_row, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_row, parent, false);
            return new ItemViewHolder(v);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListaEntry entry = entries.get(position);
        if (entry.getType() == ListaEntry.TYPE_HEADER) {
            HeaderViewHolder hv = (HeaderViewHolder) holder;
            hv.tvHeaderFecha.setText(entry.getFecha());
        } else {
            ItemViewHolder iv = (ItemViewHolder) holder;
            ItemLista itemLista = entry.getItem();

            iv.tvNombre.setText(itemLista.getNombre());
            iv.tvDetalle.setText(itemLista.getDetalle());
            iv.tvMonto.setText(String.format("%.2f", itemLista.getMonto()));
            iv.cbCancelado.setOnCheckedChangeListener(null);
            iv.cbCancelado.setChecked(itemLista.isCancelado());
            iv.cbCancelado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                itemLista.setCancelado(isChecked);
                if (checkedListener != null) {
                    checkedListener.onItemCheckedChanged(entry.getOriginalIndex(), isChecked);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeaderFecha;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderFecha = itemView.findViewById(R.id.tvHeaderFecha);
        }
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
