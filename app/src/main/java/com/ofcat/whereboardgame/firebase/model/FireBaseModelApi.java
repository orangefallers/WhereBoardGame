package com.ofcat.whereboardgame.firebase.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by orangefaller on 2017/2/4.
 */

public interface FireBaseModelApi {

    void addValueEventListener(ValueEventListener valueEventListener);

    void removeValueEventListener(ValueEventListener valueEventListener);

    DatabaseReference getDatabaseRef();

    DatabaseReference getDefaultDatabaseRef();
}
