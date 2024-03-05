package com.ucllc.smcalculatorlock.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.health.connect.datatypes.AppInfo;
import android.os.Build;

import com.ucllc.smcalculatorlock.Custom.Global;

import java.util.ArrayList;
import java.util.List;

public class AppListHelper {
    @SuppressLint("QueryPermissionsNeeded")
    public static List<AppInfo> getInstalledApps(Context context) {
        List<AppInfo> installedApps = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            apps = packageManager.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
        } else {
            apps = packageManager.queryIntentActivities(intent, 0);
        }

        for (ResolveInfo appInfo : apps) {
            AppInfo app = new AppInfo();
            app.setAppName(appInfo.loadLabel(packageManager).toString());
            app.setPackageName(appInfo.activityInfo.packageName);
            app.setIcon(appInfo.loadIcon(packageManager));
            installedApps.add(app);
        }

        return installedApps;
    }

    public static class AppInfo {
        private String appName;
        private String packageName;
        private Drawable icon;

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }
    }
}
