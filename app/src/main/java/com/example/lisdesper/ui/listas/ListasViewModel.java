package com.example.lisdesper.ui.listas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lisdesper.firebase.CBaseDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListasViewModel extends ViewModel {
    private final MutableLiveData<List<Lista>> listas;
    private final CBaseDatos db;
    private String currentListaId;
    private String currentListaNombre = "Lista Principal";

    public ListasViewModel() {
        listas = new MutableLiveData<>(new ArrayList<>());
        db = CBaseDatos.getInstance();
        cargarListaPrincipal();
    }

    private void cargarListaPrincipal() {
        db.obtenerListaPrincipal((result, e) -> {
            if (e != null || result == null || result.getValue() == null) {
                // Manejar error
                return;
            }
            this.currentListaId = result.getKey(); // Obtenemos el ID de la lista
            List<Item> items = result.getValue(); // Obtenemos los items

            List<Lista> current = new ArrayList<>();
            Lista listaPrincipal = new Lista(currentListaNombre);
            listaPrincipal.setItems(items); // Aquí pasamos directamente la List<Item>
            current.add(listaPrincipal);
            listas.postValue(current);
        });
    }

    public LiveData<List<Lista>> getListas() {
        return listas;
    }

    public void agregarItem(int posicionLista, Item item) {
        final List<Lista> current = new ArrayList<>(listas.getValue() != null ?
                listas.getValue() : new ArrayList<>());

        if (current.isEmpty()) {
            current.add(new Lista(currentListaNombre));
        }

        db.agregarItem(currentListaId, item, (savedItem, e) -> {
            if (e != null || savedItem == null) {
                // Mostrar error al usuario
                return;
            }

            // Usamos el item con ID asignado por Firestore
            List<Lista> updatedList = new ArrayList<>(current);
            int pos = (posicionLista >= 0 && posicionLista < updatedList.size()) ? posicionLista : 0;
            Lista listaSeleccionada = updatedList.get(pos);

            List<Item> nuevosItems = new ArrayList<>(listaSeleccionada.getItems());
            nuevosItems.add(savedItem);
            listaSeleccionada.setItems(nuevosItems);

            updatedList.set(pos, listaSeleccionada);
            listas.postValue(updatedList);
        });
    }

    public void actualizarItem(int posicionLista, int posicionItem, Item itemActualizado) {
        final List<Lista> current = new ArrayList<>(listas.getValue() != null ?
                listas.getValue() : new ArrayList<>());

        if (current.isEmpty()) return;

        final int safePosicionLista = Math.max(0, Math.min(posicionLista, current.size()-1));
        Lista lista = current.get(safePosicionLista);
        List<Item> items = lista.getItems();

        if (posicionItem < 0 || posicionItem >= items.size()) return;

        // Actualizar localmente primero para mejor experiencia de usuario
        items.set(posicionItem, itemActualizado);
        lista.setItems(items);
        current.set(safePosicionLista, lista);
        listas.postValue(current);

        // Actualizar en Firestore
        if (itemActualizado.getId() != null && !itemActualizado.getId().isEmpty()) {
            db.actualizarItem(currentListaId, itemActualizado.getId(), itemActualizado,
                    (result, e) -> {
                        if (e != null) {
                            // Revertir cambios si falla la actualización en Firestore
                            items.set(posicionItem, itemActualizado);
                            lista.setItems(items);
                            current.set(safePosicionLista, lista);
                            listas.postValue(current);
                        }
                    });
        }
    }

    public void setCurrentListaId(String listaId) {
        this.currentListaId = listaId;
    }

    public void setCurrentListaNombre(String nombre) {
        this.currentListaNombre = nombre;
    }
}