package com.ucllc.smcalculatorlock.Pages.Frags;

import static android.app.Activity.RESULT_OK;
import static com.ucllc.smcalculatorlock.Custom.Global.hasOverlayPermission;
import static com.ucllc.smcalculatorlock.Custom.Global.hasUsageStatsPermission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ucllc.smcalculatorlock.Adapters.AppListAdapter;
import com.ucllc.smcalculatorlock.Helpers.AppListHelper;
import com.ucllc.smcalculatorlock.databinding.FragApplockBinding;

import java.util.List;

public class AppLock extends Fragment {
    private ActivityResultLauncher<Intent> accessibilityServiceLauncher;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragApplockBinding binding = FragApplockBinding.inflate(inflater);
        List<AppListHelper.AppInfo> apps = AppListHelper.getInstalledApps(requireContext());
        binding.getRoot().setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.getRoot().setAdapter(new AppListAdapter(apps, requireContext()));

        accessibilityServiceLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if(hasUsageStatsPermission(requireContext())){
                    Toast.makeText(requireContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
                }
                if(hasOverlayPermission(requireContext())){
                    Toast.makeText(requireContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(!hasOverlayPermission(requireContext())){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("Display over other apps")
                    .setMessage("We need Display over other apps permission to lock apps. Please grant the permission.\nReason: To show lock screen.\nNecessity: Fundamental, can't function without it.")
                    .setPositiveButton("Open settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        accessibilityServiceLauncher.launch(intent);
                    })
                    .setCancelable(false)
                    .show();
        }
        else {
            if(!hasUsageStatsPermission(requireContext())){
                propUsageStatsPermission();
            }
        }

        return binding.getRoot();
    }

    private void propUsageStatsPermission() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Usage Access")
                .setMessage("We need Usage Access permission to lock apps. Please grant the permission.\nReason: To detect which app the user is opening.\nNecessity: Fundamental, can't function without it.")
                .setPositiveButton("Open settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    accessibilityServiceLauncher.launch(intent);
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(hasOverlayPermission(requireContext())){
            if(!hasUsageStatsPermission(requireContext())){
                propUsageStatsPermission();
            }
        }
    }
}
