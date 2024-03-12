package com.ucllc.smcalculatorlock.Helpers;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ucllc.smcalculatorlock.Custom.Config;
import com.ucllc.smcalculatorlock.Custom.Global;

public class AdLoader {
    public static boolean interstitialAdBusy = false, openAppAdBusy = false;
    public interface OnAdLoaderReady{
        void ready(AdLoader adLoader);
    }
    private final Context context;
    private final Activity activity;
    public AdLoader(Context context, Activity activity, OnAdLoaderReady onAdLoaderReady) {
        this.context = context;
        this.activity = activity;
        MobileAds.initialize(context, initializationStatus -> onAdLoaderReady.ready(this));
    }
    public void loadInterstitialAd() {
        /*
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAdBusy = true;
        InterstitialAd.load(context, Config.AD_UNIT_ID_INTERSTITIAL, adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialAd.show(activity);
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                interstitialAdBusy = false;
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                interstitialAdBusy = false;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                interstitialAdBusy = false;
                            }
                        });
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Global.logError(new Exception("Ad failed to load: " + loadAdError.getMessage()));
                        interstitialAdBusy = false;
                    }
                });
         */
        }
        public void loadOpenAppAd(){
        /*
            openAppAdBusy = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context, Config.AD_UNIT_ID_OPEN_APP, adRequest,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd ad) {
                            openAppAdBusy = false;
                            ad.show(activity);
                        }
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Global.logError(new Exception("Ad failed to load: " + loadAdError.getMessage()));
                            openAppAdBusy = false;
                        }
                    });
         */
        }
}
