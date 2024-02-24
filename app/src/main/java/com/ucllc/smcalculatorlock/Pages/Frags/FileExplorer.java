package com.ucllc.smcalculatorlock.Pages.Frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ucllc.smcalculatorlock.databinding.FragExplorerBinding;

public class FileExplorer extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragExplorerBinding binding = FragExplorerBinding.inflate(inflater);



        return binding.getRoot();
    }
}
