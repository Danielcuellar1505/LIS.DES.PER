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

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemCheckedChangeListener {
        /**
         * originalItemIndex: índice del item en la lista original (no en la lista "aplanada" del adapter)
         */
        void onItemCheckedChanged(int originalItemIndex, boolean isChecked);
    }

    private List<ListEntry> entries;
    private OnItemCheckedChangeListener checkedListener;

    public ItemsAdapter(List<ListEntry> entries, OnItemCheckedChangeListener listener) {
        this.entries = entries;
        this.checkedListener = listener;
    }

    public void setItems(List<ListEntry> nuevos) {
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
        if (viewType == ListEntry.TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_row, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListEntry entry = entries.get(position);
        if (entry.getType() == ListEntry.TYPE_HEADER) {
            HeaderViewHolder hv = (HeaderViewHolder) holder;
            // Aquí puedes formatear la fecha si quieres (p.ej dd/MM/yyyy o "HOY")
            hv.tvHeaderFecha.setText(entry.getFecha());
        } else {
            ItemViewHolder iv = (ItemViewHolder) holder;
            Item item = entry.getItem();

            iv.tvNombre.setText(item.getNombre());
            iv.tvDetalle.setText(item.getDetalle());
            iv.tvMonto.setText(String.format("%.2f", item.getMonto()));

            // Evitamos que el listener se dispare al rebindear la celda
            iv.cbCancelado.setOnCheckedChangeListener(null);
            iv.cbCancelado.setChecked(item.isCancelado());
            iv.cbCancelado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Actualiza el modelo local (UI ya refleja el cambio)
                item.setCancelado(isChecked);

                // Notificamos al listener con el índice ORIGINAL del item (no la posición del adapter)
                if (checkedListener != null) {
                    checkedListener.onItemCheckedChanged(entry.getOriginalIndex(), isChecked);
                }
                // NO llamamos notifyItemChanged(...) aquí para evitar IllegalStateException.
            });
        }
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    /* ViewHolders */

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
