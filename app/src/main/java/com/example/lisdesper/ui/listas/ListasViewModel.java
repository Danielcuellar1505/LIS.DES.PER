package com.example.lisdesper.ui.listas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListasViewModel extends ViewModel {

    private final MutableLiveData<List<String>> listas;

    public ListasViewModel() {
        listas = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<String>> getListas() {
        return listas;
    }

    public void agregarLista(String nombre) {
        List<String> current = listas.getValue();
        if (current != null) {
            current.add(nombre);
            listas.setValue(current);
        }
    }
}
