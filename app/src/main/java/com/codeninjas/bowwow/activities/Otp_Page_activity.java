package com.codeninjas.bowwow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;
import com.codeninjas.bowwow.utils.Config;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Otp_Page_activity extends BaseActivity implements View.OnClickListener {

    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six;
    //    Button verifyOTPBtn;
    TextView phone, resendSms;
    String pno,ccp,nme;

    private FirebaseAuth mAuth;
    private String verificationId;
    private DatabaseReference databaseReference;
    boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_page);
        phone=findViewById(R.id.phoneNum);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        resendSms= findViewById(R.id.resendSms2);
        otp_textbox_one = findViewById(R.id.otp1);
        otp_textbox_two = findViewById(R.id.otp2);
        otp_textbox_three = findViewById(R.id.otp3);
        otp_textbox_four = findViewById(R.id.otp4);
        otp_textbox_five = findViewById(R.id.otp5);
        otp_textbox_six = findViewById(R.id.otp6);

        mAuth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        pno=extras.getString("phone number");
        ccp=extras.getString("ccp");
        nme=extras.getString("name");

        phone.setText(ccp+" "+pno);

        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six};

        otp_textbox_one.addTextChangedListener(new otp_helper(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new otp_helper(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new otp_helper(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new otp_helper(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new otp_helper(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new otp_helper(otp_textbox_six, edit));

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.resendSms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(ccp+""+pno);
                b=true;
                if (b){
                    resendSms.setVisibility(View.VISIBLE);
                    findViewById(R.id.resendSms).setVisibility(View.GONE);
                    for (int i=59; i<=0; i--){
                        resendSms.setText("  Resend SMS "+i);
                    }

                }else {
                    resendSms.setVisibility(View.GONE);
                    findViewById(R.id.resendSms).setVisibility(View.VISIBLE);
                }
            }
        });
        sendVerificationCode(ccp+""+pno);


        findViewById(R.id.verify_otp).setOnClickListener(this);

    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s;
            Toast.makeText(Otp_Page_activity.this, "Code sent successfully", Toast.LENGTH_SHORT).show();
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {

//                edtOTP.setText(code);
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {

            Log.i("TAG", "onVerificationFailed: "+e.getMessage());
            Toast.makeText(Otp_Page_activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private void verifyCode(String code) {
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.

                            String uid = FirebaseAuth.getInstance().getUid();
                            store.saveString(Config.userID, uid);
                            startActivity(new Intent(Otp_Page_activity.this, MainActivity.class));
                            finish();
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Log.e("OTP_FAILED", task.getException() + "");
                            showSnackBar("something went wrong!" + task.getException());
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.verify_otp){
            String otpSTr=getStr(otp_textbox_one)+getStr(otp_textbox_two)+getStr(otp_textbox_three)+getStr(otp_textbox_four)+getStr(otp_textbox_five)+getStr(otp_textbox_six);
            Log.i("TAG", "onClick: "+otpSTr+otpSTr.length());
            Log.i("TAG", "onClick: "+verificationId);
            if(otpSTr.length()==6){
                verifyCode(otpSTr);
            }else {

                Toast.makeText(this, "OTP is incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class otp_helper implements TextWatcher {
        private final EditText[] editText;
        private View view;

        public otp_helper(View view, EditText editText[]) {
            this.editText = editText;
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            if (R.id.otp1 == view.getId()) {
                Log.i("otp", "otp1");
                if (text.length() == 1)
                    editText[1].requestFocus();
            } else if (R.id.otp2 == view.getId()) {
                Log.i("otp", "otp2");
                if (text.length() == 1)
                    editText[2].requestFocus();
                else if (text.length() == 0)
                    editText[0].requestFocus();
            } else if (R.id.otp3 == view.getId()) {
                Log.i("otp", "otp3");
                if (text.length() == 1)

                    editText[3].requestFocus();
                else if (text.length() == 0)
                    editText[1].requestFocus();
            } else if (R.id.otp4 == view.getId()) {
                Log.i("otp", "otp4");
                if (text.length() == 1)
                    editText[4].requestFocus();
                else if (text.length() == 0)
                    editText[2].requestFocus();
            } else if (R.id.otp5 == view.getId()) {
                Log.i("otp", "otp5");
                if (text.length() == 1)
                    editText[5].requestFocus();
                else if (text.length() == 0)
                    editText[3].requestFocus();
            } else if (R.id.otp6 == view.getId()) {
                Log.i("otp", "otp6");
                if (text.length() == 0)
                    editText[4].requestFocus();
            } else {
                Log.e("MSG", "Error");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }}

    private String getStr(EditText editText){
        return editText.getText().toString();
    }
}