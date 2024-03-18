package com.sm.calculatorlock.Pages;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.sm.calculatorlock.Custom.Config;
import com.sm.calculatorlock.Custom.Global;
import com.sm.calculatorlock.Helpers.AdLoader;
import com.sm.calculatorlock.R;
import com.sm.calculatorlock.databinding.ActivityBrowserBinding;

import java.util.Objects;

public class Browser extends AppCompatActivity {
    public static boolean homeCall = false;
    ActivityBrowserBinding binding;
    WebView browser;
    private Global global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrowserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnight_black_button));
        global = new Global(this, this);
        browser = global.initBrowser(binding.view, binding.progress, binding.urlIcon,
                binding.urlInput, binding.error, binding.swipe);

        //Auto ad in 3s
        new Handler(Looper.getMainLooper()).postDelayed(() -> runOnUiThread(() -> new AdLoader(Browser.this, Browser.this, AdLoader::loadInterstitialAd)), 3000);

        binding.urlInput.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String inputUrl = textView.getText().toString();
                Uri uri = Uri.parse(inputUrl);
                //Clear focus and hide keyboard
                binding.urlInput.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                if (uri.getScheme() == null) {
                    inputUrl = "https://" + inputUrl;
                    uri = Uri.parse(inputUrl);
                }
                if (uri.getHost() == null || !global.hasTopLevelDomain(uri.getHost())){
                    global.openAsSearch(binding.view, inputUrl.replace("https://", ""));
                    return true;
                }
                //Clear the views
                binding.error.setVisibility(View.GONE);
                binding.swipe.setVisibility(View.VISIBLE);
                browser.loadUrl(uri.toString());
                return true;
            }
            return false;
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(homeCall){
                    finish();
                    return;
                }
                if (browser.canGoBack()) {
                    browser.goBack();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        binding.swipe.setOnRefreshListener(() -> {
            binding.error.setVisibility(View.GONE);
            binding.swipe.setVisibility(View.VISIBLE);
            browser.reload();
        });


        binding.urlIcon.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(Browser.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialogue_incognito);
            if(null != dialog.getWindow()){
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(Browser.this, R.drawable.dialogue_background));
            }
            dialog.show();
        });
        binding.home.setOnClickListener(view -> {
            browser.clearCache(true);
            browser.clearHistory();
            browser.clearFormData();
            browser.clearMatches();
            browser.clearSslPreferences();
            browser.clearDisappearingChildren();
            browser.clearFocus();
            browser.clearAnimation();
            browser.clearMatches();
            global.browserTryAgain = true;
            browser.loadUrl(Objects.requireNonNull(global.decrypt(Config.BROWSER_VALIDATOR)));
            //Clear focus and hide keyboard
            binding.urlInput.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.urlInput.getWindowToken(), 0);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        browser.clearCache(true);
        browser.destroy();
    }
}