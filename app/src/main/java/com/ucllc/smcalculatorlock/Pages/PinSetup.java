package com.ucllc.smcalculatorlock.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.ucllc.smcalculatorlock.Custom.Config;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.databinding.ActivityPinSetupBinding;

import java.util.ArrayList;
import java.util.List;

public class PinSetup extends AppCompatActivity {
    ActivityPinSetupBinding binding;
    private String pin, holderPin;
    Global global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));
        global = new Global(this, this);
        pin = "";
        holderPin = null;

        //KEYPAD SETUP
        binding.num0.setOnClickListener(v-> keyDown(0));
        binding.num1.setOnClickListener(v-> keyDown(1));
        binding.num2.setOnClickListener(v-> keyDown(2));
        binding.num3.setOnClickListener(v-> keyDown(3));
        binding.num4.setOnClickListener(v-> keyDown(4));
        binding.num5.setOnClickListener(v-> keyDown(5));
        binding.num6.setOnClickListener(v-> keyDown(6));
        binding.num7.setOnClickListener(v-> keyDown(7));
        binding.num8.setOnClickListener(v-> keyDown(8));
        binding.num9.setOnClickListener(v-> keyDown(9));

        binding.allClear.setOnClickListener(view -> {
            if(pin.length() > 0){
                pin = "";
                binding.display.setText(getString(R.string.pin_empty));
                handleDisplay();
            }
        });
        binding.backSpace.setOnClickListener(view -> {
            if(pin.length() > 0){
                pin = pin.substring(0, pin.length() - 1);
                handleDisplay();
            }
        });
        binding.equal.setOnClickListener(view -> {
            if(holderPin == null){
                holderPin = pin;
                binding.pinMessage.setText(R.string.confirm_your_pin);
                pin = "";
                handleDisplay();
            }
            else {
                if(holderPin.equals(pin)){
                    if(null != global.getDBHandler()){
                        String encryptedPin = global.encrypt(pin);
                        if(null != encryptedPin) {

                            //STORE PIN
                            List<String[]> stateList = new ArrayList<>();
                            stateList.add(new String[]{StateKeys.PIN, encryptedPin});
                            stateList.add(new String[]{StateKeys.ENCRYPTION_VERSION, Config.ENCRYPTION_VERSION});
                            global.getDBHandler().setAppState(stateList);

                            if(null == global.getDBHandler().getStateValue(StateKeys.RECOVERY_PATTERN)){
                                startActivity(new Intent(PinSetup.this, PatternLock.class).putExtra("setup", true));
                                finish();
                                return;
                            }

                            startActivity(new Intent(PinSetup.this, Calculator.class));
                            finish();
                        }
                        else {
                            Toast.makeText(PinSetup.this, "Encryption error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(PinSetup.this, "Database error!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    global.vibrate(150);
                    Toast.makeText(PinSetup.this, "Wrong PIN!", Toast.LENGTH_SHORT).show();
                    binding.pinMessage.setText(R.string.enter_pin);
                    pin = "";
                    holderPin = null;
                    handleDisplay();
                }
            }
        });
    }
    private void keyDown(int n){
        if(pin.length() < 4){
            pin += Integer.toString(n);
        }
        else {
            global.vibrate(80);
        }
        handleDisplay();
    }
    private void handleDisplay(){
        if(pin.length() == 0){
            binding.display.setText(getString(R.string.pin_empty));
        }
        else {
            StringBuilder display = new StringBuilder(pin);
            for(int i = 0; i < 4 - pin.length(); i++){
                display.append("-");
            }
            for(int i = 0; i < pin.length(); i++){
                display.setCharAt(i, '*');
            }
            binding.display.setText(display.toString());
        }
    }
}