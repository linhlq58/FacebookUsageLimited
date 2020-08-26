package com.zergitas.linhlee.facebookusagelimited.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.zergitas.linhlee.facebookusagelimited.R;
import com.zergitas.linhlee.facebookusagelimited.utils.Constant;

/**
 * Created by Linh Lee on 11/19/2016.
 */
public class LockActivity extends AppCompatActivity implements View.OnClickListener {
    private Button okButton;
    private Button rateUsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        okButton = (Button) findViewById(R.id.ok_btn);
        rateUsButton = (Button) findViewById(R.id.rate_btn);

        okButton.setOnClickListener(this);
        rateUsButton.setOnClickListener(this);
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
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_btn:
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                break;
            case R.id.rate_btn:
                break;
        }
    }
}
