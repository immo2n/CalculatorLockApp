package com.ucllc.smcalculatorlock.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ucllc.smcalculatorlock.Custom.Config;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityBrowserBinding;

public class Browser extends AppCompatActivity {
    private Global global;
    ActivityBrowserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrowserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));

        global = new Global(this, this);
        if(null != global.decrypt(Config.BROWSER_VALIDATOR)){

            return;
        }
        Toast.makeText(this, "Browser can not start!", Toast.LENGTH_SHORT).show();
        finish();
    }
}