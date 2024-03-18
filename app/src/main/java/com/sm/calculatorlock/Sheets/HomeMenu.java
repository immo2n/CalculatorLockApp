package com.sm.calculatorlock.Sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sm.calculatorlock.Custom.Global;
import com.sm.calculatorlock.Pages.Home;
import com.sm.calculatorlock.R;

public class HomeMenu extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_home, container, false);
        Global.vCheckMenu(v);
        v.findViewById(R.id.privacy).setOnClickListener(view -> Home.showPrivacy.show());
        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}