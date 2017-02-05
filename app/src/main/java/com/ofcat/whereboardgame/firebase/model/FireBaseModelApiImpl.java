package com.ofcat.whereboardgame.firebase.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.config.AppConfig;

/**
 * Created by orangefaller on 2017/2/4.
 */

public class FireBaseModelApiImpl implements FireBaseModelApi {

    private DatabaseReference myRef;

    public FireBaseModelApiImpl() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReferenceFromUrl(AppConfig.FIREBASE_URL);
//        myRef.setValue("Where Hello World");
//        myRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void addValueEventListener(ValueEventListener valueEventListener) {
        if (valueEventListener != null) {
            myRef.addValueEventListener(valueEventListener);
        }
    }

    @Override
    public void removeValueEventListener(ValueEventListener valueEventListener) {
        if (valueEventListener != null) {
            myRef.removeEventListener(valueEventListener);
        }
    }

    @Override
    public DatabaseReference getDatabaseRef() {
        return myRef;
    }
}
