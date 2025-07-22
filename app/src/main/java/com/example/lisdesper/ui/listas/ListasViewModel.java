package com.example.lisdesper.ui.listas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListasViewModel extends ViewModel {

    private final MutableLiveData<List<Lista>> listas;

    public ListasViewModel() {
        listas = new MutableLiveData<>(new ArrayList<>());

    }

    public LiveData<List<Lista>> getListas() {
        return listas;
    }

    public void agregarLista(String nombreLista) {
        List<Lista> current = new ArrayList<>(listas.getValue());
        current.add(new Lista(nombreLista)); // lista nueva con items vacíos
        listas.setValue(current);
    }

    public void agregarItem(int posicionLista, Item item) {
        List<Lista> current = new ArrayList<>(listas.getValue());
        Lista listaSeleccionada = current.get(posicionLista);

        // Copiar items para evitar problemas de referencias
        List<Item> nuevosItems = new ArrayList<>(listaSeleccionada.getItems());
        nuevosItems.add(item);
        listaSeleccionada.setItems(nuevosItems);

        current.set(posicionLista, listaSeleccionada);
        listas.setValue(current);
    }

}
