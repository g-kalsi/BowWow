package com.codeninjas.bowwow.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.models.FeedsModel;
import com.codeninjas.bowwow.ui.InfoDialogBox;
import com.codeninjas.bowwow.utils.Config;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddFeedFragment extends BaseFragment implements View.OnClickListener {

    RadioGroup radioGroup;
    RadioButton radioButton;
    TextInputEditText feedET;
    Switch privateSW;
    ImageView infoIV;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("Add Feed", true, false, true);
        return inflater.inflate(R.layout.fragment_add_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        radioGroup = view.findViewById(R.id.radioGroup);
        feedET = view.findViewById(R.id.feedET);
        infoIV = view.findViewById(R.id.infoIV);
        infoIV.setOnClickListener(this);
        privateSW = view.findViewById(R.id.privateSW);
        view.findViewById(R.id.uploadBT).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.uploadBT) {
            if (isValid()) {
                String uid = firebaseUser.getUid();

                String userName = baseActivity.store.getString(Config.dogName);
                String profilePicUrl = baseActivity.store.getString(Config.profilePic);
                String feeds = feedET.getText().toString().trim();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) radioGroup.findViewById(selectedId);
                String type = radioButton.getText().toString().trim(); //types will be Story or Tips

                boolean privacy = privateSW.isChecked();
                long created_at = System.currentTimeMillis();
                String key = databaseReference.child(Config.userFeeds).child(uid).push().getKey();

                FeedsModel feedsModel = new FeedsModel(uid, userName, profilePicUrl, feeds, type, privacy, created_at, key);

                databaseReference.child(Config.userFeeds).child(uid).child(key).setValue(feedsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(e -> baseActivity.showSnackBar("Something went wrong! Please try again later."));

                if (!privateSW.isChecked()) {
                    databaseReference.child(Config.publicFeeds).child(key).setValue(feedsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(e -> baseActivity.showSnackBar("Something went wrong! Please try again later."));
                }
                baseActivity.showSnackBar("Your " + type.toLowerCase() + " feed added successfully.");
                baseActivity.onBackPressed();
            }
        } else if (v.getId() == R.id.infoIV){
           baseActivity.showInfoDialog("You can upload feed in two different types Story and Tips.");
        }

    }

    private boolean isValid() {

        if (feedET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please write down your feed!");
        } else {
            return true;
        }

        return false;

    }

}