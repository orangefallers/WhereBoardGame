package com.ofcat.whereboardgame.service;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ofcat.whereboardgame.util.MyLog;
import com.ofcat.whereboardgame.util.SharedPreferenceKey;

/**
 * Created by orangefaller on 2017/8/13.
 */

public class WhereBoardGameFCMInstanceIdService extends FirebaseInstanceIdService {

    private final static String TAG = "WhereBoardGameFCMInstanceIdService";

    private SharedPreferences sp;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        MyLog.i(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
        saveInstanceId(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void saveInstanceId(String token){
        sp = getSharedPreferences("BGS_DATA", MODE_PRIVATE);
        sp.edit().putString(SharedPreferenceKey.DATA_INSTANCE_ID, token).apply();
    }
}
