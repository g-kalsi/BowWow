package com.codeninjas.bowwow.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.models.NotesModel;
import com.codeninjas.bowwow.models.RemindersModel;
import com.codeninjas.bowwow.utils.Config;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AddNotesFragment extends BaseFragment {

    EditText titleET, notesET;
    Button saveBT, deleteBT;

    NotesModel notesModel;

    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (notesModel == null)
            ((MainActivity) baseActivity).showToolbarBottomNavigation("Add Note", true, false, true);
        else
            ((MainActivity) baseActivity).showToolbarBottomNavigation("Edit Note", true, false, true);
        return inflater.inflate(R.layout.fragment_add_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleET = view.findViewById(R.id.titleET);
        notesET = view.findViewById(R.id.notesET);
        saveBT = view.findViewById(R.id.saveBT);
        deleteBT = view.findViewById(R.id.deleteBT);
        saveBT.setOnClickListener(this);
        deleteBT.setOnClickListener(this);
        if (notesModel!=null) {
            titleET.setText(notesModel.getTitle());
            notesET.setText(notesModel.getNote());
            deleteBT.setVisibility(View.VISIBLE);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v == saveBT) {
            if (isValid()) {
                if (notesModel == null) {
                    saveNote();
                } else {
                    updateNote();
                }
            }
        } else if (v==deleteBT) {
            deleteNote();
        }

    }

    private void updateNote() {
        String uid = baseActivity.store.getString(Config.userID);
        notesModel.setNote(notesET.getText().toString().trim());
        notesModel.setTitle(titleET.getText().toString().trim());
        notesModel.setCreated_at(System.currentTimeMillis());
        Map<String, Object> postValues = notesModel.toMap();
        databaseReference.child(Config.notes).child(uid).child(notesModel.getKey()).updateChildren(postValues).addOnSuccessListener(unused -> {
            showSnackBar("Note updated successfully!");
            baseActivity.onBackPressed();
        }).addOnFailureListener(e -> baseActivity.showSnackBar("Something went wrong! Please try again later."));

    }

    private void saveNote() {
        String uid = baseActivity.store.getString(Config.userID);
        String title = titleET.getText().toString().trim();
        String key = databaseReference.child(Config.reminders).child(uid).push().getKey();
        String notes = notesET.getText().toString().trim();
        long created_at = System.currentTimeMillis();
        NotesModel notesModel = new NotesModel(title, notes, key, created_at);
        databaseReference.child(Config.notes).child(uid).child(key).setValue(notesModel).addOnSuccessListener(aVoid -> {
            showSnackBar("Note saved successfully!");
            baseActivity.onBackPressed();
        }).addOnFailureListener(e -> baseActivity.showSnackBar("Something went wrong! Please try again later."));

    }

    private void deleteNote() {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(baseActivity);
        builder1.setMessage("Are you sure you want to delete this note?");
        builder1.setCancelable(false);
        builder1.setPositiveButton("Delete",
                (dialog, id) -> {
                    String key = notesModel.getKey();
                    String uid = baseActivity.store.getString(Config.userID);
                    databaseReference.child(Config.notes).child(uid).child(key).removeValue();
                    showSnackBar("Note deleted successfully!");
                    baseActivity.onBackPressed();
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

    private boolean isValid() {
        if (titleET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please enter title!");
        } else if (notesET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please enter notes to save!");
        } else {
            return true;
        }

        return false;
    }

}