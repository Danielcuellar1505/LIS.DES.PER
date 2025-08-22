package com.example.lisdesper.ui.acreedores;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AcreedoresViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AcreedoresViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("En desarollo...");
    }

    public LiveData<String> getText() {
        return mText;
    }
}