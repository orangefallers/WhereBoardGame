package com.ofcat.whereboardgame.firebase.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.config.AppConfig;

/**
 * 不要用singleton,會有問題 2017/06/22 by orangefaller
 *
 * Created by orangefaller on 2017/2/4.
 */

public class FireBaseModelApiImpl implements FireBaseModelApi {

    private final String TAG = FireBaseModelApiImpl.class.getSimpleName();

    private DatabaseReference myRef;
    private FirebaseDatabase database;

    private String apiNote = "";

    private static FireBaseModelApiImpl fireBaseModelApi;

    public FireBaseModelApiImpl() {
        database = FirebaseDatabase.getInstance();
//        myRef = database.getReferenceFromUrl(AppConfig.FIREBASE_URL);
//        myRef.setValue("Where Hello World");
//        myRef.addValueEventListener(valueEventListener);
    }

    public FireBaseModelApiImpl addApiNote(String note) {
        apiNote = apiNote + "/" + note;
        return this;
    }

    public void execute() {
        String apiUrl = AppConfig.FIREBASE_URL + apiNote;
//        Log.i(TAG, "apiUrl = " + apiUrl);
        myRef = database.getReferenceFromUrl(apiUrl);
    }

    public void cleanUrl() {
        apiNote = "";
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

    @Override
    public DatabaseReference getDefaultDatabaseRef() {
        return database.getReferenceFromUrl(AppConfig.FIREBASE_URL);
    }
}
