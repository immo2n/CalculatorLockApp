package com.ucllc.smcalculatorlock.Custom;

import android.util.Pair;

public class Config {
    /*!!!!*******************************************************************!!!!*/
    /*******NEVER CHANGE THIS, IF CHANGED OLD DATA RECOVERY WILL NOT WORK*******/
    /*Here 1st string is the encryption key and 2nd string is the version of the encryption key.
    If the encryption key is changed, the version should also be changed.
    */
    private static final Pair<String, String> ENCRYPTION_KEY_PAIR = new Pair<>("JX[6AozP3jq1:^=F:Nr1m>/,t@>vU+8329", "1.0");
    /*!!!!*******************************************************************!!!!*/
    protected static final String ENCRYPTION_KEY = ENCRYPTION_KEY_PAIR.first;
    public static final String ENCRYPTION_VERSION = ENCRYPTION_KEY_PAIR.second;
}
