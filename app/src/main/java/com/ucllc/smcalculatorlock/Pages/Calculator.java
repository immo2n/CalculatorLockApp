package com.ucllc.smcalculatorlock.Pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ucllc.smcalculatorlock.CalculatorPages.CalculatorHistory;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.Interfaces.CalculatorEvalCallback;
import com.ucllc.smcalculatorlock.R;
import com.ucllc.smcalculatorlock.Setup;
import com.ucllc.smcalculatorlock.Sheets.PrivacyNotice;
import com.ucllc.smcalculatorlock.databinding.ActivityCalculatorBinding;

import java.util.ArrayList;
import java.util.List;

public class Calculator extends AppCompatActivity {
    private ActivityCalculatorBinding binding;
    private WebView evalClient;
    private String expression;
    private List<String> history;
    private DBHandler dbHandler;
    private String homePin;
    private int invalidPinCount = 0;
    @SuppressLint({"SetJavaScriptEnabled", "InflateParams"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        startActivity(new Intent(Calculator.this, Home.class));
        finish();








        dbHandler = new DBHandler(this);
        /*SETUP CHECK STARTS*/
        if(dbHandler.getStateValue(StateKeys.SETUP) == null){
            startActivity(new Intent(Calculator.this, Setup.class));
            finish();
            return;
        }
        if(dbHandler.getStateValue(StateKeys.PIN) == null){
            startActivity(new Intent(Calculator.this, PinSetup.class));
            finish();
            return;
        }
        if(dbHandler.getStateValue(StateKeys.RECOVERY_PATTERN) == null){
            startActivity(new Intent(Calculator.this, PatternLock.class).putExtra("setup", true));
            finish();
            return;
        }
        /*SETUP CHECK ENDS*/
        binding = ActivityCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Global global = new Global(this, this);
        evalClient = new WebView(this);
        evalClient.getSettings().setJavaScriptEnabled(true);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));
        expression = "";
        history = new ArrayList<>();

