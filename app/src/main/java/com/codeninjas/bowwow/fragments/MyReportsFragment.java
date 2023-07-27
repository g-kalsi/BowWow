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
import com.codeninjas.bowwow.adapters.ReportsAdapter;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.interfaces.SettingInterface;
import com.codeninjas.bowwow.models.ReportsModel;
import com.codeninjas.bowwow.utils.Config;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MyReportsFragment extends BaseFragment  implements SettingInterface {

    RecyclerView reportsRV;
    ReportsAdapter reportsAdapter;
    TextView noReportTV;
    private DatabaseReference databaseReference, publicDatabaseReference;
    private ArrayList<ReportsModel> reportsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("My Reports", true, false, true);
        return inflater.inflate(R.layout.fragment_my_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reportsRV = view.findViewById(R.id.reportsRV);
        noReportTV = view.findViewById(R.id.noReportTV);
        databaseReference = FirebaseDatabase.getInstance().getReference(Config.userReports).child(baseActivity.store.getString(Config.userID));
        reportsAdapter = new ReportsAdapter(reportsModelArrayList, baseActivity, this);
        reportsRV.setAdapter(reportsAdapter);
        view.findViewById(R.id.addFab).setOnClickListener(v -> gotofragmenttransstack(new AddReportFragment()));
        getReports();

    }

    private void getReports() {
        baseActivity.startProgressDialog();

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reportsModelArrayList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            ReportsModel reportsModel = postSnapshot.getValue(ReportsModel.class);
                            if (reportsModel!=null)
                                reportsModelArrayList.add(reportsModel);
                        }
                        baseActivity.stopProgressDialog();
                        if (reportsModelArrayList.size()==0){
                            noReportTV.setVisibility(View.VISIBLE);
                        }else{
                            noReportTV.setVisibility(View.GONE);
                            reportsAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        noReportTV.setVisibility(View.VISIBLE);
                        baseActivity.stopProgressDialog();
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(baseActivity);
        builder1.setMessage("Are you sure you want to delete this report?");
        builder1.setCancelable(false);
        builder1.setPositiveButton("Delete",
                (dialog, id) -> {
                    String key = reportsModelArrayList.get(position).getKey();
                    databaseReference.child(key).removeValue();
                    publicDatabaseReference = FirebaseDatabase.getInstance().getReference(Config.publicReports).child(key);
                    publicDatabaseReference.removeValue();
                    showSnackBar("Report deleted successfully!");
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