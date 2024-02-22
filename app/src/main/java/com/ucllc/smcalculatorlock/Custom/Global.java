package com.ucllc.smcalculatorlock.Custom;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Global {

    //**********************GLOBAL CONFIG STARTS**********************//
    public static boolean LOG_ERRORS = true;
    public static final String LOG_TAG = "UNICORE_DEV";
    //**********************GLOBAL CONFIG ENDS**********************//

    protected final Context context;
    protected final Activity activity;

    public Global(@Nullable Context context, @Nullable Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public Context getContext() {
        return context;
    }

    public Activity getActivity() {
        return activity;
    }

    public static void log(Exception e){
        if(LOG_ERRORS) Log.d(LOG_TAG, e.toString());
    }
    public static void logError(Exception e){
        if(LOG_ERRORS) Log.e(LOG_TAG, e.toString());
    }
}
