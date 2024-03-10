package com.ucllc.smcalculatorlock.Sheets;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.Pages.Home;
import com.ucllc.smcalculatorlock.R;

public class HomeMenu extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getContext();
        View v = inflater.inflate(R.layout.menu_home, container, false);
        Global.vCheckMenu(v);
        v.findViewById(R.id.privacy).setOnClickListener(view -> Home.showPrivacy.show());
        Global.opUset(v.findViewById(R.id.stabilizer), context);
        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}