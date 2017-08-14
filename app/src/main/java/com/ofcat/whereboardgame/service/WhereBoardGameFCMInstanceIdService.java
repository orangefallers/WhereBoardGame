package com.ofcat.whereboardgame.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ofcat.whereboardgame.util.MyLog;

/**
 * Created by orangefaller on 2017/8/13.
 */

public class WhereBoardGameFCMInstanceIdService extends FirebaseInstanceIdService {

    private final static String TAG = "WhereBoardGameFCMInstanceIdService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        MyLog.i(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
