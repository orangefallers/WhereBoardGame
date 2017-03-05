package com.ofcat.whereboardgame.report;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by orangefaller on 2017/3/5.
 */

public class ReportFragment extends Fragment {
    private static final String TAG = ReportFragment.class.getSimpleName();
    private static final String TABLE_USER_REPORT = "UserReport";

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

                    if (etStoreAddress.getText().toString().trim().equals("") || etStoreName.getText().toString().trim().equals("")) {
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
            Log.i(TAG, "onChildAdded");
            if (isClickUpload) {
                isClickUpload = false;
                Toast.makeText(getActivity(), getString(R.string.report_success), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.i(TAG, "onChildChanged");
            Log.i(TAG, "isClick = " + isClickUpload);
            if (isClickUpload) {
                isClickUpload = false;
                Toast.makeText(getActivity(), getString(R.string.report_success), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.i(TAG, "onChildRemoved");
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            Log.i(TAG, "onChildMoved");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.i(TAG, "onCancelled");
        }
    };


    public static ReportFragment newInstance() {
        ReportFragment fragment = new ReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (fireBaseModelApi == null) {
            fireBaseModelApi = new FireBaseModelApiImpl();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_report, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fireBaseModelApi.getDefaultDatabaseRef().addChildEventListener(childEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fireBaseModelApi != null) {
            fireBaseModelApi.getDefaultDatabaseRef().removeEventListener(childEventListener);
        }

    }

    private void initView(View view) {
        spinnerStoreStatus = (Spinner) view.findViewById(R.id.spinner_report_store_status);
        etStoreName = (EditText) view.findViewById(R.id.et_report_store_name);
        etStoreAddress = (EditText) view.findViewById(R.id.et_report_store_address);
        btnConfirm = (Button) view.findViewById(R.id.btn_report_confirm);

        spinnerStoreStatus.setOnItemSelectedListener(itemSelectedListener);
        btnConfirm.setOnClickListener(clickListener);

    }

    private void onClickConfirm(String name, String address, String status) {
        isClickUpload = true;

        String ur_key = fireBaseModelApi.getDefaultDatabaseRef().child(TABLE_USER_REPORT).push().getKey();

        StoreDTO ur_StoreDTO = new StoreDTO();
        ur_StoreDTO.setStoreName(name);
        ur_StoreDTO.setStoreAddress(address);
        ur_StoreDTO.setStoreStatus(status);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TABLE_USER_REPORT + "/" + ur_key, ur_StoreDTO);

        fireBaseModelApi.getDefaultDatabaseRef().updateChildren(childUpdates);

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
        new AlertDialog.Builder(getActivity()).setTitle(R.string.report_dialog_thanks_title)
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
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.report_dialog_error_data_message)
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }
}
