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
import com.codeninjas.bowwow.adapters.NotesAdapter;
import com.codeninjas.bowwow.adapters.RemindersAdapter;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.interfaces.SettingInterface;
import com.codeninjas.bowwow.models.NotesModel;
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

public class NotesFragment extends BaseFragment implements SettingInterface {
    RecyclerView notesRV;
    NotesAdapter notesAdapter;
    private DatabaseReference databaseReference;
    TextView noFoundTV;
    ArrayList<NotesModel> notesModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("My Notes", true, false, true);
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notesRV = view.findViewById(R.id.notesRV);
        noFoundTV = view.findViewById(R.id.noNotesTV);
        notesAdapter = new NotesAdapter(notesModelArrayList, baseActivity, this);
        notesRV.setAdapter(notesAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference(Config.notes).child(baseActivity.store.getString(Config.userID));
        getNotes();
        view.findViewById(R.id.addFab).setOnClickListener(v -> gotofragmenttransstack(new AddNotesFragment()));

    }

    private void getNotes() {

        baseActivity.startProgressDialog();

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notesModelArrayList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            NotesModel notesModel = postSnapshot.getValue(NotesModel.class);
                            if (notesModel!=null)
                                notesModelArrayList.add(notesModel);
                        }
                        baseActivity.stopProgressDialog();
                        if (notesModelArrayList.size()==0){
                            noFoundTV.setVisibility(View.VISIBLE);
                        }else{
                            notesAdapter.notifyDataSetChanged();
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

        AddNotesFragment addNotesFragment = new AddNotesFragment();
        addNotesFragment.notesModel = notesModelArrayList.get(position);
        gotofragmenttransstack(addNotesFragment);

    }
}