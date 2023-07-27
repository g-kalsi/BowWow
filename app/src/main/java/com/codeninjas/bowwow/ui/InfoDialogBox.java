package com.codeninjas.bowwow.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;

public class InfoDialogBox extends Dialog implements View.OnClickListener {

    public Context context;
    public Dialog dialog;
    public Button ok;
    public TextView messageTV;
    String message;

    public InfoDialogBox(@NonNull Context context, String message) {
        super(context);
        this.context = context;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_dialog);
        ok = findViewById(R.id.okBT);
        messageTV = findViewById(R.id.msgTV);
        messageTV.setText(message);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
