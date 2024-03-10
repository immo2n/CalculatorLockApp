package com.ucllc.smcalculatorlock.Custom;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class LockCheckSchedule {
    private static final int INTERVAL_TEN_SECONDS = 1;
    public static void scheduleAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LockScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        long currentTimeMillis = SystemClock.elapsedRealtime() + (INTERVAL_TEN_SECONDS * 1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeMillis, pendingIntent);
    }
}