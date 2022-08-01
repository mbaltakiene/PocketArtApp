package com.example.android.pocketartapp.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    private static FirebaseDatabase mDatabase;

    public static DatabaseReference getDatabase() {
        if (mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance();
        }
        return mDatabase.getReference().child("paintings");
    }
}
