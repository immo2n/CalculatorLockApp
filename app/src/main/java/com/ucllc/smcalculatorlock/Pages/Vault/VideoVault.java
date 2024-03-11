package com.ucllc.smcalculatorlock.Pages.Vault;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ucllc.smcalculatorlock.Adapters.VaultListAdapter;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Explorer;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.Custom.Locker;
import com.ucllc.smcalculatorlock.DataClasses.LockedFile;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.Pages.Frags.FileVault;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityPhotoVaultBinding;
import com.ucllc.smcalculatorlock.databinding.ActivityVideoVaultBinding;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class VideoVault extends AppCompatActivity {
    ActivityVideoVaultBinding binding;
    private DBHandler dbHandler;
    private FileVault.OnFileSelectedCallback fileSelectedCallback;
    int totalUnlocked = 0;
    private Locker locker;
    private File lockerDestination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoVaultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black_button));
        binding.back.setOnClickListener(v -> finish());
        dbHandler = new DBHandler(this);

        AdView adView = binding.vaultBannerAd;
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(null == adView.getAdSize()) return;
                int adHeight = adView.getAdSize().getHeightInPixels(getApplicationContext());
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.vaultView.getLayoutParams();
                layoutParams.bottomMargin = adHeight;
                binding.vaultView.setLayoutParams(layoutParams);
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        FileVault.diableSelection = false;
        binding.selectAll.setOnClickListener(v -> {
            FileVault.diableSelection = true;
            FileVault.unlockMapLocked = true;
            FileVault.unlockMap.clear();
            FileVault.unlockMapLink.clear();
            for (LockedFile lockedFile : dbHandler.getLockedFiles(Explorer.FileType.VIDEO)) {
                FileVault.unlockMap.put(lockedFile, null);
                FileVault.unlockMapLink.add(lockedFile.getHash());
            }
            FileVault.unlockMapLocked = false;
            loadFiles();
            lockerUIChange();
        });

        locker = new Locker(new Global(this, this));
        lockerDestination = new File(getFilesDir(), "locked");

        YoYo.with(Techniques.SlideOutDown).duration(0).playOn(binding.unlockButton);
        new Handler(Looper.getMainLooper()).postDelayed(() -> runOnUiThread(() -> binding.unlockButton.setVisibility(View.VISIBLE)), 1000);

        FileVault.unlockMap = new HashMap<>();
        FileVault.unlockMapLink = new java.util.ArrayList<>();
        FileVault.unlockMapLocked = false;

        fileSelectedCallback = (lockedFile, checkBox) -> lockerUIChange();
        loadFiles();

        binding.cancelSelection.setOnClickListener(v -> {
            FileVault.diableSelection = false;
            FileVault.unlockMapLocked = true;
            for (LockedFile lockedFile : FileVault.unlockMap.keySet()) {
                CheckBox checkBox = FileVault.unlockMap.get(lockedFile);
                if(null == checkBox) continue;
                checkBox.setChecked(false);
            }
            FileVault.unlockMap.clear();
            FileVault.unlockMapLink.clear();
            FileVault.unlockMapLocked = false;
            loadFiles();
            lockerUIChange();
        });

        binding.lockerText.setOnClickListener(view -> {
            if(FileVault.unlockMap.size() == 0) return;
            totalUnlocked = 0;
            AlertDialog.Builder Dbuilder = new AlertDialog.Builder(this);
            Dbuilder.setMessage("Unlocking...");
            Dbuilder.setTitle("Please wait");
            Dbuilder.setCancelable(false);
            AlertDialog DDialog = Dbuilder.create();
            DDialog.show();
            new Thread(() -> {
                for (LockedFile lockedFile : FileVault.unlockMap.keySet()) {
                    File file = new File(lockerDestination, lockedFile.getHash());
                    if(file.exists()){
                        if(locker.unlockFile(lockedFile.getHash())){
                            totalUnlocked++;
                            runOnUiThread(() -> DDialog.setMessage("Unlocked " + totalUnlocked + "/" + FileVault.unlockMap.size() +" files..." + "\n" + lockedFile.getFileName()));
                        }
                    }
                    dbHandler.removeLockedFile(lockedFile.getHash());
                }
                runOnUiThread(() -> {
                    if(null == dbHandler.getStateValue(StateKeys.TIP_UNLOCK)){
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                        builder.setTitle("Unlocked files")
                                .setMessage("Your files are saved in internal storage inside 'Calculator Vault' folder.")
                                .setPositiveButton("OK, don't show again", (dialog, which) -> dbHandler.setAppState(StateKeys.TIP_UNLOCK, StateKeys.VALUE_TRUE))
                                .setNegativeButton("OK", (dialog, which) -> dialog.dismiss())
                                .setCancelable(false)
                                .show();
                    }
                    DDialog.dismiss();
                    Toast.makeText(this, totalUnlocked + " files unlocked", Toast.LENGTH_SHORT).show();
                    FileVault.unlockMap = new HashMap<>();
                    binding.vaultView.setAdapter(new VaultListAdapter(
                            dbHandler.getLockedFiles(Explorer.FileType.VIDEO), this, this, fileSelectedCallback
                    ));
                    lockerUIChange();
                });
            }).start();
        });

    }
    public static Thread tabThread = null;
    private void loadFiles() {
        if(null != tabThread && tabThread.isAlive()) return;
        binding.vaultView.setVisibility(View.GONE);
        binding.emptyVault.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);
        tabThread = new Thread(() -> {
            List<LockedFile> lockedFiles = dbHandler.getLockedFiles(Explorer.FileType.VIDEO);
            runOnUiThread(() -> {
                binding.vaultView.setLayoutManager(new LinearLayoutManager(this));
                binding.vaultView.setAdapter(new VaultListAdapter(
                        lockedFiles, this, this, fileSelectedCallback
                ));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    binding.loading.setVisibility(View.GONE);
                    if(lockedFiles.size() == 0){
                        binding.vaultView.setVisibility(View.GONE);
                        binding.emptyVault.setVisibility(View.VISIBLE);
                        binding.selectAll.setVisibility(View.GONE);
                    }
                    else {
                        binding.vaultView.setVisibility(View.VISIBLE);
                        binding.emptyVault.setVisibility(View.GONE);
                        binding.selectAll.setVisibility(View.VISIBLE);
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
        if(FileVault.unlockMap.size() > 0){
            binding.lockerText.setText(String.format("%s (%d)", getString(R.string.unlock_files), FileVault.unlockMap.size()));
        }
        if(FileVault.unlockMap.size() == 0){
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
            List<LockedFile> lockedFiles = dbHandler.getLockedFiles(Explorer.FileType.VIDEO);
            runOnUiThread(() -> {
                if(lockedFiles.size() == 0){
                    binding.vaultView.setVisibility(View.GONE);
                    binding.emptyVault.setVisibility(View.VISIBLE);
                    binding.selectAll.setVisibility(View.GONE);
                }
                else {
                    binding.vaultView.setVisibility(View.VISIBLE);
                    binding.emptyVault.setVisibility(View.GONE);
                    binding.selectAll.setVisibility(View.GONE);
                }
            });
        }).start();
    }
}