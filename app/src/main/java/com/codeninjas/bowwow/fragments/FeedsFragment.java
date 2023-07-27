package com.codeninjas.bowwow.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.adapters.FeedsAdapter;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.models.FeedsModel;
import com.codeninjas.bowwow.models.RemindersModel;
import com.codeninjas.bowwow.utils.Config;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FeedsFragment extends BaseFragment {

    FeedsAdapter feedsAdapter;
    RecyclerView feedsRV;
    LinearLayout reminderLL;
    TextView noFeedTV, textView, setReminderTV;
    CardView reminderCV;
    private DatabaseReference databaseReference, remindersDBReference;
    ArrayList<RemindersModel> remindersModelArrayList = new ArrayList<>();
    private ArrayList<FeedsModel> feedsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("BowWow", true, true, false);
        return inflater.inflate(R.layout.fragment_feeds, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feedsRV = view.findViewById(R.id.feedsRV);
        textView = view.findViewById(R.id.textView);
        setReminderTV = view.findViewById(R.id.setReminderTV);
        reminderLL = view.findViewById(R.id.ll); reminderLL.setOnClickListener(this);
        reminderCV = view.findViewById(R.id.reminderCV);
        reminderCV.setOnClickListener(v->new RemindersFragment() );
        noFeedTV = view.findViewById(R.id.noFeedTV);
        feedsAdapter = new FeedsAdapter(feedsModelArrayList, baseActivity);
        feedsRV.setAdapter(feedsAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference(Config.publicFeeds);
        remindersDBReference = FirebaseDatabase.getInstance().getReference(Config.reminders).child(baseActivity.store.getString(Config.userID));
        view.findViewById(R.id.addFab).setOnClickListener(v -> gotofragmenttransstack(new AddFeedFragment()));
        reminderLL.setOnClickListener(v -> gotofragmenttransstack(new RemindersFragment()));
        getFeeds();
        getReminders();
    }
// notification not working
    private void getFeeds() {
        baseActivity.startProgressDialog();

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        feedsModelArrayList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            FeedsModel addWritingModel = postSnapshot.getValue(FeedsModel.class);
                            if (addWritingModel!=null && !addWritingModel.isPrivacy())
                                feedsModelArrayList.add(addWritingModel);
                        }
                        baseActivity.stopProgressDialog();
                        if (feedsModelArrayList.size()==0){
                            noFeedTV.setVisibility(View.VISIBLE);
                        }else{
                            feedsAdapter.notifyDataSetChanged();
                            noFeedTV.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        noFeedTV.setVisibility(View.VISIBLE);
                        baseActivity.stopProgressDialog();
                    }
                });
            }
        });
    }

    private void getReminders() {

        baseActivity.startProgressDialog();

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                remindersDBReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        remindersModelArrayList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            RemindersModel addWritingModel = postSnapshot.getValue(RemindersModel.class);
                            if (addWritingModel!=null)
                                remindersModelArrayList.add(addWritingModel);
                        }
                        baseActivity.stopProgressDialog();
                        if (remindersModelArrayList.size()==0){
                            textView.setText("You don't have any reminder for today.");
                            setReminderTV.setText("Set Reminder");
                        }else{
                            textView.setText(remindersModelArrayList.get((remindersModelArrayList.size() - 1)).getMessage());
                            setReminderTV.setText("View Reminder");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        baseActivity.stopProgressDialog();
                    }
                });
            }
        });

    }


}