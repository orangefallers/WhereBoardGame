package com.ofcat.whereboardgame;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.firebase.dataobj.SystemConfigDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;
import com.ofcat.whereboardgame.joinplay.PlayerRoomListActivity;
import com.ofcat.whereboardgame.login.UserLoginActivity;
import com.ofcat.whereboardgame.model.GetBoardGameStoreDataImpl;
import com.ofcat.whereboardgame.report.ReportActivity;
import com.ofcat.whereboardgame.report.issue.IssueReportActivity;
import com.ofcat.whereboardgame.util.FirebaseTableKey;
import com.ofcat.whereboardgame.util.MyLog;


/**
 * Created by orangefaller on 2017/1/15.
 */

public class WelcomeActivity extends AppCompatActivity {

    private final String TAG = WelcomeActivity.class.getSimpleName();
    private final static int permissionRequestCode = 24;

    private GetBoardGameStoreDataImpl boardGameStoreData;
    private FireBaseModelApiImpl fireBaseModelApi;

    private AlertDialog upDateDialog;

    private TextView tvAppStatus;
    private TextView tvUpdateDate;
    private TextView tvSystemNotify;
    private Button btnGo;
    private Button btnReport;
    private Button btnLogin;
    private Button btnJoinPlay;
    private Button btnIssueReport;

    private int debugCount = 0;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_welcome_go:

                    if (ActivityCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        btnGo.setEnabled(false);
                        ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
                    } else {
//                        btnGo.setEnabled(true);
                        btnGo.setEnabled(false);
                        tvAppStatus.setVisibility(View.VISIBLE);
                        tvAppStatus.setText(getString(R.string.waitting));
                        Intent intent = new Intent(WelcomeActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }

                    break;
                case R.id.btn_welcome_report:
                    Intent intent = new Intent(WelcomeActivity.this, ReportActivity.class);
                    startActivity(intent);

                    break;
                case R.id.btn_welcome_login:
                    startActivity(new Intent(WelcomeActivity.this, UserLoginActivity.class));

                    break;
                case R.id.btn_welcome_join_playroom:
                    startActivity(new Intent(WelcomeActivity.this, PlayerRoomListActivity.class));

                    break;
                case R.id.btn_welcome_issue_report:
                    startActivity(new Intent(WelcomeActivity.this, IssueReportActivity.class));

                    break;
                case R.id.tv_welcome_data_update_date:
                    debugCount++;

                    if (debugCount == 20) {
                        String versionName;
                        try {
                            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            versionName = "0.0.000";
                            e.printStackTrace();
                        }

                        Toast.makeText(WelcomeActivity.this, versionName, Toast.LENGTH_LONG).show();
                        debugCount = 0;
                    }

                    break;
            }

        }
    };

    private ValueEventListener systemConfigValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.i(TAG, "systemConfigValueEventListener");
//            GenericTypeIndicator<ArrayList<StoreDTO>> typeIndicator = new GenericTypeIndicator<ArrayList<StoreDTO>>() {
//            };
//            SystemConfigDTO systemConfigDTO;
//            if (dataSnapshot.child("SystemConfig").exists()) {
//                systemConfigDTO = dataSnapshot.child("SystemConfig").getValue(SystemConfigDTO.class);
//                settingSystemConfig(systemConfigDTO);
//            }
//            if (dataSnapshot.child("SystemNotification").exists()) {
//                String systemNotify = dataSnapshot.child("SystemNotification").getValue(String.class);
//                showSystemBulletinBoard(systemNotify);
//            }

            SystemConfigDTO systemConfigDTO = dataSnapshot.getValue(SystemConfigDTO.class);
            settingSystemConfig(systemConfigDTO);
//            Log.i(TAG, "data = " + dataSnapshot.toString());

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "systemConfigValueEventListener DatabaseError = " + databaseError.getMessage());
        }
    };

    private ValueEventListener systemNotifyValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
//            Log.i(TAG, "systemNotifyValueEventListener");
            String systemNotify = dataSnapshot.getValue(String.class);
            showSystemBulletinBoard(systemNotify);
