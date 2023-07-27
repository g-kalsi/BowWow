package com.codeninjas.bowwow.fragments;

import android.app.AlertDialog;
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
import com.codeninjas.bowwow.adapters.RemindersAdapter;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.interfaces.SettingInterface;
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


public class RemindersFragment extends BaseFragment implements SettingInterface {

    RecyclerView remindersRV;
    RemindersAdapter remindersAdapter;
    private DatabaseReference databaseReference;
    TextView noFoundTV;
    ArrayList<RemindersModel> remindersModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("My Reminders", true, false, true);
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        remindersRV = view.findViewById(R.id.remindersRV);
        noFoundTV = view.findViewById(R.id.noFoundTV);
        remindersAdapter = new RemindersAdapter(remindersModelArrayList, baseActivity, this);
        remindersRV.setAdapter(remindersAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference(Config.reminders).child(baseActivity.store.getString(Config.userID));
        getReminders();
        view.findViewById(R.id.addFab).setOnClickListener(v -> gotofragmenttransstack(new AddReminderFragment()));

    }

    private void getReminders() {
        baseActivity.startProgressDialog();

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                databaseReference.addValueEventListener(new ValueEventListener() {
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
                            noFoundTV.setVisibility(View.VISIBLE);
                        }else{
                            remindersAdapter.notifyDataSetChanged();
                            noFoundTV.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        noFoundTV.setVisibility(View.VISIBLE);
                        baseActivity.stopProgressDialog();
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(baseActivity);
        builder1.setMessage("Are you sure you want to delete this reminder?");
        builder1.setCancelable(false);
        builder1.setPositiveButton("Delete",
                (dialog, id) -> {
                    String key = remindersModelArrayList.get(position).getId();
                    databaseReference.child(key).removeValue();
                    showSnackBar("Reminder deleted successfully!");
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