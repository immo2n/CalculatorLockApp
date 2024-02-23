package com.ucllc.smcalculatorlock.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.itsxtt.patternlock.PatternLockView;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityPatternLockBinding;

import java.util.ArrayList;

public class PatternLock extends AppCompatActivity {
    ActivityPatternLockBinding binding;
    boolean setupMode = false;
    private String holdPattern = null, oldPattern = null;
    private Global global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatternLockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));
        global = new Global(this, this);
        if(getIntent().getBooleanExtra("setup", false)){
            setupMode = true;
        }
        if(setupMode){
            binding.message.setText(R.string.setup_a_new_pattern_for_pin_recovery);
        }
        else {
            if(null != global.getDBHandler()) {
                oldPattern = global.decrypt(global.getDBHandler().getStateValue(StateKeys.RECOVERY_PATTERN));
            }
        }
        binding.patternView.setOnPatternListener(new PatternLockView.OnPatternListener() {
            @Override
            public void onStarted() {
                //Do none
            }
            @Override
            public void onProgress(@NonNull ArrayList<Integer> arrayList) {
                //Do none
            }
            @Override
            public boolean onComplete(@NonNull ArrayList<Integer> arrayList) {
                if(setupMode){
                    if(arrayList.size() < 4){
                        Toast.makeText(PatternLock.this, "Too short", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if(holdPattern == null){
                        holdPattern = arrayList.toString();
                        binding.message.setText(R.string.draw_again_to_confirm);
                        return true;
                    }
                    else {
                        //Confirm mode
                        if(holdPattern.equals(arrayList.toString())){
                            //Save the pattern
                            String key = global.encrypt(holdPattern);
                            if(null != key){
                                if(null != global.getDBHandler()){
                                    global.getDBHandler().setAppState(StateKeys.RECOVERY_PATTERN, key);
                                    startActivity(new Intent(PatternLock.this, Calculator.class));
                                    Toast.makeText(PatternLock.this, "Setup complete", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                            else {
                                Toast.makeText(PatternLock.this, "Failed to save the pattern! Restart app.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(PatternLock.this, "Patterns do not match", Toast.LENGTH_SHORT).show();
                            holdPattern = null;
                            binding.message.setText(R.string.setup_a_new_pattern_for_pin_recovery);
                        }
                    }
                    return true;
                }
                else {
                    //Recovery PIN mode -- ONLY CHECK THE PATTERN
                    if(null == oldPattern){
                        Toast.makeText(PatternLock.this, "No pattern found! Or, can not decrypt.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    else {
                        if(oldPattern.equals(arrayList.toString())){
                            Toast.makeText(PatternLock.this, "Done yo!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(PatternLock.this, "Wrong pattern", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return false;
            }
        });
    }
}