//            Log.i(TAG, "data 02 = " + dataSnapshot.toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
//            Log.e(TAG, "systemNotifyValueEventListener DatabaseError = " + databaseError.getMessage());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();


        btnGo.setOnClickListener(clickListener);
        btnReport.setOnClickListener(clickListener);
        btnLogin.setOnClickListener(clickListener);
        btnJoinPlay.setOnClickListener(clickListener);
        btnIssueReport.setOnClickListener(clickListener);
        tvUpdateDate.setOnClickListener(clickListener);


        if (boardGameStoreData == null) {
            boardGameStoreData = new GetBoardGameStoreDataImpl(this, null);
        }

        if (fireBaseModelApi == null) {
            fireBaseModelApi = new FireBaseModelApiImpl().addApiNote(FirebaseTableKey.TABLE_SYSTEM_CONFIG);
            fireBaseModelApi.execute();
            fireBaseModelApi.addValueEventListener(systemConfigValueEventListener);

            fireBaseModelApi.cleanUrl();
            fireBaseModelApi.addApiNote(FirebaseTableKey.TABLE_SYSTEM_NOTIFICATION);
            fireBaseModelApi.execute();
            fireBaseModelApi.addValueEventListener(systemNotifyValueEventListener);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isConnectInternet()) {
            tvAppStatus.setVisibility(View.VISIBLE);
            tvAppStatus.setText(getString(R.string.no_internet));
            btnGo.setEnabled(false);
        } else {
            tvAppStatus.setVisibility(View.GONE);
            btnGo.setEnabled(true);
        }

        tvUpdateDate.setText(String.format(getResources().getString(R.string.data_update), boardGameStoreData.getDataUpdateDate()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fireBaseModelApi != null) {
            fireBaseModelApi.removeValueEventListener(systemConfigValueEventListener);
            fireBaseModelApi.removeValueEventListener(systemNotifyValueEventListener);
        }

        upDateDialog = null;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionRequestCode) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnGo.setEnabled(true);
            } else {
                Toast.makeText(WelcomeActivity.this, getString(R.string.please_argue_location_permission), Toast.LENGTH_LONG).show();
                btnGo.setEnabled(false);
            }

        }
    }

    private void initView() {
        tvAppStatus = (TextView) findViewById(R.id.tv_app_status);
        tvSystemNotify = (TextView) findViewById(R.id.tv_app_system_notify);
        tvUpdateDate = (TextView) findViewById(R.id.tv_welcome_data_update_date);

        btnGo = (Button) findViewById(R.id.btn_welcome_go);
        btnReport = (Button) findViewById(R.id.btn_welcome_report);
        btnLogin = (Button) findViewById(R.id.btn_welcome_login);
        btnJoinPlay = (Button) findViewById(R.id.btn_welcome_join_playroom);
        btnIssueReport = (Button) findViewById(R.id.btn_welcome_issue_report);

    }

    private boolean isConnectInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;

    }

    private void showSystemBulletinBoard(String bulletin) {
        if (bulletin == null || bulletin.equals("")) {
            tvSystemNotify.setVisibility(View.GONE);
        } else {
            tvSystemNotify.setVisibility(View.VISIBLE);
            tvSystemNotify.setText(bulletin);
        }
    }

    private void settingSystemConfig(SystemConfigDTO configDTO) {
        if (configDTO != null) {
            btnReport.setEnabled(configDTO.isOpenReportFeature());
            btnGo.setEnabled(configDTO.isOpenWatchMapFeature());


            showGoToUpdateAppDialog(
                    configDTO.getOpenAppUpdateFeature().isOpenUpdate(),
                    configDTO.getOpenAppUpdateFeature().isForcedUpdate(),
                    configDTO.getOpenAppUpdateFeature().getUpdateTitle(),
                    configDTO.getOpenAppUpdateFeature().getUpdateMessage());

        }

    }


    private void showGoToUpdateAppDialog(boolean isShow, boolean isForced, String title, String message) {

        MyLog.i(TAG,"is show =" +isShow +" is forced = "+ isForced);
        if (!isShow || isFinishing()) {
            return;
        }


        String dialogTitle;
        String dialogMessage;

        if (title != null && !title.equals("")) {
            dialogTitle = title;
        } else {
            dialogTitle = getString(R.string.update_dialog_title);
        }

        if (message != null && !message.equals("")) {
            dialogMessage = message;
        } else {
            dialogMessage = getString(R.string.update_dialog_message);
        }

        if (upDateDialog == null) {
            upDateDialog = new AlertDialog.Builder(this)
                    .setTitle(dialogTitle)
                    .setMessage(dialogMessage)
                    .setCancelable(!isForced)
                    .setPositiveButton(R.string.goto_update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();


                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            MyLog.i(TAG, "package name = " + appPackageName);
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            }

                            finish();
                        }
                    }).create();
            upDateDialog.show();
        } else if (!upDateDialog.isShowing()) {
            upDateDialog.show();
        }
    }

}
