package com.example.lisdesper.ui.listas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;

import java.util.List;

public class ListasAdapter extends RecyclerView.Adapter<ListasAdapter.ListaViewHolder> {

    public interface OnAgregarClickListener {
        void onAgregarClick(int position, String nombreLista);
    }

    public List<String> listas;
    private OnAgregarClickListener listener;

    public ListasAdapter(List<String> listas, OnAgregarClickListener listener) {
        this.listas = listas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista, parent, false);
        return new ListaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaViewHolder holder, int position) {
        String nombre = listas.get(position);
        holder.tvNombre.setText(nombre);
        holder.btnAgregar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAgregarClick(position, nombre);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }

    public static class ListaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        Button btnAgregar;

        public ListaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreLista);
            btnAgregar = itemView.findViewById(R.id.btnAgregar);
        }
    }
}
