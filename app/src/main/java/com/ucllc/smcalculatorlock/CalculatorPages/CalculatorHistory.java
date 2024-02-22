package com.ucllc.smcalculatorlock.CalculatorPages;

import static java.security.AccessController.getContext;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ucllc.smcalculatorlock.Adapters.CalculatorHistoryAdapter;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityCalculatorHistoryBinding;

import java.util.List;

public class CalculatorHistory extends AppCompatActivity {
    protected ActivityCalculatorHistoryBinding binding;
    protected OnBackPressedCallback backPressedCallback;
    protected DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalculatorHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));
        dbHandler = new DBHandler(this);

        List<String> list = dbHandler.getHistory();
        if(null != list && list.size() > 0) {
            CalculatorHistoryAdapter adapter = new CalculatorHistoryAdapter(dbHandler.getHistory());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.historyView.setLayoutManager(layoutManager);
            binding.historyView.setAdapter(adapter);
        }
        else {
            binding.deleteButton.setVisibility(View.GONE);
            binding.historyView.setVisibility(View.GONE);
            binding.noHistory.setVisibility(View.VISIBLE);
        }

        binding.deleteButton.setOnClickListener(view -> new MaterialAlertDialogBuilder(CalculatorHistory.this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete all history?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        dbHandler.deleteHistory();
                        finish();
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    } catch (Exception e) {
                        Global.logError(e);
                    }
                })
                .setNegativeButton("No", null)
                .show());

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