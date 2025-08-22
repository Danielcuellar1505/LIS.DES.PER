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
import com.example.lisdesper.ui.acreedores.ItemAcreedores;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemsAdapterDashboard extends RecyclerView.Adapter<ItemsAdapterDashboard.ItemViewHolder> {
    public static class DashboardItem {
        private final Object item;
        private final boolean isDeudor;

        public DashboardItem(Object item, boolean isDeudor) {
            this.item = item;
            this.isDeudor = isDeudor;
        }

        public Object getItem() {
            return item;
        }

        public boolean isDeudor() {
            return isDeudor;
        }
    }

    private List<DashboardItem> items;

    public ItemsAdapterDashboard(List<?> items) {
        this.items = new ArrayList<>();
        setItems(items);
    }

    public void setItems(List<?> nuevos) {
        this.items.clear();
        if (nuevos != null) {
            for (Object item : nuevos) {
                boolean isDeudor = item instanceof ItemDeudores;
                this.items.add(new DashboardItem(item, isDeudor));
            }
        }
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
        DashboardItem dashboardItem = items.get(position);
        Object item = dashboardItem.getItem();
        if (dashboardItem.isDeudor()) {
            ItemDeudores itemDeudores = (ItemDeudores) item;
            holder.tvNombre.setText(itemDeudores.getNombre());
            holder.tvDetalle.setText(itemDeudores.getDetalle());
            holder.tvMonto.setText(String.format(Locale.getDefault(), "%.2f", itemDeudores.getMonto()));
            holder.cbCancelado.setChecked(itemDeudores.isCancelado());
        } else {
            ItemAcreedores itemAcreedores = (ItemAcreedores) item;
            holder.tvNombre.setText(itemAcreedores.getNombre());
            holder.tvDetalle.setText(itemAcreedores.getDetalle());
            holder.tvMonto.setText(String.format(Locale.getDefault(), "%.2f", itemAcreedores.getMonto()));
            holder.cbCancelado.setChecked(itemAcreedores.isCancelado());
        }
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