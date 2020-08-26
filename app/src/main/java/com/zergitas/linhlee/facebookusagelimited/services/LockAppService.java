package com.zergitas.linhlee.facebookusagelimited.services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zergitas.linhlee.facebookusagelimited.MyApplication;
import com.zergitas.linhlee.facebookusagelimited.activities.LockActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by Linh Lee on 11/19/2016.
 */
public class    LockAppService extends Service {
    private MyApplication app;
    private SharedPreferences sharedPreferences;
    private Timer timer;
    private int timeRemaining;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private BroadcastReceiver receiverScreenOff, receiverScreenOn, receiverTimeTick;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = (MyApplication) getApplication();
        sharedPreferences = app.getSharedPreferences();
        timeRemaining = sharedPreferences.getInt("timeRemaining", 4 * 60 * 60 * 1000);
        timer = new Timer();
        startService();

        if (!sharedPreferences.getString("stopDate", dateFormat.format(new Date())).equals(dateFormat.format(new Date()))) {
            String units = sharedPreferences.getString("units", "Hour");
            int value = sharedPreferences.getInt("value", 4);
            if (units.equals("Hour")) {
                sharedPreferences.edit().putInt("timeRemaining", value * 60 * 60 * 1000).apply();
            } else if (units.equals("Minute")) {
                sharedPreferences.edit().putInt("timeRemaining", value * 60 * 1000).apply();
            } else if (units.equals("Second")) {
                sharedPreferences.edit().putInt("timeRemaining", value * 1000).apply();
            }
            timeRemaining = sharedPreferences.getInt("timeRemaining", 4 * 60 * 60 * 1000);
        }
        Log.d("time remaining", timeRemaining + "");

        IntentFilter filterScreenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        receiverScreenOff = new ScreenOffReceiver();
        registerReceiver(receiverScreenOff, filterScreenOff);

        IntentFilter filterScreenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        receiverScreenOn = new ScreenOnReceiver();
        registerReceiver(receiverScreenOn, filterScreenOn);

        IntentFilter filterTimeTick = new IntentFilter(Intent.ACTION_TIME_TICK);
        receiverTimeTick = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Calendar calendar = Calendar.getInstance();
                if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) {
                    String units = sharedPreferences.getString("units", "Hour");
                    int value = sharedPreferences.getInt("value", 4);
                    if (units.equals("Hour")) {
                        sharedPreferences.edit().putInt("timeRemaining", value * 60 * 60 * 1000).apply();
                    } else if (units.equals("Minute")) {
                        sharedPreferences.edit().putInt("timeRemaining", value * 60 * 1000).apply();
                    } else if (units.equals("Second")) {
                        sharedPreferences.edit().putInt("timeRemaining", value * 1000).apply();
                    }
                }
            }
        };
        registerReceiver(receiverTimeTick, filterTimeTick);

    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 500);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
        }
    }

    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String topActivity = "";

            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
                topActivity = runningAppProcessInfo.get(0).processName;
            } else {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
                long endTime = System.currentTimeMillis();
                long beginTime = endTime - 1000*60;

                // We get usage stats for the last minute
                List<UsageStats> stats = null;
                stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);
                if(stats != null)
                {
                    SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                    for (UsageStats usageStats : stats)
                    {
                        mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                    }
                    if(mySortedMap != null && !mySortedMap.isEmpty())
                    {
                        topActivity =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    }
                }
            }

            //Log.d("activityOnTop", topActivity);
            if (topActivity.equals("com.facebook.katana")) {
                if (sharedPreferences.getBoolean("isLocking", false)) {
                    if (timeRemaining > 0) {
                        timeRemaining -= 0.5 * 1000;
                        Log.d("time remaining", timeRemaining + "");
                    }

                    if (timeRemaining <= 0) {
                        Intent lockIntent = new Intent(getApplicationContext(), LockActivity.class);
                        lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(lockIntent);
                    }
                }
            }

            if (topActivity.equals("com.zergitas.linhlee.facebookusagelimited")) {
                if (sharedPreferences.getInt("timeRemaining", 4 * 60 * 60 * 1000) != timeRemaining)
                sharedPreferences.edit().putInt("timeRemaining", timeRemaining).apply();
                Intent i = new Intent("update_time");
                sendBroadcast(i);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().putInt("timeRemaining", timeRemaining).apply();
        sharedPreferences.edit().putString("stopDate", dateFormat.format(new Date())).apply();
        Log.d("stop date", dateFormat.format(new Date()));
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        if (receiverScreenOff != null) {
            unregisterReceiver(receiverScreenOff);
        }
        if (receiverScreenOn != null) {
            unregisterReceiver(receiverScreenOn);
        }
        if (receiverTimeTick != null) {
            unregisterReceiver(receiverTimeTick);
        }
    }

    public class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sharedPreferences.edit().putInt("timeRemaining", timeRemaining).apply();
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
        }
    }

    public class ScreenOnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (timer == null) {
                timer = new Timer();
                startService();
            }
        }
    }
}