        //Ready the PIN
        String rawPin = dbHandler.getStateValue(StateKeys.PIN);
        homePin = (null != rawPin)? global.decrypt(rawPin):null;
        if(null != rawPin && null == homePin) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Decryption error")
                    .setMessage("The app is unable to decrypt the stored key! Please contact the publishers.")
                    .setPositiveButton("I Understand", null)
                    .show();
        }

        //Hooks
        binding.history.setOnClickListener(view -> {
            startActivity(new Intent(Calculator.this, CalculatorHistory.class));
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        });

        binding.privacy.setOnClickListener(v-> {
            PrivacyNotice notice = new PrivacyNotice();
            notice.show(getSupportFragmentManager(), notice.getTag());
        });

        //If API Level 26 or higher
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            binding.display.setAutoSizeTextTypeUniformWithConfiguration(10, 100, 1, 1);
            binding.historyDisplay.setAutoSizeTextTypeUniformWithConfiguration(10, 40, 1, 1);
        }

        //AllClear
        binding.allClear.setOnClickListener(view -> {
            if(expression.length() > 0){
                expression = "";
                binding.display.setText("0");
            }
            else {
                history.clear();
                binding.historyDisplay.setText("");
            }
        });

        //Backspace
        binding.backSpace.setOnClickListener(view -> {
            if(null != expression && expression.length() > 0){
                expression = expression.substring(0, expression.length() - 1);
                binding.display.setText(signFix(expression));
            }
        });

        //Percentage
        binding.parcent.setOnClickListener(view -> {
            if(null != expression && expression.length() > 0){
                expression += "/100";
                eval(expression, this::displayResult);
            }
        });

        //Divide
        binding.divide.setOnClickListener(view -> {
            if(null != expression && expression.length() > 0){
                expression += "/";
                binding.display.setText(signFix(expression));
            }
        });

        //Seven
        binding.num7.setOnClickListener(view -> {
            expression += "7";
            binding.display.setText(signFix(expression));
        });

        //Eight
        binding.num8.setOnClickListener(view -> {
            expression += "8";
            binding.display.setText(signFix(expression));
        });

        //Nine
        binding.num9.setOnClickListener(view -> {
            expression += "9";
            binding.display.setText(signFix(expression));
        });

        //Multiply
        binding.multiply.setOnClickListener(view -> {
            if(null != expression && expression.length() > 0){
                expression += "*";
                binding.display.setText(signFix(expression));
            }
        });

        //Four
        binding.num4.setOnClickListener(view -> {
            expression += "4";
            binding.display.setText(signFix(expression));
        });

        //Five
        binding.num5.setOnClickListener(view -> {
            expression += "5";
            binding.display.setText(signFix(expression));
        });

        //Six
        binding.num6.setOnClickListener(view -> {
            expression += "6";
            binding.display.setText(signFix(expression));
        });

        //Minus
        binding.subtract.setOnClickListener(view -> {
            if(null != expression && expression.length() > 0){
                expression += "-";
                binding.display.setText(signFix(expression));
            }
        });

        //One
        binding.num1.setOnClickListener(view -> {
            expression += "1";
            binding.display.setText(signFix(expression));
        });

        //Two
        binding.num2.setOnClickListener(view -> {
            expression += "2";
            binding.display.setText(signFix(expression));
        });

        //Three
        binding.num3.setOnClickListener(view -> {
            expression += "3";
            binding.display.setText(signFix(expression));
        });

        //Plus
        binding.addition.setOnClickListener(view -> {
            if(null != expression && expression.length() > 0){
                expression += "+";
                binding.display.setText(signFix(expression));
            }
        });

        //Engineering
        binding.exp.setOnClickListener(view -> {
            if(null != expression && expression.length() > 0){
                expression += "e";
                binding.display.setText(signFix(expression));
            }
        });

        //Zero
        binding.num0.setOnClickListener(view -> {
            expression += "0";
            binding.display.setText(signFix(expression));
        });

        //Dot
        binding.dot.setOnClickListener(view -> {
            expression += ".";
            binding.display.setText(signFix(expression));
        });

        //Equal
        binding.equal.setOnClickListener(view -> {
            try {
                if(4 == expression.length() && 4 == checkLengthAsPin(expression)){
                    //Could be PIN
                    if(expression.equals(homePin)){
                        startActivity(new Intent(Calculator.this, Home.class));
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                        finish();
                        return;
                    }
                    else {
                        ++invalidPinCount;
                    }
                    if(invalidPinCount >= 3){
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                        builder.setTitle("Forgot PIN?")
                                .setMessage("It seems you have forgotten your PIN. Do you want to reset the PIN?")
                                .setPositiveButton("Yes, Reset", (dialogInterface, i) -> {
                                    startActivity(new Intent(Calculator.this, PatternLock.class));
                                    finish();
                                })
                                .setCancelable(false)
                                .setNegativeButton("No",(dialogInterface, i) -> {
                                    invalidPinCount = 0;
                                })
                                .show();
                    }
                }
            } catch (Exception e){
                //Do none
            }
            if(null != expression && expression.length() > 0){
                eval(expression, this::displayResult);
            }
        });
    }

    private int checkLengthAsPin(String expression) {
        int c = 0;
        for(int i = 0; i < expression.length(); ++i){
            try {
                int k = Integer.parseInt(String.valueOf(expression.charAt(i)));
                if(k >= 0 && k <= 10) ++c;
            }
            catch (Exception e){
                --c;
            }
        }
        return c;
    }

    private void eval(String expression, CalculatorEvalCallback callback){
        evalClient.evaluateJavascript(expression, s -> callback.onDone((s.equals("null") || s.equals("")) ? "Error" : s));
    }
    public void displayResult(String result){
        StringBuilder resultData = new StringBuilder();
        for(int i = 0; i < history.size(); i++){
            if(i == history.size() - 1){
                resultData.append(history.get(i));
            }
            else{
                resultData.append(history.get(i)).append("\n");
            }
        }
        binding.historyDisplay.setText(signFix(resultData.toString()));
        resultData = new StringBuilder();
        resultData.append(expression).append(" = ").append(result);
        binding.display.setText(signFix(resultData.toString()));
        if(!result.equals("Error")){
            if(history.size() >= 8){
                history.remove(0);
            }
            String historyData = expression + " = " + result;
            dbHandler.insertHistory(historyData);
            history.add(historyData);
            expression = result;
        }
    }
    public static String signFix(String expression){
        return expression.replace("*", "×").replace("/", "÷");
    }
}