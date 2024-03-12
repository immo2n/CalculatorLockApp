package com.ucllc.smcalculatorlock.Pages.Frags;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ucllc.smcalculatorlock.DataClasses.LockedFile;
import com.ucllc.smcalculatorlock.Pages.Home;
import com.ucllc.smcalculatorlock.Pages.Vault.AllVault;
import com.ucllc.smcalculatorlock.Pages.Vault.PhotoVault;
import com.ucllc.smcalculatorlock.Pages.Vault.VideoVault;
import com.ucllc.smcalculatorlock.databinding.FragVaultBinding;

import java.util.HashMap;
import java.util.List;

public class FileVault extends Fragment {
    public static boolean disableSelection = false;
    public interface OnFileSelectedCallback {
        void onSelect(LockedFile lockedFile, CheckBox checkBox);
    }
    public static HashMap<LockedFile, CheckBox> unlockMap = new HashMap<>();
    public static List<String> unlockMapLink = new java.util.ArrayList<>();
    public static boolean unlockMapLocked = false;
    private AdView adView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.ucllc.smcalculatorlock.databinding.FragVaultBinding binding = FragVaultBinding.inflate(inflater);
        Home.fragmentControlVault = new Home.onFragmentControl() {
            @Override
            public void onBack() {
                // Do nothing
            }
            @Override
            public void onNeedReload() {
                // Do nothing
            }
        };
        //Hooks
        binding.photoPage.setOnClickListener(view -> startActivity(new Intent(requireContext(), PhotoVault.class)));
        binding.videoPage.setOnClickListener(view -> startActivity(new Intent(requireContext(), VideoVault.class)));
        binding.allPage.setOnClickListener(view -> startActivity(new Intent(requireContext(), AllVault.class)));

        /*
        adView = binding.vaultBannerAd;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
         */

        return binding.getRoot();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        adView.destroy();
    }
}
