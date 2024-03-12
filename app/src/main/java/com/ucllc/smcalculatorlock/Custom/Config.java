package com.ucllc.smcalculatorlock.Custom;

import android.util.Pair;

public class Config {
    public static final String LISENCE_KEY = "p$4yXiD(UFga&;|j,F&T;^Ig6#z[qmNgCR4:Q,{D}?bf}_/r$'";
    /*!!!!*******************************************************************!!!!*/
    /*******NEVER CHANGE THESE, IF CHANGED OLD DATA RECOVERY WILL NOT WORK - APP WILL CRASH*******/
    /*Here 1st string is the encryption key and 2nd string is the version of the encryption key.
    If the encryption key is changed, the version should also be changed.
    Also, the VALIDATOR should not be changed. If changed browser will not work.*
    */
    private static final Pair<String, String> ENCRYPTION_KEY_PAIR = new Pair<>("JX[6AozP3jq1:^=F:Nr1m>/,t@>vU+8329", "1.0");
    public static final String BROWSER_VALIDATOR = "jySI0uaUVT4xTtqPJ0X3xWXYwsSjxHtGyRup_TN4dT1os9BgtmPJHr9GpLTqdFEeZ2h4kNPI7dXyunpNXswXXLwG8J9zLV-H-OK24W1B3fU";
    /*!!!!*******************************************************************!!!!*/
    protected static final String ENCRYPTION_KEY = ENCRYPTION_KEY_PAIR.first;
    public static final String ENCRYPTION_VERSION = ENCRYPTION_KEY_PAIR.second;
    public static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";
    public static final String AD_UNIT_ID_OPEN_APP = "ca-app-pub-3940256099942544/9257395921";
    public static void rCheck(boolean t){
        if(t) throw new RuntimeException();
    }
}
