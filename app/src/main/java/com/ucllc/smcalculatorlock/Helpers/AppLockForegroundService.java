package com.ucllc.smcalculatorlock.Helpers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ucllc.smcalculatorlock.Custom.DBHandler;
import com.ucllc.smcalculatorlock.Custom.Global;
import com.ucllc.smcalculatorlock.R;

import java.util.Timer;
import java.util.TimerTask;

public class AppLockForegroundService extends Service {

    private UsageStatsManager usageStatsManager;
    private String lastForegroundApp;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "AppLockServiceChannel";

    private DBHandler dbHandler;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        dbHandler = new DBHandler(this);
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForegroundApp();
            }
        }, 0, 1000);
        return START_STICKY;
    }

    private void checkForegroundApp() {
        UsageEvents usageEvents = usageStatsManager.queryEvents(System.currentTimeMillis() - 1000, System.currentTimeMillis());
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                String packageName = event.getPackageName();
                if (!packageName.equals(lastForegroundApp)) {
                    if (isAppLocked(packageName)) {
                        try {
                            if (Settings.canDrawOverlays(this)) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    UnlockView unlockView = new UnlockView(getApplicationContext(), packageName);
                                    WindowManager.LayoutParams params;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        params = new WindowManager.LayoutParams(
                                                WindowManager.LayoutParams.MATCH_PARENT,
                                                WindowManager.LayoutParams.MATCH_PARENT,
                                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                                                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                                                PixelFormat.TRANSLUCENT);
                                    } else {
                                        params = new WindowManager.LayoutParams(
                                                WindowManager.LayoutParams.MATCH_PARENT,
                                                WindowManager.LayoutParams.MATCH_PARENT,
                                                WindowManager.LayoutParams.TYPE_PHONE,
                                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                                                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                                                PixelFormat.TRANSLUCENT);
                                    }
                                    WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                                    windowManager.addView(unlockView, params);
                                });
                            }
                        } catch (Exception e) {
                            Global.log(e);
                        }

                    }
                    lastForegroundApp = packageName;
                    break;
                }
            }
        }
    }

    private boolean isAppLocked(String packageName) {
        return dbHandler.isAppLocked(packageName);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Lock Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App Lock Service")
                .setContentText("Monitoring app usage")
                .setSmallIcon(R.mipmap.ic_launcher_round);
        return builder.build();
    }
}