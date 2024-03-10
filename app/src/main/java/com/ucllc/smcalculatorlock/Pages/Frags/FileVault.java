package com.ucllc.smcalculatorlock.Pages.Frags;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ucllc.smcalculatorlock.Adapters.VaultListAdapter;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.DataClasses.LockedFile;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.FragVaultBinding;

import java.io.File;
import java.util.HashMap;

public class FileVault extends Fragment {
    public interface OnFileSelectedCallback {
        void onSelect(LockedFile lockedFile, CheckBox checkBox);
    }
    private OnFileSelectedCallback fileSelectedCallback;
    private DBHandler dbHandler;
    public static HashMap<LockedFile, CheckBox> unlockMap;
    private FragVaultBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragVaultBinding.inflate(inflater);
        dbHandler = new DBHandler(requireContext());
        unlockMap = new HashMap<>();

        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(binding.unlockButton);
        new Handler(Looper.getMainLooper()).postDelayed(() -> requireActivity().runOnUiThread(() -> binding.unlockButton.setVisibility(View.VISIBLE)), 300);
        fileSelectedCallback = (lockedFile, checkBox) -> lockerUIChange();

        binding.vaultView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.vaultView.setAdapter(new VaultListAdapter(
                dbHandler.getLockedFiles(), requireActivity(), requireContext(), fileSelectedCallback
        ));

        return binding.getRoot();
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
    }
}
