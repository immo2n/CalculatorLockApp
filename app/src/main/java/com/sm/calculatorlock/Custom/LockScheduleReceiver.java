package com.sm.calculatorlock.Custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sm.calculatorlock.Helpers.AppLockForegroundService;
public class LockScheduleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AppLockForegroundService.class);
        context.startService(serviceIntent);
        LockCheckSchedule.scheduleAlarms(context);
    }
}
