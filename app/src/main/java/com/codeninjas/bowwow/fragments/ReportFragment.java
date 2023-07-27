package com.codeninjas.bowwow.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.adapters.ReportsAdapter;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.models.FeedsModel;
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

public class ReportFragment extends BaseFragment {

    RecyclerView reportsRV;
    ReportsAdapter reportsAdapter;
    TextView noReportTV;
    private DatabaseReference databaseReference;
    private ArrayList<ReportsModel> reportsModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("Reports", true, true, false);
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reportsRV = view.findViewById(R.id.reportsRV);
        noReportTV = view.findViewById(R.id.noReportTV);
        databaseReference = FirebaseDatabase.getInstance().getReference(Config.publicReports);
        reportsAdapter = new ReportsAdapter(reportsModelArrayList, baseActivity);
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

}