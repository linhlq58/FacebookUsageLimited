package com.zergitas.linhlee.facebookusagelimited;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Linh Lee on 11/19/2016.
 */
public class MyApplication extends Application {
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
