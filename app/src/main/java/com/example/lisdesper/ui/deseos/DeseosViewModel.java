package com.example.lisdesper.ui.deseos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeseosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DeseosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("DESEOS");
    }

    public LiveData<String> getText() {
        return mText;
    }
}