package com.ucllc.smcalculatorlock;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;
import android.Manifest;

import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.DataClasses.StateKeys;
import com.ucllc.smcalculatorlock.Pages.Calculator;
import com.ucllc.smcalculatorlock.Pages.PinSetup;
import com.ucllc.smcalculatorlock.databinding.ActivitySetupBinding;

public class Setup extends AppCompatActivity {
    ActivitySetupBinding binding;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private final int PERMISSION_CODE = 100;
    private boolean permissionCheck = false;
    private DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black));
        dbHandler = new DBHandler(this);

        //Hooks
        binding.dontAgree.setOnClickListener(v -> {
            finish();
            System.exit(0);
        });
        binding.continueButton.setOnClickListener(v-> {
            if(permissionCheck){
                completeSetup();
                return;
            }
            //Ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                //Permission for android 13+
                String[] PERMISSIONS = {
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                };
                boolean permission = true;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
                    permission = false;
                }
                if(!Environment.isExternalStorageManager()){
                    Intent permission_intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,  Uri.parse("package:" + getPackageName()));
                    startActivity(permission_intent);
                    Toast.makeText(getBaseContext(), "Please turn on files permission.", Toast.LENGTH_LONG).show();
                    permission = false;
                }
                if(permission) completeSetup();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if(!Environment.isExternalStorageManager()){
                        Intent permission_intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,  Uri.parse("package:" + getPackageName()));
                        startActivity(permission_intent);
                        Toast.makeText(getBaseContext(), "Please turn on files permission.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        completeSetup();
                    }
                }
                else {
                    //Permission for android 9 and below
                    requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (!isGranted) {
                            Toast.makeText(this, "Allow permission from settings to continue!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    boolean permission = true;
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        permission = false;
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        permission = false;
                    }
                    if(permission) completeSetup();
                }
            }
            permissionCheck = true;
        });
    }
    private void completeSetup(){
        dbHandler.setAppState(StateKeys.SETUP, StateKeys.VALUE_TRUE);
        if(dbHandler.getStateValue(StateKeys.PIN) == null){
            startActivity(new Intent(Setup.this, PinSetup.class));
            finish();
            return;
        }
        startActivity(new Intent(Setup.this, Calculator.class));
        finish();
    }
}