package com.ucllc.smcalculatorlock.Custom;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ucllc.smcalculatorlock.Pages.Browser;
import com.ucllc.smcalculatorlock.R;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
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
    private boolean browserTryAgain = false;
    @SuppressLint("SetJavaScriptEnabled")
    public WebView initBrowser(WebView view, ProgressBar progressBar, ImageView iconView, EditText addressBar, LinearLayout errorView, SwipeRefreshLayout swipe){
        if(view.getVisibility() == WebView.GONE) {
            errorView.setVisibility(LinearLayout.GONE);
            swipe.setVisibility(WebView.VISIBLE);
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

                if (downloadAble(url)) {
                    startDownload(url);
                    return true;
                }

                return false;
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                browserTryAgain = false;
                Global.logError(new Exception(error.getDescription().toString()));
                if(error.getDescription().equals("net::ERR_FAILED") || error.getDescription().equals("net::ERR_CACHE_MISS")){
                    view.reload();
                    return;
                }
                swipe.setVisibility(WebView.GONE);
                TextView message = errorView.findViewById(R.id.failedMessage);
                Button tryAgain = errorView.findViewById(R.id.tryAgain);
                message.setText(error.getDescription());
                tryAgain.setOnClickListener(v -> {
                    browserTryAgain = true;
                    view.reload();
                });
                swipe.setRefreshing(false);
                errorView.setVisibility(LinearLayout.VISIBLE);
            }
        });
        view.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE) progressBar.setVisibility(ProgressBar.VISIBLE);

                if(newProgress == 100){
                    if(browserTryAgain){
                        errorView.setVisibility(LinearLayout.GONE);
                        swipe.setVisibility(WebView.VISIBLE);
                        browserTryAgain = false;
                    }
                    progressBar.setVisibility(ProgressBar.GONE);
                    swipe.setRefreshing(false);
                }

                addressBar.setText((token.equals(view.getUrl()))?"":view.getUrl());
                if(token.equals(view.getUrl())){
                    Browser.homeCall = true;
                    iconView.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(context), R.drawable.browser));
                }
                else {
                    Browser.homeCall = false;
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

    private boolean downloadAble(String url) {
        return url.endsWith(".mp4") || url.endsWith(".avi") || url.endsWith(".mov") || url.endsWith(".wmv") ||
                url.endsWith(".mp3") || url.endsWith(".wav") || url.endsWith(".ogg") || url.endsWith(".flac") ||
                url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".gif") ||
                url.endsWith(".bmp") || url.endsWith(".webp") || url.endsWith(".pdf") || url.endsWith(".doc") ||
                url.endsWith(".docx") || url.endsWith(".xls") || url.endsWith(".xlsx") || url.endsWith(".ppt") ||
                url.endsWith(".pptx") || url.endsWith(".zip") || url.endsWith(".rar") || url.endsWith(".tar") ||
                url.endsWith(".gz") || url.endsWith(".7z") || url.endsWith(".bz2") || url.endsWith(".xz") ||
                url.endsWith(".iso") || url.endsWith(".dmg") || url.endsWith(".exe") || url.endsWith(".apk") ||
                url.endsWith(".jar") || url.endsWith(".class") || url.endsWith(".war") || url.endsWith(".ear") ||
                url.endsWith(".tar.gz") || url.endsWith(".tar.bz2") || url.endsWith(".tar.xz") ||
                url.endsWith(".tgz") || url.endsWith(".zipx") || url.endsWith(".cab") || url.endsWith(".deb") ||
                url.endsWith(".rpm") || url.endsWith(".msi") || url.endsWith(".ai") ||
                url.endsWith(".psd") || url.endsWith(".eps") || url.endsWith(".svg") || url.endsWith(".svgz") ||
                url.endsWith(".torrent") || url.endsWith(".ico") || url.endsWith(".tif") || url.endsWith(".tiff") ||
                url.endsWith(".txt") || url.endsWith(".rtf") || url.endsWith(".csv") || url.endsWith(".xml") ||
                url.endsWith(".json") || url.endsWith(".ini") || url.endsWith(".cfg") || url.endsWith(".log") ||
                url.endsWith(".sh") || url.endsWith(".bat") || url.endsWith(".java") || url.endsWith(".cpp") ||
                url.endsWith(".h") || url.endsWith(".py") || url.endsWith(".cs") || url.endsWith(".jsp");
    }

    @NonNull public static String makeUrlSafe(@NonNull String input) {
        try {
            String encodedString = URLEncoder.encode(input, "UTF-8");
            encodedString = encodedString.replace("+", "%20");
            encodedString = encodedString.replace("*", "%2A");
            encodedString = encodedString.replace("%7E", "~");
            return encodedString;
        } catch (UnsupportedEncodingException e) {
            return input;
        }
    }
    public void openAsSearch(WebView view, String term){
        view.loadUrl("https://www.google.com/search?q=" + makeUrlSafe(term));
    }
    public boolean hasTopLevelDomain(@NonNull String host) {
        String[] parts = host.split("\\.");
        return parts.length > 1;
    }
    public static boolean hasOverlayPermission(@NonNull Context context) {
        return Settings.canDrawOverlays(context);
    }
    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = getSystemService(context, AppOpsManager.class);
        int mode = Objects.requireNonNull(appOps).checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    public static void propUsageStatsPermission(Context context, ActivityResultLauncher<Intent> accessibilityServiceLauncher) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Usage Access")
                .setMessage("We need Usage Access permission to lock apps. Please grant the permission.\nReason: To detect which app the user is opening.\nNecessity: Fundamental, can't function without it.")
                .setPositiveButton("Open settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    accessibilityServiceLauncher.launch(intent);
                })
                .setCancelable(false)
                .show();
    }
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    public String currentTimeStamp(){
        return String.valueOf(System.currentTimeMillis());
    }
    private void startDownload(String url) {
        if (context == null) return;
        Uri uri = Uri.parse(url);
        String filename = uri.getLastPathSegment();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("File Download");
        request.setDescription("Downloading file...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename); // Use the extracted filename
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Download Manager not available", Toast.LENGTH_SHORT).show();
        }
    }
}