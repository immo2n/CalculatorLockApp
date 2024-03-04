package com.ucllc.smcalculatorlock.Pages.Frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ucllc.smcalculatorlock.Adapters.AppListAdapter;
import com.ucllc.smcalculatorlock.Helpers.AppListHelper;
import com.ucllc.smcalculatorlock.databinding.FragApplockBinding;

import java.util.List;

public class AppLock extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragApplockBinding binding = FragApplockBinding.inflate(inflater);
        List<AppListHelper.AppInfo> apps = AppListHelper.getInstalledApps(requireContext());
        binding.getRoot().setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.getRoot().setAdapter(new AppListAdapter(apps, requireContext()));
        return binding.getRoot();
    }
}
