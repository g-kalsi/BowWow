package com.codeninjas.bowwow.utils;

import android.os.Bundle;
import android.widget.TextView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;

public class NotificationMessage extends BaseActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);
        textView = findViewById(R.id.tv_message);
        Bundle bundle = getIntent().getExtras();                                                    //call the data which is passed by another intent
        textView.setText(bundle.getString("message"));
    }
}
