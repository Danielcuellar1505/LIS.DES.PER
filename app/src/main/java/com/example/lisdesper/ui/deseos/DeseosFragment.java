package com.example.lisdesper.ui.deseos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.lisdesper.databinding.FragmentDeseosBinding;

public class DeseosFragment extends Fragment {

    private FragmentDeseosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DeseosViewModel deseosViewModel =
                new ViewModelProvider(this).get(DeseosViewModel.class);

        binding = FragmentDeseosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDeseos;
        deseosViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}