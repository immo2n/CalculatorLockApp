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
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.Helpers.AppListHelper;
import com.ucllc.smcalculatorlock.Pages.Home;
import com.ucllc.smcalculatorlock.databinding.FragApplockBinding;

import java.util.List;

public class AppLock extends Fragment {
    public static Thread tabThread = null;
    private ActivityResultLauncher<Intent> accessibilityServiceLauncher;
    private FragApplockBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragApplockBinding.inflate(inflater);

        Home.fragmentControlApps = new Home.onFragmentControl() {
            @Override
            public void onBack() {
                //Do nothing
            }

            @Override
            public void onNeedReload() {
                if(Home.currentTabIndex != 1) return;
                binding.mainView.setVisibility(View.GONE);
                binding.loading.setVisibility(View.VISIBLE);
                loadApps();
            }
        };

        loadApps();

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
        return binding.getRoot();
    }

    private void loadApps() {
        if(null != tabThread && tabThread.isAlive()) return;
        tabThread = new Thread(() -> {
            if(Home.currentTabIndex != 1) return;
            List<AppListHelper.AppInfo> apps = AppListHelper.getInstalledApps(requireContext());
            requireActivity().runOnUiThread(() -> {
                binding.mainView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.mainView.setAdapter(new AppListAdapter(apps, requireContext()));
                binding.loading.setVisibility(View.GONE);
                binding.mainView.setVisibility(View.VISIBLE);
                checkPermissions();
                tabThread = null;
            });
        });
        tabThread.start();
    }

    private void checkPermissions() {
        if(!hasOverlayPermission(requireContext())){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("Display over other apps")
                    .setMessage("We need Display over other apps permission to lock apps. Please grant the permission.\nReason: To show lock screen.\nNecessity: Fundamental, can't function without it.")
                    .setPositiveButton("Open settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        accessibilityServiceLauncher.launch(intent);
                        Home.appLockPermissionCheck = true;
                    })
                    .setCancelable(false)
                    .show();
        }
        else {
            if(!hasUsageStatsPermission(requireContext())){
                Global.propUsageStatsPermission(requireContext(), accessibilityServiceLauncher);
            }
        }
    }
}
