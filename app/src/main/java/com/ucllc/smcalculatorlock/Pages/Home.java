package com.ucllc.smcalculatorlock.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Global;
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
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));






    }
}