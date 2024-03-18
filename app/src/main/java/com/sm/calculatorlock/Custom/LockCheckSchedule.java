package com.sm.calculatorlock.Custom;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class LockCheckSchedule {
    private static final int INTERVAL_SECONDS = 60; //Repeat every 60 seconds
    public static void scheduleAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LockScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        long currentTimeMillis = SystemClock.elapsedRealtime() + (INTERVAL_SECONDS * 1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeMillis, pendingIntent);
    }
}