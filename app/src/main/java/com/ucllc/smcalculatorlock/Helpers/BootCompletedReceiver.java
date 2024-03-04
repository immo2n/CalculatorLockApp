package com.ucllc.smcalculatorlock.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class BootCompletedReceiver extends BroadcastReceiver {
    private final List<String> actionList = new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        actionList.add("android.intent.action.BOOT_COMPLETED");
        actionList.add("android.intent.action.QUICKBOOT_POWERON");
        actionList.add("android.intent.category.DEFAULT");
        actionList.add("android.intent.action.REBOOT");
        actionList.add("android.intent.action.LOCKED_BOOT_COMPLETED");
        if (intent.getAction() != null && actionList.contains(intent.getAction())) {
            Intent serviceIntent = new Intent(context, AppLockForegroundService.class);
            context.startService(serviceIntent);
        }
    }
}