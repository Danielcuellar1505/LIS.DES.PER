package com.example.lisdesper.ui.listas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListasViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ListasViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("LISTAS");
    }

    public LiveData<String> getText() {
        return mText;
    }
}