package com.codeninjas.bowwow.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codeninjas.bowwow.R;
import com.nispok.snackbar.listeners.ActionClickListener;


public class BaseFragment extends Fragment implements AdapterView.OnItemClickListener,
        View.OnClickListener, AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    public BaseActivity baseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        baseActivity.hideKeyboard();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {

    }

    public void showToast(String msg) {
        baseActivity.showMessage(msg);
    }

    public void showSnackBar(String message, String action, ActionClickListener actionClickListener) {
        baseActivity.showSnackBar(message, action, actionClickListener);
    }

    public void showSnackBar(String msg) {
        baseActivity.showSnackBar(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


    public void openActivityOnTokenExpire() {
        baseActivity.openActivityOnTokenExpire();
    }

    public void onError(int resId) {
        baseActivity.onError(resId);
    }

    public void onError(String message) {
        baseActivity.onError(message);
    }

    public void showMessage(String message) {
        baseActivity.showMessage(message);
    }

    public void showMessage(int resId) {
        baseActivity.showMessage(resId);
    }

    public boolean isNetworkConnected() {
        return baseActivity.isNetworkConnected();
    }

    public void hideKeyboard() {
        baseActivity.hideKeyboard();
    }

    public void hideKeyboard(Dialog dialog) {
        baseActivity.hideKeyboard(dialog);
    }


    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }

  /*  public void signuploginfragmenttransstack(Fragment fragment) {
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out).replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }*/

    public void gotofragmenttransstack(Fragment fragment) {
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out).replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    protected void gotofragmenttransnostack(Fragment fragment) {
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out).replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
}
