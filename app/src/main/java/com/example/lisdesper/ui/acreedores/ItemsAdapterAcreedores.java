package com.example.lisdesper.ui.acreedores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapterAcreedores extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(int originalItemIndex, boolean isChecked, ItemAcreedores item);
    }
    public interface OnItemClickListener {
        void onItemClick(int originalItemIndex, ItemAcreedores item);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int originalItemIndex, ItemAcreedores item);
    }
    private List<AcreedoresEntry> entries;
    private OnItemCheckedChangeListener checkedListener;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public ItemsAdapterAcreedores(List<AcreedoresEntry> entries,
                                  OnItemCheckedChangeListener checkedListener,
                                  OnItemClickListener clickListener,
                                  OnItemLongClickListener longClickListener) {
        this.entries = entries != null ? entries : new ArrayList<>();
        this.checkedListener = checkedListener;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void setItems(List<AcreedoresEntry> nuevos) {
        this.entries = nuevos != null ? nuevos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return entries.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AcreedoresEntry.TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deudores_header_row, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deudores_item_row, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AcreedoresEntry entry = entries.get(position);
        if (entry.getType() == AcreedoresEntry.TYPE_HEADER) {
            HeaderViewHolder hv = (HeaderViewHolder) holder;
            hv.tvHeaderFecha.setText(entry.getFecha());
            hv.tvHeaderFecha.setContentDescription("Fecha: " + entry.getFecha());
        } else {
            ItemViewHolder iv = (ItemViewHolder) holder;
            ItemAcreedores itemAcreedores = entry.getItem();

            iv.tvNombre.setText(itemAcreedores.getNombre());
            iv.tvDetalle.setText(itemAcreedores.getDetalle());
            iv.tvMonto.setText(String.format("%.2f", itemAcreedores.getMonto()));
            iv.cbCancelado.setOnCheckedChangeListener(null);
            iv.cbCancelado.setChecked(itemAcreedores.isCancelado());
            iv.cbCancelado.setContentDescription(itemAcreedores.isCancelado() ? "Desmarcar cancelado" : "Marcar como cancelado");
            iv.cbCancelado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                itemAcreedores.setCancelado(isChecked);
                if (checkedListener != null) {
                    checkedListener.onItemCheckedChanged(entry.getOriginalIndex(), isChecked, itemAcreedores);
                }
            });
            iv.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(entry.getOriginalIndex(), itemAcreedores);
                }
            });
            iv.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(entry.getOriginalIndex(), itemAcreedores);
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
        TextView hNombre, hDetalle, hMonto, hCancelado;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderFecha = itemView.findViewById(R.id.tvHeaderFecha);
            hNombre = itemView.findViewById(R.id.hNombre);
            hDetalle = itemView.findViewById(R.id.hDetalle);
            hMonto = itemView.findViewById(R.id.hMonto);
            hCancelado = itemView.findViewById(R.id.hCancelado);
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