package com.ucllc.smcalculatorlock.Custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Global {

    //**********************GLOBAL STATE KEYS STARTS**********************//
    public static boolean LOG_ERRORS = true;
    public static final String LOG_TAG = "UNICORE_DEV";
    //**********************GLOBAL STATE KEYS ENDS**********************//

    private final Context context;
    private final Activity activity;
    private final DBHandler dbHandler;

    public Global(@Nullable Context context, @Nullable Activity activity) {
        this.context = context;
        this.activity = activity;
        this.dbHandler = (null != context)?new DBHandler(context):null;
    }

    @Nullable public Context getContext() {
        return context;
    }

    @Nullable public Activity getActivity() {
        return activity;
    }
    public static void log(Exception e){
        if(LOG_ERRORS) Log.d(LOG_TAG, e.toString());
    }
    public static void logError(Exception e){
        if(LOG_ERRORS) Log.e(LOG_TAG, e.toString());
    }
    private SecretKeySpec secretKey;
    @Nullable public String decrypt(@NonNull String strToDecrypt) {
        try {
            prepareSecreteKey();
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt, android.util.Base64.URL_SAFE | android.util.Base64.NO_PADDING)));
        } catch (Exception e) {
            return null;
        }
    }
    @Nullable public String encrypt(@NonNull String strToEncrypt) {
        try {
            prepareSecreteKey();
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return android.util.Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)), android.util.Base64.URL_SAFE | android.util.Base64.NO_PADDING)
                    .replaceAll(System.lineSeparator(), "");
        } catch (Exception e) {
            Global.logError(e);
            return null;
        }
    }
    public void prepareSecreteKey() {
        MessageDigest sha;
        try {
            byte[] key = Config.ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            Global.logError(e);
        }
    }
    public void vibrate(long durationMS) {
        if(null == activity) return;
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(durationMS, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(durationMS);
            }
        }
    }
    @Nullable public DBHandler getDBHandler(){
        return dbHandler;
    }
}