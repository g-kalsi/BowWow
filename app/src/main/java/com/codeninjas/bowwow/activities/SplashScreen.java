package com.codeninjas.bowwow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;
import com.codeninjas.bowwow.utils.Config;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        // using timer to take pause of 3 sec
        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                // checking if user is loged in or not respectively taking user to login screen or main screen
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser==null)
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                else{
                    String uid = firebaseUser.getUid();
                    store.saveString(Config.userID, uid);
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }
        };
        countDownTimer.start();

    }


}
