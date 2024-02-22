package com.ucllc.smcalculatorlock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ucllc.smcalculatorlock.CalculatorPages.CalculatorHistory;
import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Interfaces.CalculatorEvalCallback;
import com.ucllc.smcalculatorlock.databinding.ActivityCalculatorBinding;

import java.util.ArrayList;
import java.util.List;

public class Calculator extends AppCompatActivity {
    protected ActivityCalculatorBinding binding;
    protected WebView evalClient;
    private String expression;
    private List<String> history;
    protected DBHandler dbHandler;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        evalClient = new WebView(this);
        evalClient.getSettings().setJavaScriptEnabled(true);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));
        expression = ""; //init
        history = new ArrayList<>();
        dbHandler = new DBHandler(this);

        //Hooks
        binding.history.setOnClickListener(view -> {
            startActivity(new Intent(Calculator.this, CalculatorHistory.class));
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
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
            if(null != expression && expression.length() > 0){
                eval(expression, this::displayResult);
            }
        });
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
    public String signFix(String expression){
        return expression.replace("*", "×").replace("/", "÷");
    }
}