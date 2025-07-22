package com.example.lisdesper.ui.listas;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.R;

import java.util.List;

public class ListasAdapter extends RecyclerView.Adapter<ListasAdapter.ListaViewHolder> {

    private List<Lista> listas;
    private OnAgregarClickListener listener;

    public interface OnAgregarClickListener {
        void onAgregarClick(int posicionLista);
    }



    private Context context;

    public ListasAdapter(Context context, List<Lista> listas, OnAgregarClickListener listener) {
        this.context = context;
        this.listas = listas;
        this.listener = listener;
    }


    public void setListas(List<Lista> listas) {
        this.listas = listas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_checkbox, parent, false);
        return new ListaViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ListaViewHolder holder, int position) {
        Lista lista = listas.get(position);
        holder.txtNombreLista.setText(lista.getNombre());
        // Botón para agregar ítems
        holder.btnAgregarItem.setOnClickListener(v -> {
            listener.onAgregarClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }

    public static class ListaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreLista;
        LinearLayout itemsContainer;
        View btnAgregarItem;

        public ListaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreLista = itemView.findViewById(R.id.txtNombreLista);
            btnAgregarItem = itemView.findViewById(R.id.btnAgregarItem);
            itemsContainer = itemView.findViewById(R.id.itemsContainer); // si lo tienes
        }
    }
}
