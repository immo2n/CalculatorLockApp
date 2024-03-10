package com.ucllc.smcalculatorlock.Custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ucllc.smcalculatorlock.Helpers.AppLockForegroundService;
public class LockScheduleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AppLockForegroundService.class);
        context.startService(serviceIntent);
        LockCheckSchedule.scheduleAlarms(context);
    }
}
