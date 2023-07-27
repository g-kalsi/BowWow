package com.codeninjas.bowwow.activities;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;
import com.codeninjas.bowwow.utils.Config;
import com.hbb20.CountryCodePicker;


public class LoginActivity extends BaseActivity {

    EditText phone;
    CountryCodePicker countryCodePicker;

// testing for signout, country code should be +1 by default
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone = findViewById(R.id.phoneNumber);
        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.setCountryForPhoneCode(1);
        // setting button click listener
        findViewById(R.id.nextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setMessage("+1" + phone.getText().toString() + " Is this OK, or would you like to edit the number? ");
                alert.setTitle("Alert !");

                if (phone.getText().toString().length() != 10) {
                    Toast.makeText(LoginActivity.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                } else {
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String phonenum = phone.getText().toString();
                            String ccpT = countryCodePicker.getSelectedCountryCodeWithPlus();
                            store.setBoolean(Config.profileUpdated, false);
                            startActivity(new Intent(LoginActivity.this, Otp_Page_activity.class).putExtra("phone number", phonenum).putExtra("ccp", ccpT));//.putExtra("name", name.getText().toString()));

                        }
                    }).setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alertDialog = alert.create();
                    // Show the Alert Dialog box
                    alertDialog.show();

                }

            }
        });
    }
}
