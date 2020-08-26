package com.zergitas.linhlee.facebookusagelimited.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.zergitas.linhlee.facebookusagelimited.R;

/**
 * Created by Linh Lee on 11/22/2016.
 */
public class SelectUnitsDialog extends Dialog implements View.OnClickListener {
    private Activity context;
    private ItemClicked listener;

    private RelativeLayout hourLayout;
    private RelativeLayout minuteLayout;
    private RelativeLayout secondLayout;

    public SelectUnitsDialog(Activity context, ItemClicked listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_units);

        hourLayout = (RelativeLayout) findViewById(R.id.hour_layout);
        minuteLayout = (RelativeLayout) findViewById(R.id.minute_layout);
        secondLayout = (RelativeLayout) findViewById(R.id.second_layout);

        hourLayout.setOnClickListener(this);
        minuteLayout.setOnClickListener(this);
        secondLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hour_layout:
                listener.hourClicked();
                dismiss();
                break;
            case R.id.minute_layout:
                listener.minuteClicked();
                dismiss();
                break;
            case R.id.second_layout:
                listener.secondClicked();
                dismiss();
                break;
        }
    }

    public interface ItemClicked {
        void hourClicked();
        void minuteClicked();
        void secondClicked();
    }
}
