package com.ofcat.whereboardgame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.firebase.dataobj.SystemConfigDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApi;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;
import com.ofcat.whereboardgame.model.GetBoardGameStoreDataImpl;
import com.ofcat.whereboardgame.report.ReportActivity;

/**
 * Created by orangefaller on 2017/1/15.
 */

public class WelcomeActivity extends AppCompatActivity {

    private final String TAG = WelcomeActivity.class.getSimpleName();
    private final static int permissionRequestCode = 24;

    private GetBoardGameStoreDataImpl boardGameStoreData;
    private FireBaseModelApi fireBaseModleApi;

    private TextView tvAppStatus;
    private TextView tvUpdateDate;
    private TextView tvSystemNotify;
    private Button btnGo;
    private Button btnReport;

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

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
//            GenericTypeIndicator<ArrayList<StoreDTO>> typeIndicator = new GenericTypeIndicator<ArrayList<StoreDTO>>() {
//            };
            SystemConfigDTO systemConfigDTO = null;
            if (dataSnapshot.child("SystemConfig").exists()) {
                systemConfigDTO = dataSnapshot.child("SystemConfig").getValue(SystemConfigDTO.class);
                settingSystemConfig(systemConfigDTO);
            }
            if (dataSnapshot.child("SystemNotification").exists()) {
                String systemNotify = dataSnapshot.child("SystemNotification").getValue(String.class);
                showSystemBulletinBoard(systemNotify);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();

        btnGo.setOnClickListener(clickListener);
        btnReport.setOnClickListener(clickListener);
        tvUpdateDate.setOnClickListener(clickListener);

        if (boardGameStoreData == null) {
            boardGameStoreData = new GetBoardGameStoreDataImpl(this, null);
        }

        if (fireBaseModleApi == null) {
            fireBaseModleApi = new FireBaseModelApiImpl();
            fireBaseModleApi.addValueEventListener(valueEventListener);
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
        if (fireBaseModleApi != null) {
            fireBaseModleApi.removeValueEventListener(valueEventListener);
        }

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
        }

    }

}
