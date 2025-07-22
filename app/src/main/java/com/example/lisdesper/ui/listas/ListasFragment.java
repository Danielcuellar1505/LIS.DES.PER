package com.example.lisdesper.ui.listas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lisdesper.databinding.FragmentListasBinding;

import java.util.ArrayList;

public class ListasFragment extends Fragment {

    private FragmentListasBinding binding;
    private ListasViewModel listasViewModel;
    private ListasAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listasViewModel = new ViewModelProvider(this).get(ListasViewModel.class);
        binding = FragmentListasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup RecyclerView
        binding.recyclerListas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListasAdapter(new ArrayList<>(), (position, nombreLista) -> {
            Toast.makeText(getContext(), "Clicked + on: " + nombreLista, Toast.LENGTH_SHORT).show();
            // Aquí puedes manejar lo que haga el botón + por ítem
        });
        binding.recyclerListas.setAdapter(adapter);

        // Observar cambios en la lista
        listasViewModel.getListas().observe(getViewLifecycleOwner(), listas -> {
            adapter.listas = listas;
            adapter.notifyDataSetChanged();
        });

        // Botón Nueva Lista
        binding.btnNuevaLista.setOnClickListener(v -> mostrarDialogoNuevaLista());

        return root;
    }

    private void mostrarDialogoNuevaLista() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nueva Lista");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String texto = input.getText().toString().trim();
            if (!texto.isEmpty()) {
                listasViewModel.agregarLista(texto);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
