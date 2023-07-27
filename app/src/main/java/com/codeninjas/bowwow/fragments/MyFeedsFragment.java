package com.codeninjas.bowwow.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.adapters.FeedsAdapter;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.interfaces.SettingInterface;
import com.codeninjas.bowwow.models.FeedsModel;
import com.codeninjas.bowwow.utils.Config;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MyFeedsFragment extends BaseFragment implements SettingInterface {

    FeedsAdapter feedsAdapter;
    RecyclerView feedsRV;
    TextView noFeedTV;
    private DatabaseReference databaseReference, publicDatabaseReference;
    private ArrayList<FeedsModel> feedsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("My Feeds", true, false, true);
        return inflater.inflate(R.layout.fragment_my_feeds, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feedsRV = view.findViewById(R.id.feedsRV);
        noFeedTV = view.findViewById(R.id.noFeedTV);
        feedsAdapter = new FeedsAdapter(feedsModelArrayList, baseActivity, this);
        feedsRV.setAdapter(feedsAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference(Config.userFeeds).child(baseActivity.store.getString(Config.userID));
        view.findViewById(R.id.addFab).setOnClickListener(v -> gotofragmenttransstack(new AddFeedFragment()));
        getFeeds();
    }

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
                            if (addWritingModel!=null)
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

    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(baseActivity);
        builder1.setMessage("Are you sure you want to delete this feed?");
        builder1.setCancelable(false);
        builder1.setPositiveButton("Delete",
                (dialog, id) -> {
                    String key = feedsModelArrayList.get(position).getKey();
                    databaseReference.child(key).removeValue();
                    publicDatabaseReference = FirebaseDatabase.getInstance().getReference(Config.publicFeeds).child(key);
                    publicDatabaseReference.removeValue();
                    showSnackBar("Feed deleted successfully!");
                });

        builder1.setNegativeButton(
                this.getString(R.string.cancel),
                (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = builder1.create();
        alertDialog.setOnShowListener(arg0 -> {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Black));
        });
        alertDialog.show();

    }
}