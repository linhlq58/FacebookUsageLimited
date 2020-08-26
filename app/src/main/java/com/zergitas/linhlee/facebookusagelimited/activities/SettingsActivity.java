package com.zergitas.linhlee.facebookusagelimited.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zergitas.linhlee.facebookusagelimited.MyApplication;
import com.zergitas.linhlee.facebookusagelimited.R;
import com.zergitas.linhlee.facebookusagelimited.dialogs.SelectUnitsDialog;
import com.zergitas.linhlee.facebookusagelimited.utils.Constant;
import com.zergitas.linhlee.facebookusagelimited.utils.InputFilterMinMax;

/**
 * Created by Linh Lee on 11/22/2016.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout saveButton;
    private EditText editUnits;
    private EditText editValue;
    private String units;
    private String language;
    private int value;
    private MyApplication app;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        app = (MyApplication) getApplication();
        sharedPreferences = app.getSharedPreferences();

        saveButton = (RelativeLayout) findViewById(R.id.save_button);
        editUnits = (EditText) findViewById(R.id.edit_units);
        editValue = (EditText) findViewById(R.id.edit_value);

        units = sharedPreferences.getString("units", "Hour");
        value = sharedPreferences.getInt("value", 4);
        language = sharedPreferences.getString("language", "English");

        editUnits.setText(units);
        editValue.setText(value + "");

        if (units.equals("Hour")) {
            editValue.setFilters(new InputFilter[]{new InputFilterMinMax("1", "4")});
        } else if (units.equals("Minute")) {
            editValue.setFilters(new InputFilter[]{new InputFilterMinMax("1", "59")});
        } else if (units.equals("Second")) {
            editValue.setFilters(new InputFilter[]{new InputFilterMinMax("1", "59")});
        }

        saveButton.setOnClickListener(this);
        editUnits.setOnClickListener(this);

        Constant.increaseHitArea(editUnits);
        Constant.increaseHitArea(editValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constant.hideNavigationBar(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Constant.hideNavigationBar(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                if (sharedPreferences.getBoolean("isLocking", false)) {
                    Toast.makeText(SettingsActivity.this, "Disable lock before saving your settings", Toast.LENGTH_SHORT).show();
                } else {
                    value = Integer.parseInt(editValue.getText().toString());
                    sharedPreferences.edit().putInt("value", value).apply();
                    if (editUnits.getText().toString().equals("Hour")) {
                        sharedPreferences.edit().putString("units", "Hour").apply();
                        sharedPreferences.edit().putInt("timeRemaining", value * 60 * 60 * 1000).apply();
                    } else if (editUnits.getText().toString().equals("Minute")) {
                        sharedPreferences.edit().putString("units", "Minute").apply();
                        sharedPreferences.edit().putInt("timeRemaining", value * 60 * 1000).apply();
                    } else if (editUnits.getText().toString().equals("Second")) {
                        sharedPreferences.edit().putString("units", "Second").apply();
                        sharedPreferences.edit().putInt("timeRemaining", value * 1000).apply();
                    }

                    Intent i = new Intent("update_time");
                    sendBroadcast(i);
                    finish();
                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                    Toast.makeText(SettingsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edit_units:
                SelectUnitsDialog unitsDialog = new SelectUnitsDialog(this, new SelectUnitsDialog.ItemClicked() {
                    @Override
                    public void hourClicked() {
                        editUnits.setText(getResources().getString(R.string.hour));
                        editValue.setFilters(new InputFilter[]{new InputFilterMinMax("1", "4")});
                    }

                    @Override
                    public void minuteClicked() {
                        editUnits.setText(getResources().getString(R.string.minute));
                        editValue.setFilters(new InputFilter[]{new InputFilterMinMax("1", "59")});
                    }

                    @Override
                    public void secondClicked() {
                        editUnits.setText(getResources().getString(R.string.second));
                        editValue.setFilters(new InputFilter[]{new InputFilterMinMax("1", "59")});
                    }
                });
                unitsDialog.show();
                break;
        }
    }
}
