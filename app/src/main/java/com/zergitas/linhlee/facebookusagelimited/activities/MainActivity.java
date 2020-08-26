package com.zergitas.linhlee.facebookusagelimited.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zergitas.linhlee.facebookusagelimited.MyApplication;
import com.zergitas.linhlee.facebookusagelimited.R;
import com.zergitas.linhlee.facebookusagelimited.dialogs.SelectLanguageDialog;
import com.zergitas.linhlee.facebookusagelimited.services.LockAppService;
import com.zergitas.linhlee.facebookusagelimited.services.WindowChangeDetectingService;
import com.zergitas.linhlee.facebookusagelimited.utils.Constant;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MyApplication app;
    private SharedPreferences sharedPreferences;
    private Intent lockIntent;
    private ImageView enableButton;
    private ImageView flagImg;
    private ImageView settingsButton;
    private ImageView graphButton;
    private ImageView moreButton;
    private TextView timeRemainingText;
    private int timeRemaining, hour, minute, second;
    private boolean isEnable;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (!Constant.checkUsageAccess(this)) {
                showAlertDialog();
            }
        }

        app = (MyApplication) getApplication();
        sharedPreferences = app.getSharedPreferences();
        isEnable = sharedPreferences.getBoolean("isLocking", false);
        lockIntent = new Intent(this, LockAppService.class);

        enableButton = (ImageView) findViewById(R.id.enable_button);
        flagImg = (ImageView) findViewById(R.id.flag);
        settingsButton = (ImageView) findViewById(R.id.settings_btn);
        graphButton = (ImageView) findViewById(R.id.graph_btn);
        moreButton = (ImageView) findViewById(R.id.more_btn);
        timeRemainingText = (TextView) findViewById(R.id.time_remaining_text);

        setTimeText();

        if (isEnable) {
            enableButton.setImageResource(R.drawable.img_disable);
        } else {
            enableButton.setImageResource(R.drawable.img_enable);
        }
        if (sharedPreferences.getString("language", "Tiếng Việt").equals("English")) {
            flagImg.setImageResource(R.mipmap.flag_en);
        } else if (sharedPreferences.getString("language", "Tiếng Việt").equals("Tiếng Việt")) {
            flagImg.setImageResource(R.mipmap.flag_vi);
        }

        enableButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        graphButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);
        flagImg.setOnClickListener(this);

        IntentFilter filter = new IntentFilter("update_time");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setTimeText();
            }
        };
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.hideNavigationBar(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Constant.hideNavigationBar(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enable_button:
                if (isEnable) {
                    enableButton.setImageResource(R.drawable.img_enable);
                    sharedPreferences.edit().putBoolean("isLocking", false).apply();
                    isEnable = sharedPreferences.getBoolean("isLocking", false);
                    stopService(lockIntent);
                    Toast.makeText(MainActivity.this, "Disabled", Toast.LENGTH_SHORT).show();
                } else {
                    enableButton.setImageResource(R.drawable.img_disable);
                    sharedPreferences.edit().putBoolean("isLocking", true).apply();
                    isEnable = sharedPreferences.getBoolean("isLocking", false);
                    startService(lockIntent);
                    Toast.makeText(MainActivity.this, "Enabled", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.flag:
                SelectLanguageDialog dialog = new SelectLanguageDialog(MainActivity.this, new SelectLanguageDialog.ItemClicked() {
                    @Override
                    public void engClicked() {
                        sharedPreferences.edit().putString("language", "English").apply();
                        changeLanguage("en-US");
                    }

                    @Override
                    public void viClicked() {
                        sharedPreferences.edit().putString("language", "Tiếng Việt").apply();
                        changeLanguage("vi");
                    }
                });
                dialog.show();
                break;
            case R.id.settings_btn:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                break;
            case R.id.graph_btn:
                Intent graphIntent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(graphIntent);
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                break;
            case R.id.more_btn:
                break;
        }
    }

    private void setTimeText() {
        timeRemaining = sharedPreferences.getInt("timeRemaining", 4 * 60 * 60 * 1000);
        hour = timeRemaining / (1000 * 60 * 60);
        minute = (timeRemaining / (1000 * 60)) % 60;
        second = (timeRemaining / 1000) % 60;

        timeRemainingText.setText(getResources().getString(R.string.you_have) + " " + Constant.formatTime(hour) + " hours " + Constant.formatTime(minute) + " minutes " + Constant.formatTime(second) + " seconds " + getResources().getString(R.string.left));
    }

    private void changeLanguage(String languageToLoad) {
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        finish();
        startActivity(getIntent());
    }

    private void showAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(getResources().getString(R.string.notification));
        dialog.setMessage(getResources().getString(R.string.notification_content));
        dialog.setPositiveButton(getResources().getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.show();
    }
}
