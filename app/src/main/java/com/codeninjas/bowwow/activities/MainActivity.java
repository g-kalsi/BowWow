package com.codeninjas.bowwow.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.fragments.AddFeedFragment;
import com.codeninjas.bowwow.fragments.FeedsFragment;
import com.codeninjas.bowwow.base.BaseActivity;
import com.codeninjas.bowwow.fragments.ProfileFrament;
import com.codeninjas.bowwow.fragments.ReportFragment;
import com.codeninjas.bowwow.fragments.SettingsFragment;
import com.codeninjas.bowwow.models.DogModel;
import com.codeninjas.bowwow.utils.Config;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigation;
    private int pos = -1;
    private Toolbar toolbar;
    private String uid;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ImageView closeBtn;
    private TextView title_text;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        uid = firebaseUser.getUid();

        navigation = findViewById(R.id.navigation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view = findViewById(R.id.view);
        closeBtn = toolbar.findViewById(R.id.backBtn);
        closeBtn.setOnClickListener(this);
        title_text = toolbar.findViewById(R.id.title_text);

        navigation.setOnNavigationItemSelectedListener(this);
        closeBtn.setOnClickListener(this);

        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setSelectedItemId(R.id.navigation_home);
        checkProfileUpdated();

    }

    private void gotoFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out).replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public void showToolbarBottomNavigation(String text, boolean tool_bar, boolean bottombar, boolean backBtn) {
        title_text.setText(text);

        if (backBtn){
            closeBtn.setVisibility(View.VISIBLE);
        }else{
            closeBtn.setVisibility(View.GONE);
        }

        if (tool_bar) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
        if (bottombar) {
            navigation.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
            navigation.setVisibility(View.GONE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        if (pos != item.getItemId()) {
            pos = item.getItemId();

            if (item.getItemId() == R.id.navigation_home) {
                fragment = new FeedsFragment();
            } else if (item.getItemId() == R.id.navigation_report) {
                fragment = new ReportFragment();
            } else {
                fragment = new SettingsFragment();
            }

            gotoFragment(fragment);
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v==closeBtn){

            onBackPressed();

        }

    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);
        if (fragment instanceof FeedsFragment || fragment instanceof ReportFragment) {
          exitFromApp();
        }
        else if (fragment instanceof ProfileFrament) {
            gotoFragment(new SettingsFragment());
        }
        else if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();

        } else {
            gotoFragment(new FeedsFragment());

        }
    }

    private void checkProfileUpdated() {
        // getting user profile
        databaseReference = FirebaseDatabase.getInstance().getReference(Config.dogTable).child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DogModel dogModel = dataSnapshot.getValue(DogModel.class);
                if (dogModel==null){
                    store.setBoolean(Config.profileUpdated, false);
                    gotoFragment(new ProfileFrament());
                } else {
                    store.setBoolean(Config.profileUpdated, true);
                    store.saveString(Config.dogName, dogModel.getName());
                    store.saveString(Config.profilePic, dogModel.getProfilePic());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
