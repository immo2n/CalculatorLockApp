package com.ucllc.smcalculatorlock.Custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ucllc.smcalculatorlock.Pages.Browser;
import com.ucllc.smcalculatorlock.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

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
    @SuppressLint("SetJavaScriptEnabled")
    public WebView initBrowser(WebView view, ProgressBar progressBar, ImageView iconView, EditText addressBar, LinearLayout errorView, SwipeRefreshLayout swipe){
        if(view.getVisibility() == WebView.GONE) {
            errorView.setVisibility(LinearLayout.GONE);
            view.setVisibility(WebView.VISIBLE);
        }
        String token = Objects.requireNonNull(decrypt(Config.BROWSER_VALIDATOR));
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("FACEBOOK_PAGE_LINK_FLAG")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        Objects.requireNonNull(activity).startActivity(intent);
                        return true;
                    } catch (ActivityNotFoundException e) {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if(error.getDescription().equals("net::ERR_FAILED")){
                    view.reload();
                    return;
                }
                view.setVisibility(WebView.GONE);
                errorView.setVisibility(LinearLayout.VISIBLE);
                TextView message = errorView.findViewById(R.id.failedMessage);
                Button tryAgain = errorView.findViewById(R.id.tryAgain);
                message.setText(error.getDescription());
                tryAgain.setOnClickListener(v -> {
                    view.setVisibility(WebView.VISIBLE);
                    errorView.setVisibility(LinearLayout.GONE);
                    view.reload();
                });
                swipe.setRefreshing(false);
            }
        });
        view.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE) progressBar.setVisibility(ProgressBar.VISIBLE);
                if(newProgress == 100){
                    progressBar.setVisibility(ProgressBar.GONE);
                    swipe.setRefreshing(false);
                    if(view.getVisibility() == WebView.GONE) {
                        errorView.setVisibility(LinearLayout.GONE);
                        view.setVisibility(WebView.VISIBLE);
                    }
                }
                addressBar.setText((token.equals(view.getUrl()))?"":view.getUrl());
                if(token.equals(view.getUrl())){
                    Browser.homeCall = true;
                    addressBar.setText("");
                    iconView.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(context), R.drawable.browser));
                }
                else {
                    Browser.homeCall = false;
                    addressBar.setText(view.getUrl());
                }
            }
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                iconView.setImageBitmap(icon);
            }
        });
        view.loadUrl(Objects.requireNonNull(decrypt(Config.BROWSER_VALIDATOR)));
        return view;
    }
}