package com.codeninjas.bowwow.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseHelper {
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    public static void getUserData(String phone, ValueEventListener valueEventListener) {
        databaseReference.child(Config.dogTable).orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(valueEventListener);

    }

}
