package com.ucllc.smcalculatorlock.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    private DBHandler dbHandler;
    private Global global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        global = new Global(this, this);
        dbHandler = new DBHandler(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black_button));



        //Hooks
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_key);
        if(null != dialog.getWindow()){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialogue_background));
            dialog.getWindow().findViewById(R.id.changePinButton).setOnClickListener(v-> {
                startActivity(new Intent(Home.this, PatternLock.class));
            });
            dialog.getWindow().findViewById(R.id.chagePatternButton).setOnClickListener(v-> {
                if(null != global.getDBHandler()) global.getDBHandler().deleteStateValue(StateKeys.RECOVERY_PATTERN);
                startActivity(new Intent(Home.this, PatternLock.class).putExtra("setup", true));
            });
        }
        binding.key.setOnClickListener(v-> dialog.show());
    }
}