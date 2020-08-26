package com.zergitas.linhlee.facebookusagelimited.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.zergitas.linhlee.facebookusagelimited.MyApplication;
import com.zergitas.linhlee.facebookusagelimited.R;
import com.zergitas.linhlee.facebookusagelimited.utils.Constant;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/22/2016.
 */
public class GraphActivity extends AppCompatActivity {
    private LineChart lineChart;
    private MyApplication app;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        app = (MyApplication) getApplication();
        sharedPreferences = app.getSharedPreferences();

        lineChart = (LineChart) findViewById(R.id.line_chart);
    }

    private ArrayList<Entry> setYAxisValues() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(2f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));

        return entries;
    }

    private ArrayList<String> setXAxisValues() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Mon");
        labels.add("Tue");
        labels.add("Wed");
        labels.add("Thu");
        labels.add("Fri");
        labels.add("Sat");
        labels.add("Sun");

        return labels;
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
}
