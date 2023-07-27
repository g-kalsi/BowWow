package com.codeninjas.bowwow.base;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.LoginActivity;
import com.codeninjas.bowwow.ui.InfoDialogBox;
import com.codeninjas.bowwow.utils.AppConstants;
import com.codeninjas.bowwow.utils.CommonUtils;
import com.codeninjas.bowwow.utils.KeyboardUtils;
import com.codeninjas.bowwow.utils.LocationUtil;
import com.codeninjas.bowwow.utils.NetworkUtils;
import com.codeninjas.bowwow.utils.PermissionUtil;
import com.codeninjas.bowwow.utils.PrefStore;
import com.codeninjas.bowwow.utils.SnackBarManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, BaseFragment.Callback {

    public PrefStore store;
    public PermissionCallback permCallback;
    private final NetworkErrorReceiver networkErrorReceiver = new NetworkErrorReceiver();

    private AlertDialog alert11;
    public Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        (BaseActivity.this).overridePendingTransition(
                R.anim.slide_in,
                R.anim.slide_out);
        store = new PrefStore(this);

        initializeProgressDialog();
        strictModeThread();
        transitionSlideInHorizontal();
        initializeNetworkBroadcast();
    }

    public String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date resultdate = new Date(milliSeconds);
        // Create a calendar object that will convert the date and time values in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(resultdate);
    }

    public String getTimeAgo(long milliSeconds, String dateFormat){
        String timeAgo="";
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());


            long nowTime = System.currentTimeMillis();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(nowTime - milliSeconds);
            long minutes=TimeUnit.MILLISECONDS.toMinutes(nowTime - milliSeconds);
            long hours=TimeUnit.MILLISECONDS.toHours(nowTime - milliSeconds);
            long days=TimeUnit.MILLISECONDS.toDays(nowTime - milliSeconds);

            if(seconds<60)
            {
                timeAgo = seconds+" s";
                System.out.println(seconds+" seconds ago");
            }
            else if(minutes<60)
            {
                timeAgo = minutes+" m";
                System.out.println(minutes+" minutes ago");
            }
            else if(hours<24)
            {
                timeAgo = hours+" h";
                System.out.println(hours+" hours ago");
            }
            else
            {
                timeAgo = days+" d";
                System.out.println(days+" days ago");
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }
        return timeAgo;
    }

    public String changeDateFormat(String dateString, String sourceDateFormat, String targetDateFormat) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        SimpleDateFormat inputDateFromat = new SimpleDateFormat(sourceDateFormat, Locale.getDefault());
        Date date = new Date();
        try {
            date = inputDateFromat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(targetDateFormat, Locale.getDefault());
        return outputDateFormat.format(date);
    }

    public void showSnackBar(String message) {
        SnackBarManager.showSnackBar(message, this);
    }

    public void showSnackBar(String message, String actionLabel, ActionClickListener actionClickListener) {
        SnackBarManager.showSnackBar(message, actionLabel, actionClickListener, this);
    }

    public void onError(String message) {
        if (message != null) {
            showSnackBar(message);
        }
    }

    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    public void showMessage(String message) {
        CommonUtils.showToast(this, message);
    }

    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    public void hideKeyboard() {
        KeyboardUtils.hideSoftInput(this);
    }

    public void hideKeyboard(Dialog dialog) {
        KeyboardUtils.hideSoftInput(dialog);
    }

    public void openActivityOnTokenExpire() {
        //   startActivity(LoginActivity.getStartIntent(this));
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocationUtil.onActivityResult(requestCode, resultCode);
        if (requestCode == AppConstants.REQUEST_CODE) {
            // ---------------------------- Write Setting  ---------------------
        }
    }

    public void checkSelfPermission(String[] perms, PermissionCallback permCallback) {
        this.permCallback = permCallback;
        ActivityCompat.requestPermissions(this, perms, AppConstants.REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this, permCallback);
    }

    public void exitFromApp() {
        exit();
    }

    public void showInfoDialog(String message) {
        InfoDialogBox infoDialogBox = new InfoDialogBox(this, message);
        infoDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialogBox.show();
    }

    private void exit() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(this.getString(R.string.are_you_sure_to_exit));
        builder1.setCancelable(false);
        builder1.setPositiveButton(this.getString(R.string.yes),
                (dialog, id) -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } catch (SecurityException e) {
                        e.printStackTrace();

                    }
                });

        builder1.setNegativeButton(
                this.getString(R.string.cancel),
                (dialog, id) -> dialog.cancel());

        alert11 = builder1.create();
        alert11.setOnShowListener(arg0 -> {
            alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
            alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Black));
        });
        alert11.show();
    }

    private void strictModeThread() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll().build());
    }

    public void transitionSlideInHorizontal() {
        this.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {

    }
    public void startProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface PermissionCallback {
        void permGranted();

        void permDenied();
    }

    private void initializeNetworkBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(networkErrorReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkErrorReceiver);
    }

    public class NetworkErrorReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = NetworkUtils.getConnectivityStatusString(context);
            showNetworkAlert(status);
        }
    }
    private void initializeProgressDialog() {
        progressDialog = new Dialog(this, R.style.transparent_dialog_borderless);
        View view = View.inflate(this, R.layout.progress_dialog, null);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // txtMsgTV = (TextView) view.findViewById(R.id.txtMsgTV);
        progressDialog.setCancelable(false);
    }
    public void showNetworkAlert(final String status) {
        showSnackBar(status, getString(R.string.retry), new ActionClickListener() {
            @Override
            public void onActionClicked(Snackbar snackbar) {
                snackbar.dismiss();
                if (!NetworkUtils.isNetworkConnected(BaseActivity.this)) {
                    BaseActivity.this.showNetworkAlert(status);
                }
            }
        });
    }

    public void logout(){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure? you want to logout.");
        builder1.setCancelable(false);
        builder1.setPositiveButton(this.getString(R.string.yes),
                (dialog, id) -> {
                    try {
                        store.cleanPref();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                });

        builder1.setNegativeButton(
                this.getString(R.string.cancel),
                (dialog, id) -> dialog.cancel());

        alert11 = builder1.create();
        alert11.setOnShowListener(arg0 -> {
            alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
            alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Black));
        });
        alert11.show();


    }


}
