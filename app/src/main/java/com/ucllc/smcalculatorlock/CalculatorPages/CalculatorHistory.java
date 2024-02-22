package com.ucllc.smcalculatorlock.CalculatorPages;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;

import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityCalculatorHistoryBinding;

public class CalculatorHistory extends AppCompatActivity {
    protected ActivityCalculatorHistoryBinding binding;
    protected OnBackPressedCallback backPressedCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalculatorHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));


        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
        binding.backButton.setOnClickListener(view -> {
            backPressedCallback.handleOnBackPressed();
        });
    }
}