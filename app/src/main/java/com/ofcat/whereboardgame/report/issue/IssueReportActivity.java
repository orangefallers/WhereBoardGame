package com.ofcat.whereboardgame.report.issue;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.IssueReportDTO;
import com.ofcat.whereboardgame.firebase.dataobj.StoreDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;
import com.ofcat.whereboardgame.util.FirebaseTableKey;
import com.ofcat.whereboardgame.util.MyLog;
import com.ofcat.whereboardgame.util.SystemUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by orangefaller on 2017/8/11.
 */

public class IssueReportActivity extends AppCompatActivity {

    private static final String TAG = IssueReportActivity.class.getSimpleName();
    private static final String KEY_SP_ISSUE_MESSAGE = "sp_key_issue_message";

    private FirebaseDatabase database;
    private FireBaseModelApiImpl fireBaseModelApi;

    private SharedPreferences sp;

    private EditText etIssueUserReport;
    private TextView tvSystemAnswer;
    private Button btnConfirm;
    private Button btnCleanMessage;

    private String userIssueMsg;
    private String tempKey;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_issue_report_confirm:

                    issueUpload(etIssueUserReport.getText().toString());
                    break;
                case R.id.btn_issue_report_clean:
                    issueClean();
                    break;
            }

        }
    };


    private ChildEventListener issueChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            MyLog.i(TAG, "report user key = " + dataSnapshot.getKey());
            IssueReportDTO reportDTO = dataSnapshot.getValue(IssueReportDTO.class);
            MyLog.i(TAG, "report user message = " + reportDTO.getUserIssueReport());

            if (tempKey.equals(dataSnapshot.getKey())) {
                showUserIssueReportMessage(reportDTO.getUserIssueReport());
                showSystemAnswer(reportDTO.getSystemIssueAnswer());
            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            MyLog.i(TAG, "report user key = " + dataSnapshot.getKey());
            IssueReportDTO reportDTO = dataSnapshot.getValue(IssueReportDTO.class);
            MyLog.i(TAG, "report user message = " + reportDTO.getUserIssueReport());

            if (tempKey.equals(dataSnapshot.getKey())) {
                showUserIssueReportMessage(reportDTO.getUserIssueReport());
                showSystemAnswer(reportDTO.getSystemIssueAnswer());
            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            MyLog.i(TAG, "report user key = " + dataSnapshot.getKey());
            if (tempKey.equals(dataSnapshot.getKey())) {
                showSystemAnswer(null);
                showUserIssueReportMessage(null);
            }

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_report);
        initView();
        database = FirebaseDatabase.getInstance();
        sp = getSharedPreferences("BGS_DATA", MODE_PRIVATE);
        tempKey = sp.getString(KEY_SP_ISSUE_MESSAGE, "");

        if (fireBaseModelApi == null) {
            fireBaseModelApi = new FireBaseModelApiImpl().addApiNote(FirebaseTableKey.TABLE_SUGGESTIONS);
            fireBaseModelApi.execute();
            fireBaseModelApi.getDatabaseRef().addChildEventListener(issueChildEventListener);
        }


    }

    private void initView() {
        etIssueUserReport = (EditText) findViewById(R.id.et_issue_user_input_message);
        tvSystemAnswer = (TextView) findViewById(R.id.tv_issue_system_answer);
        btnConfirm = (Button) findViewById(R.id.btn_issue_report_confirm);
        btnCleanMessage = (Button) findViewById(R.id.btn_issue_report_clean);
        btnConfirm.setOnClickListener(clickListener);
        btnCleanMessage.setOnClickListener(clickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void issueUpload(String userIssueMessage) {

        if (tempKey.trim().equals("")) {
            tempKey = fireBaseModelApi.getDatabaseRef().push().getKey();
            sp.edit().putString(KEY_SP_ISSUE_MESSAGE, tempKey).apply();
        }


//        String ur_key = fireBaseModelApi.getDatabaseRef().push().getKey();

        IssueReportDTO issueReportDTO = new IssueReportDTO();
        issueReportDTO.setUserIssueReport(userIssueMessage);
        issueReportDTO.setSystemIssueAnswer("");
        issueReportDTO.setIssueTimeStamp(SystemUtility.getTimeStamp());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + tempKey, issueReportDTO);

        fireBaseModelApi.getDatabaseRef().updateChildren(childUpdates);

    }

    private void issueClean() {

        String ur_key = sp.getString(KEY_SP_ISSUE_MESSAGE, "");
        if (!ur_key.trim().equals("")) {
            fireBaseModelApi.getDatabaseRef().child(ur_key).removeValue();
        }

    }


    private void showSystemAnswer(String systemAnswer) {

        if (systemAnswer == null) {
            tvSystemAnswer.setText("");
        } else if (systemAnswer.equals("")) {
            tvSystemAnswer.setText(getString(R.string.issue_report_system_default_answer));
        } else {
            tvSystemAnswer.setText(systemAnswer);
        }

    }

    private void showUserIssueReportMessage(String userIssueMsg) {

        if (userIssueMsg == null) {
            etIssueUserReport.setText("");
        } else {
            etIssueUserReport.setText(userIssueMsg);
        }

    }


}
