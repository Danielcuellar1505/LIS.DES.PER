package com.example.lisdesper.ui.listas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lisdesper.R;
import com.example.lisdesper.databinding.FragmentListasBinding;

import java.util.ArrayList;
import java.util.List;

public class ListasFragment extends Fragment {

    private FragmentListasBinding binding;
    private ListasViewModel listasViewModel;
    private ListasAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        listasViewModel = new ViewModelProvider(this).get(ListasViewModel.class);

        binding = FragmentListasBinding.inflate(inflater, container, false);

        List<Lista> listasIniciales = listasViewModel.getListas().getValue();
        if (listasIniciales == null) {
            listasIniciales = new ArrayList<>();
        }
        adapter = new ListasAdapter(requireContext(), listasIniciales, posicionLista -> {
            mostrarDialogoAgregarItem(posicionLista);
        });

        binding.recyclerViewListas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewListas.setAdapter(adapter);

        listasViewModel.getListas().observe(getViewLifecycleOwner(), listas -> {
            adapter.setListas(listas);
        });

        binding.fabAgregarLista.setOnClickListener(v -> {
            mostrarDialogoAgregarLista();
        });
        return binding.getRoot();
    }
    private void mostrarDialogoAgregarItem(int posicionLista) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_agregar_item, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etMonto = dialogView.findViewById(R.id.etMonto);

        builder.setView(dialogView)
                .setTitle("Agregar ítem a la lista")
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String montoStr = etMonto.getText().toString().trim();

                    if (nombre.isEmpty() || montoStr.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor ingresa nombre y monto", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double monto;
                    try {
                        monto = Double.parseDouble(montoStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Monto inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listasViewModel.agregarItem(posicionLista, new Item(nombre, monto));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoAgregarLista() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        final EditText inputNombre = new EditText(requireContext());
        inputNombre.setHint("Nombre de la lista");

        builder.setView(inputNombre)
                .setTitle("Nueva Lista")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = inputNombre.getText().toString().trim();
                    if (nombre.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor ingresa el nombre de la lista", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listasViewModel.agregarLista(nombre);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
