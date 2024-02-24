package com.ucllc.smcalculatorlock.Pages.Frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ucllc.smcalculatorlock.databinding.FragApplockBinding;

public class AppLock extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragApplockBinding binding = FragApplockBinding.inflate(inflater);


        return binding.getRoot();
    }
}
