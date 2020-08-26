package com.zergitas.linhlee.facebookusagelimited.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.zergitas.linhlee.facebookusagelimited.MyApplication;
import com.zergitas.linhlee.facebookusagelimited.utils.Constant;

/**
 * Created by Linh Lee on 11/21/2016.
 */
public class DeviceBootCompletedReceiver extends BroadcastReceiver {
    private MyApplication app;
    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        app = (MyApplication) context.getApplicationContext();
        sharedPreferences = app.getSharedPreferences();
        if (sharedPreferences.getBoolean("isLocking", true)) {
            if (!Constant.isMyServiceRunning(context, LockAppService.class)) {
                Intent lockService = new Intent(context, LockAppService.class);
                context.startService(lockService);
            }
        }
    }
}
