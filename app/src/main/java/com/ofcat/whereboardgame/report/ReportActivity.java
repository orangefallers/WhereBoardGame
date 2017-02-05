package com.ofcat.whereboardgame.report;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.StoreDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApi;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by orangefaller on 2017/2/5.
 */

public class ReportActivity extends AppCompatActivity {

    private final String TAG = ReportActivity.class.getSimpleName();

    private final static String TABLE_USER_REPORT = "UserReport";

    private FireBaseModelApi fireBaseModelApi;

    private Spinner spinnerStoreStatus;
    private EditText etStoreName;
    private EditText etStoreAddress;
    private Button btnConfirm;

    private int storeStatusIndex = 0;
    private boolean isClickUpload = false;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_report_confirm:

                    if (etStoreAddress.getText().toString().equals("") || etStoreName.getText().toString().equals("")) {
                        showErrorDataDialog();
                    } else {
                        showThanksDialog();
                    }

                    break;
            }

        }
    };

    private Spinner.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            storeStatusIndex = position;

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//            Log.i(TAG, "onChildAdded");
            if (isClickUpload) {
                isClickUpload = false;
                Toast.makeText(ReportActivity.this, getString(R.string.report_success), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            Log.i(TAG, "onChildChanged");
            if (isClickUpload) {
                isClickUpload = false;
                Toast.makeText(ReportActivity.this, getString(R.string.report_success), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
//            Log.i(TAG, "onChildRemoved");
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            Log.i(TAG, "onChildMoved");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
//            Log.i(TAG, "onCancelled");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initView();

        spinnerStoreStatus.setOnItemSelectedListener(itemSelectedListener);
        btnConfirm.setOnClickListener(clickListener);

        if (fireBaseModelApi == null) {
            fireBaseModelApi = new FireBaseModelApiImpl();
            fireBaseModelApi.getDatabaseRef().addChildEventListener(childEventListener);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fireBaseModelApi != null) {
            fireBaseModelApi.getDatabaseRef().removeEventListener(childEventListener);
        }
    }

    private void initView() {
        spinnerStoreStatus = (Spinner) findViewById(R.id.spinner_report_store_status);
        etStoreName = (EditText) findViewById(R.id.et_report_store_name);
        etStoreAddress = (EditText) findViewById(R.id.et_report_store_address);
        btnConfirm = (Button) findViewById(R.id.btn_report_confirm);

    }

    private void onClickConfirm(String name, String address, String status) {
        isClickUpload = true;

        String ur_key = fireBaseModelApi.getDatabaseRef().child(TABLE_USER_REPORT).push().getKey();

        StoreDTO ur_StoreDTO = new StoreDTO();
        ur_StoreDTO.setStoreName(name);
        ur_StoreDTO.setStoreAddress(address);
        ur_StoreDTO.setStoreStatus(status);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TABLE_USER_REPORT + "/" + ur_key, ur_StoreDTO);

        fireBaseModelApi.getDatabaseRef().updateChildren(childUpdates);

    }


    private String getStoreStatus(int index) {
        String[] storeStatus = getResources().getStringArray(R.array.store_status);

        if (index >= storeStatus.length && index < 0) {
            return "";
        } else {
            return storeStatus[index];
        }

    }

    private void showThanksDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.report_dialog_thanks_title)
                .setMessage(R.string.report_dialog_thanks_message)
                .setCancelable(false)
                .setPositiveButton(R.string.upload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onClickConfirm(etStoreName.getText().toString(), etStoreAddress.getText().toString(), getStoreStatus(storeStatusIndex));
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void showErrorDataDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.report_dialog_error_data_message)
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }

}
