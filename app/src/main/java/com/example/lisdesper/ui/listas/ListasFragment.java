package com.example.lisdesper.ui.listas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.lisdesper.databinding.FragmentListasBinding;

public class ListasFragment extends Fragment {

    private FragmentListasBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListasViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ListasViewModel.class);

        binding = FragmentListasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textListas;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}