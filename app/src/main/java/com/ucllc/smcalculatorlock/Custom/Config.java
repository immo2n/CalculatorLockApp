package com.ucllc.smcalculatorlock.Custom;

import android.util.Pair;

public class Config {
    /*!!!!*******************************************************************!!!!*/
    /*******NEVER CHANGE THESE, IF CHANGED OLD DATA RECOVERY WILL NOT WORK - APP WILL CRASH*******/
    /*Here 1st string is the encryption key and 2nd string is the version of the encryption key.
    If the encryption key is changed, the version should also be changed.
    Also, the VALIDATOR should not be changed. If changed browser will not work.*
    */
    private static final Pair<String, String> ENCRYPTION_KEY_PAIR = new Pair<>("JX[6AozP3jq1:^=F:Nr1m>/,t@>vU+8329", "1.0");
    public static final String BROWSER_VALIDATOR = "wpiRgMA5XvhNdYZAa4z_nTJh4hFVGwQx6W8ljtNRT3dzgn8qOszFPFatPJ7h4F_vSThAclxk-YLE-si_cm2dAYcF2ZIjnDQNvWqiyv1b4T0";
    /*!!!!*******************************************************************!!!!*/
    protected static final String ENCRYPTION_KEY = ENCRYPTION_KEY_PAIR.first;
    public static final String ENCRYPTION_VERSION = ENCRYPTION_KEY_PAIR.second;
}
