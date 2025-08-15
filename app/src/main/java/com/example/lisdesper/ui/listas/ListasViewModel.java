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
        current.add(new Lista(nombreLista));
        listas.setValue(current);
    }

    public void agregarItem(int posicionLista, Item item) {
        List<Lista> current = new ArrayList<>(listas.getValue());

        if (current.isEmpty()) {
            current.add(new Lista("Lista Principal"));
        }

        int pos = (posicionLista >= 0 && posicionLista < current.size()) ? posicionLista : 0;
        Lista listaSeleccionada = current.get(pos);

        List<Item> nuevosItems = new ArrayList<>(listaSeleccionada.getItems());
        nuevosItems.add(item);
        listaSeleccionada.setItems(nuevosItems);

        current.set(pos, listaSeleccionada);
        listas.setValue(current);
    }

    public void actualizarItem(int posicionLista, int posicionItem, Item itemActualizado) {
        List<Lista> current = new ArrayList<>(listas.getValue());
        if (current.isEmpty()) return;
        Lista lista = current.get(Math.max(0, Math.min(posicionLista, current.size()-1)));
        List<Item> items = new ArrayList<>(lista.getItems());
        if (posicionItem < 0 || posicionItem >= items.size()) return;
        items.set(posicionItem, itemActualizado);
        lista.setItems(items);
        current.set(Math.max(0, posicionLista), lista);
        listas.setValue(current);
    }
}
