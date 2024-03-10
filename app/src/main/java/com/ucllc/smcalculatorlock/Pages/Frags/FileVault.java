package com.ucllc.smcalculatorlock.Pages.Frags;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ucllc.smcalculatorlock.Adapters.VaultListAdapter;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.Custom.Locker;
import com.ucllc.smcalculatorlock.DataClasses.LockedFile;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.Pages.Home;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.FragVaultBinding;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class FileVault extends Fragment {
    public interface OnFileSelectedCallback {
        void onSelect(LockedFile lockedFile, CheckBox checkBox);
    }
    public static HashMap<LockedFile, CheckBox> unlockMap;
    public static boolean unlockMapLocked = false;
    private FragVaultBinding binding;
    private Locker locker;
    private File lockerDestination;
    private DBHandler dbHandler;
    private OnFileSelectedCallback fileSelectedCallback;
    int totalUnlocked = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragVaultBinding.inflate(inflater);
        dbHandler = new DBHandler(requireContext());
        unlockMap = new HashMap<>();
        locker = new Locker(new Global(requireContext(), requireActivity()));
        lockerDestination = new File(requireContext().getFilesDir(), "locked");

        Home.fragmentControlVault = new Home.onFragmentControl() {
            @Override
            public void onBack() {
                // Do nothing
            }
            @Override
            public void onNeedReload() {
                if(Home.currentTabIndex != 0) return;
                loadFiles();
            }
        };

        YoYo.with(Techniques.SlideOutDown).duration(0).playOn(binding.unlockButton);
        new Handler(Looper.getMainLooper()).postDelayed(() -> requireActivity().runOnUiThread(() -> binding.unlockButton.setVisibility(View.VISIBLE)), 1000);
        fileSelectedCallback = (lockedFile, checkBox) -> lockerUIChange();

        loadFiles();

        binding.cancelSelection.setOnClickListener(v -> {
            unlockMapLocked = true;
            for (LockedFile lockedFile : unlockMap.keySet()) {
                CheckBox checkBox = unlockMap.get(lockedFile);
                if(null == checkBox) continue;
                checkBox.setChecked(false);
            }
            unlockMap.clear();
            unlockMapLocked = false;
            lockerUIChange();
        });
        binding.lockerText.setOnClickListener(view -> {
            if(unlockMap.size() == 0) return;
            totalUnlocked = 0;
            AlertDialog.Builder Dbuilder = new AlertDialog.Builder(this.requireContext());
            Dbuilder.setMessage("Unlocking...");
            Dbuilder.setTitle("Please wait");
            Dbuilder.setCancelable(false);
            AlertDialog DDialog = Dbuilder.create();
            DDialog.show();
            new Thread(() -> {
                for (LockedFile lockedFile : unlockMap.keySet()) {
                    File file = new File(lockerDestination, lockedFile.getHash());
                    if(file.exists()){
                        if(locker.unlockFile(lockedFile.getHash())){
                            totalUnlocked++;
                        }
                    }
                    dbHandler.removeLockedFile(lockedFile.getHash());
                }
                requireActivity().runOnUiThread(() -> {
                    if(null == dbHandler.getStateValue(StateKeys.TIP_UNLOCK)){
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                        builder.setTitle("Unlocked files")
                                .setMessage("Your files are saved in internal storage inside 'Calculator Vault' folder.")
                                .setPositiveButton("OK, don't show again", (dialog, which) -> dbHandler.setAppState(StateKeys.TIP_UNLOCK, StateKeys.VALUE_TRUE))
                                .setNegativeButton("OK", (dialog, which) -> dialog.dismiss())
                                .setCancelable(false)
                                .show();
                    }
                    DDialog.dismiss();
                    Toast.makeText(requireContext(), totalUnlocked + " files unlocked", Toast.LENGTH_SHORT).show();
                    unlockMap = new HashMap<>();
                    binding.vaultView.setAdapter(new VaultListAdapter(
                            dbHandler.getLockedFiles(), requireActivity(), requireContext(), fileSelectedCallback
                    ));
                    lockerUIChange();
                    Home.loadAdOnHome.loadAd();
                });
            }).start();
        });

        return binding.getRoot();
    }

    public static Thread tabThread = null;
    private void loadFiles() {
        if(null != tabThread && tabThread.isAlive()) return;
        binding.vaultView.setVisibility(View.GONE);
        binding.emptyVault.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);
        tabThread = new Thread(() -> {
            if(Home.currentTabIndex != 0 || !isAdded()) return;
            List<LockedFile> lockedFiles = dbHandler.getLockedFiles();
            requireActivity().runOnUiThread(() -> {
                binding.vaultView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.vaultView.setAdapter(new VaultListAdapter(
                        lockedFiles, requireActivity(), requireContext(), fileSelectedCallback
                ));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if(Home.currentTabIndex != 0 || !isAdded()) return;
                    binding.loading.setVisibility(View.GONE);
                    if(lockedFiles.size() == 0){
                        binding.vaultView.setVisibility(View.GONE);
                        binding.emptyVault.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.vaultView.setVisibility(View.VISIBLE);
                        binding.emptyVault.setVisibility(View.GONE);
                    }
                }, 300);
            });
            tabThread = null;
        });
        tabThread.start();
    }

    private boolean lockerShowing = false;
    @SuppressLint("DefaultLocale")
    private void lockerUIChange(){
        if(unlockMap.size() > 0){
            binding.lockerText.setText(String.format("%s (%d)", requireActivity().getString(R.string.unlock_files), unlockMap.size()));
        }
        if(unlockMap.size() == 0){
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(binding.unlockButton);
            YoYo.with(Techniques.ZoomOut).duration(300).playOn(binding.unlockButton);
            lockerShowing = false;
        } else {
            if(lockerShowing) return;
            YoYo.with(Techniques.ZoomIn).duration(300).playOn(binding.unlockButton);
            YoYo.with(Techniques.SlideInUp).duration(300).playOn(binding.unlockButton);
            lockerShowing = true;
        }
        new Thread(() -> {
            List<LockedFile> lockedFiles = dbHandler.getLockedFiles();
            if(!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if(lockedFiles.size() == 0){
                    binding.vaultView.setVisibility(View.GONE);
                    binding.emptyVault.setVisibility(View.VISIBLE);
                }
                else {
                    binding.vaultView.setVisibility(View.VISIBLE);
                    binding.emptyVault.setVisibility(View.GONE);
                }
            });
        }).start();
    }
}
