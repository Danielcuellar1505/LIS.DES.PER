package com.example.lisdesper.ui.acreedores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.lisdesper.databinding.FragmentAcreedoresBinding;

public class AcreedoresFragment extends Fragment {

    private FragmentAcreedoresBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AcreedoresViewModel acreedoresViewModel =
                new ViewModelProvider(this).get(AcreedoresViewModel.class);

        binding = FragmentAcreedoresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAcreedores;
        acreedoresViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}