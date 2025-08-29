package com.example.lisdesper.ui.deudores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;

import java.util.List;

public class ItemsAdapterDeudores extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(int originalItemIndex, boolean isChecked, ItemDeudores item);
    }
    public interface OnItemClickListener {
        void onItemClick(int originalItemIndex, ItemDeudores item);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int originalItemIndex, ItemDeudores item);
    }
    private List<DeudoresEntry> entries;
    private OnItemCheckedChangeListener checkedListener;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    public ItemsAdapterDeudores(List<DeudoresEntry> entries, OnItemCheckedChangeListener checkedListener, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.entries = entries;
        this.checkedListener = checkedListener;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }
    public void setItems(List<DeudoresEntry> nuevos) {
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
        if (viewType == DeudoresEntry.TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deudores_header_row, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deudores_item_row, parent, false);
            return new ItemViewHolder(v);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DeudoresEntry entry = entries.get(position);
        if (entry.getType() == DeudoresEntry.TYPE_HEADER) {
            HeaderViewHolder hv = (HeaderViewHolder) holder;
            hv.tvHeaderFecha.setText(entry.getFecha());
        } else {
            ItemViewHolder iv = (ItemViewHolder) holder;
            ItemDeudores itemDeudores = entry.getItem();

            iv.tvNombre.setText(itemDeudores.getNombre());
            iv.tvDetalle.setText(itemDeudores.getDetalle());
            iv.tvMonto.setText(String.format("%.2f", itemDeudores.getMonto()));
            iv.cbCancelado.setOnCheckedChangeListener(null);
            iv.cbCancelado.setChecked(itemDeudores.isCancelado());
            iv.cbCancelado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                itemDeudores.setCancelado(isChecked);
                if (checkedListener != null) {
                    checkedListener.onItemCheckedChanged(entry.getOriginalIndex(), isChecked, itemDeudores);
                }
            });
            iv.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(entry.getOriginalIndex(), itemDeudores);
                }
            });
            iv.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(entry.getOriginalIndex(), itemDeudores);
                }
                return true;
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