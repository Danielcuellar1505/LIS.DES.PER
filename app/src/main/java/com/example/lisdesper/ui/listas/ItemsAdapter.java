package com.example.lisdesper.ui.listas;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(int originalItemIndex, boolean isChecked);
    }
    public interface OnToggleCanceladosListener {
        void onToggleCancelados();
    }
    private List<ListEntry> entries;
    private OnItemCheckedChangeListener checkedListener;
    private OnToggleCanceladosListener toggleListener;
    public ItemsAdapter(List<ListEntry> entries,
                        OnItemCheckedChangeListener checkedListener,
                        OnToggleCanceladosListener toggleListener) {
        this.entries = entries;
        this.checkedListener = checkedListener;
        this.toggleListener = toggleListener;
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
            return new HeaderViewHolder(v, toggleListener);
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
            hv.tvHeaderFecha.setText(entry.getFecha());
            String texto = hv.mostrarCancelados ? "Ocultar" : "Mostrar";
            hv.btnToggleCancelados.setText(texto);

            int drawableRes = hv.mostrarCancelados ? R.drawable.ic_close_eye : R.drawable.ic_open_eye;
            Drawable icon = ContextCompat.getDrawable(hv.itemView.getContext(), drawableRes);
            int size = (int) hv.btnToggleCancelados.getTextSize(); // tamaño en px igual a texto
            icon.setBounds(0, 0, size, size);

            hv.btnToggleCancelados.setCompoundDrawables(icon, null, null, null);

        }
        else {
            ItemViewHolder iv = (ItemViewHolder) holder;
            Item item = entry.getItem();
            iv.tvNombre.setText(item.getNombre());
            iv.tvDetalle.setText(item.getDetalle());
            iv.tvMonto.setText(String.format("%.2f", item.getMonto()));
            iv.cbCancelado.setOnCheckedChangeListener(null);
            iv.cbCancelado.setChecked(item.isCancelado());
            iv.cbCancelado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setCancelado(isChecked);
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
        Button btnToggleCancelados;
        boolean mostrarCancelados = false;
        HeaderViewHolder(@NonNull View itemView, OnToggleCanceladosListener toggleListener) {
            super(itemView);
            tvHeaderFecha = itemView.findViewById(R.id.tvHeaderFecha);
            btnToggleCancelados = itemView.findViewById(R.id.btnToggleCancelados);
            btnToggleCancelados.setOnClickListener(v -> {
                mostrarCancelados = !mostrarCancelados;
                btnToggleCancelados.setText(mostrarCancelados ? "Ocultar" : "Mostrar");
                if (toggleListener != null) {
                    toggleListener.onToggleCancelados();
                }
            });
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
