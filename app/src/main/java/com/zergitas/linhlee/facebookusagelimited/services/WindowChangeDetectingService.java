package com.zergitas.linhlee.facebookusagelimited.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.zergitas.linhlee.facebookusagelimited.MyApplication;
import com.zergitas.linhlee.facebookusagelimited.activities.LockActivity;

/**
 * Created by Linh Lee on 11/19/2016.
 */
public class WindowChangeDetectingService extends AccessibilityService {
    private MyApplication app;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        app = (MyApplication) getApplication();
        sharedPreferences = app.getSharedPreferences();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (accessibilityEvent.getPackageName() != null && accessibilityEvent.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        accessibilityEvent.getPackageName().toString(),
                        accessibilityEvent.getClassName().toString()
                );

                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                if (isActivity) {
                    String currentApp = componentName.getPackageName();
                    Log.i("Current App", currentApp);
                    if (currentApp.equals("com.facebook.katana")) {
                        if (sharedPreferences.getBoolean("isLocking", true)) {
                            Intent lockIntent = new Intent(this, LockActivity.class);
                            lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(lockIntent);
                        }
                    }
                }
            }
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
