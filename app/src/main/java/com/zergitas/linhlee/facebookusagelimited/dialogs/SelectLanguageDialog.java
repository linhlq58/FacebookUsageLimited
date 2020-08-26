package com.zergitas.linhlee.facebookusagelimited.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.zergitas.linhlee.facebookusagelimited.R;

/**
 * Created by Linh Lee on 11/25/2016.
 */
public class SelectLanguageDialog extends Dialog implements View.OnClickListener{
    private Activity context;
    private ItemClicked listener;

    private RelativeLayout engLayout;
    private RelativeLayout viLayout;

    public SelectLanguageDialog(Activity context, ItemClicked listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_language);

        engLayout = (RelativeLayout) findViewById(R.id.eng_layout);
        viLayout = (RelativeLayout) findViewById(R.id.vi_layout);

        engLayout.setOnClickListener(this);
        viLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.eng_layout:
                listener.engClicked();
                dismiss();
                break;
            case R.id.vi_layout:
                listener.viClicked();
                dismiss();
                break;
        }
    }

    public interface ItemClicked {
        void engClicked();
        void viClicked();
    }
}
