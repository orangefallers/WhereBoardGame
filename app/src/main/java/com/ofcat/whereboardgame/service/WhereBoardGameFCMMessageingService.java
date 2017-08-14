package com.ofcat.whereboardgame.service;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.util.MyLog;

/**
 * Created by orangefaller on 2017/8/13.
 */

public class WhereBoardGameFCMMessageingService extends FirebaseMessagingService {

    private final String TAG = WhereBoardGameFCMMessageingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        MyLog.i(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            MyLog.i(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            MyLog.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }


    private void sendNotification(RemoteMessage.Notification notification) {
        // 取得NotificationManager物件
        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // 建立NotificationCompat.Builder物件
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.wbg_logo)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setAutoCancel(true);

        int ntfId = (int) System.currentTimeMillis();
        // 使用BASIC_ID為編號發出通知
        manager.notify(ntfId, builder.build());
        // 更新BASIC_ID編號的通知
        // manager.notify(BASIC_ID, notificationNew);
        // 清除BASIC_ID編號的通知
        // manager.cancel(BASIC_ID);
    }
}
