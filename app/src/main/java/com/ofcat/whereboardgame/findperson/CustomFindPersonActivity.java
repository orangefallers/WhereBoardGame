package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.LocationDTO;
import com.ofcat.whereboardgame.firebase.dataobj.UserInfoDTO;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseUrl;
import com.ofcat.whereboardgame.joinplay.PlayerRoomDetailFragment;
import com.ofcat.whereboardgame.map.InputAddressMapFragment;
import com.ofcat.whereboardgame.model.GetLatLngDataImpl;
import com.ofcat.whereboardgame.util.DateUtility;
import com.ofcat.whereboardgame.util.FirebaseTableKey;
import com.ofcat.whereboardgame.util.MyLog;
import com.ofcat.whereboardgame.util.SharedPreferenceKey;
import com.ofcat.whereboardgame.util.StringUtility;
import com.ofcat.whereboardgame.util.SystemUtility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * 自訂揪團
 * Created by orangefaller on 2017/8/29.
 */

public class CustomFindPersonActivity extends AppCompatActivity implements InputAddressMapFragment.InputAddressMapFragmentListener {

    private final String TAG = CustomFindPersonActivity.class.getSimpleName();
//    private final String CUSTOM_STORE_ID = "000999";

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference customWaitPlayerRoom;
    private DatabaseReference userinfo;
    private DatabaseReference removeRoom;
    private SharedPreferences sp;

    private TextView tvPlace;
    private TextView tvDate;
    private EditText etStore;
    private EditText etInitiator;
    private EditText etTime;
    private EditText etContact;
    private EditText etOther;
    private Button btnConfirm, btnPreview;

    private String userId = "", lastStoreId = "";
    private Calendar now;
    private String nowDate;
    private String recordStorePlace, recordInitiator, recordTime, recordContact, recordContent;
    private String storePlace, storeAddress;
    private double storeLat, storeLng;

    private WaitPlayerRoomDTO customWaitPlayerRoomDTO;
    private GetLatLngDataImpl getLatLngData;

    private DatePickerDialog datePickerDialog;
    private AlertDialog successDialog;


    private ChildEventListener customChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            upLoading(false);
            MyLog.i(TAG, "data snap show key = " + dataSnapshot.getKey());
            String dataKey = dataSnapshot.getKey(); //應該要是UserId
            if (userId.equals(dataKey)) {  //表示是自己更新的資料
                showUpLoadSuccessDialog();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            upLoading(false);
            MyLog.i(TAG, "data snap show key = " + dataSnapshot.getKey());
            String dataKey = dataSnapshot.getKey(); //應該要是UserId
            if (userId.equals(dataKey)) {  //表示是自己更新的資料
                showUpLoadSuccessDialog();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            upLoading(false);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            upLoading(false);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            upLoading(false);
        }
    };

    private GetLatLngDataImpl.LatLngDataImplListener latLngDataImplListener = new GetLatLngDataImpl.LatLngDataImplListener() {
        @Override
        public void onResult(String address, double latitude, double longitude) {
            MyLog.i(TAG, "address = " + address + " lat = " + latitude + " lng = " + longitude);
        }

        @Override
        public void onFail(String status) {

        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String initiator;
            String time;
            String contact;
            String content;

            switch (view.getId()) {
                case R.id.tv_custom_find_person_date:
                    showDateDialog(view, now);
                    break;
                case R.id.tv_custom_find_person_place:
                    switchInputAddressFragment();
                    break;
                case R.id.btn_custom_find_person_confirm:

                    initiator = etInitiator.getText().toString();
                    time = etTime.getText().toString();
                    contact = etContact.getText().toString();
                    content = etOther.getText().toString();

                    SaveDataToSP(initiator, time, contact, content);


                    if (customWaitPlayerRoomDTO == null) {
                        customWaitPlayerRoomDTO = new WaitPlayerRoomDTO();
                    }

                    customWaitPlayerRoomDTO.setStoreName(storePlace);
                    customWaitPlayerRoomDTO.setDate(nowDate);
                    customWaitPlayerRoomDTO.setInitiator(initiator);
                    customWaitPlayerRoomDTO.setTime(time);
                    customWaitPlayerRoomDTO.setContact(contact);
                    customWaitPlayerRoomDTO.setContent(content);
                    customWaitPlayerRoomDTO.setAddressTag("自訂");
                    customWaitPlayerRoomDTO.setStoreAddress(storeAddress);
                    customWaitPlayerRoomDTO.setTimeStamp(SystemUtility.getTimeStamp());
                    customWaitPlayerRoomDTO.setTimeStampOrder(ServerValue.TIMESTAMP);
                    if (storeLat == 0.0 && storeLng == 0.0) {
                        customWaitPlayerRoomDTO.setLocation(null);
                    } else {
                        customWaitPlayerRoomDTO.setLocation(new LocationDTO(storeLat, storeLng));
                    }

//                    uploadData(customWaitPlayerRoomDTO);

                    checkWaitPlayerRoomData(customWaitPlayerRoomDTO);


                    break;
                case R.id.btn_custom_find_person_preview:
                    initiator = etInitiator.getText().toString();
                    time = etTime.getText().toString();
                    contact = etContact.getText().toString();
                    content = etOther.getText().toString();

                    if (customWaitPlayerRoomDTO == null) {
                        customWaitPlayerRoomDTO = new WaitPlayerRoomDTO();
                    }

                    customWaitPlayerRoomDTO.setStoreName(storePlace);
                    customWaitPlayerRoomDTO.setDate(nowDate);
                    customWaitPlayerRoomDTO.setInitiator(initiator);
                    customWaitPlayerRoomDTO.setTime(time);
                    customWaitPlayerRoomDTO.setContact(contact);
                    customWaitPlayerRoomDTO.setContent(content);
                    customWaitPlayerRoomDTO.setAddressTag("自訂");
                    customWaitPlayerRoomDTO.setStoreAddress(storeAddress);
                    customWaitPlayerRoomDTO.setTimeStamp(SystemUtility.getTimeStamp());
                    customWaitPlayerRoomDTO.setTimeStampOrder(ServerValue.TIMESTAMP);

                    if (storeLat == 0.0 && storeLng == 0.0) {
                        customWaitPlayerRoomDTO.setLocation(null);
                    } else {
                        customWaitPlayerRoomDTO.setLocation(new LocationDTO(storeLat, storeLng));
                    }

                    switchDetailFragment(customWaitPlayerRoomDTO);

                    break;
            }

        }
    };

    private FirebaseAuth.AuthStateListener stateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                userId = user.getUid();

                FireBaseUrl userInfoUrl = new FireBaseUrl.Builder()
                        .addUrlNote(FirebaseTableKey.TABLE_USER_INFO)
                        .addUrlNote(userId)
                        .build();

                userinfo = firebaseDatabase.getReferenceFromUrl(userInfoUrl.getUrl());
                userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("storeId")) {
                            lastStoreId = dataSnapshot.child("storeId").getValue(String.class);
                            MyLog.i("kevintest", " lastStoreId = " + lastStoreId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                userId = null;
            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_find_person);
        sp = getSharedPreferences("BGS_DATA", MODE_PRIVATE);
        recordStorePlace = sp.getString(SharedPreferenceKey.KEY_SP_STORE_PLACE, "");
        recordInitiator = sp.getString(SharedPreferenceKey.KEY_SP_INITIATOR, "");
        recordTime = sp.getString(SharedPreferenceKey.KEY_SP_TIME, "");
        recordContact = sp.getString(SharedPreferenceKey.KEY_SP_CONTACT, "");
        recordContent = sp.getString(SharedPreferenceKey.KEY_SP_CONTENT, "");
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        initActionBar();
        initView();
        initDate();

        if (getLatLngData == null) {
            getLatLngData = new GetLatLngDataImpl(latLngDataImplListener);
        }
    }

    private void initView() {
        tvPlace = (TextView) findViewById(R.id.tv_custom_find_person_place);
        tvDate = (TextView) findViewById(R.id.tv_custom_find_person_date);
        etInitiator = (EditText) findViewById(R.id.et_initiator_info);
        etTime = (EditText) findViewById(R.id.et_time_info);
        etContact = (EditText) findViewById(R.id.et_contact_info);
        etOther = (EditText) findViewById(R.id.et_other_info);
        btnConfirm = (Button) findViewById(R.id.btn_custom_find_person_confirm);
        btnPreview = (Button) findViewById(R.id.btn_custom_find_person_preview);

        tvPlace.setOnClickListener(clickListener);
        tvDate.setOnClickListener(clickListener);
        btnConfirm.setOnClickListener(clickListener);
        btnPreview.setOnClickListener(clickListener);


    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.custom_findperson_title);
        } catch (NullPointerException e) {

        }
    }

    private void initDate() {

        if (StringUtility.isEmpty(recordStorePlace)) {
            String emptyStr = getResources().getString(R.string.custom_findperson_empty_place);
            storePlace = "";
            tvPlace.setText(getHighLightOfRed(emptyStr).insert(0, getPlaceStr("")));
        } else {
            storePlace = recordStorePlace;
            tvPlace.setText(getPlaceStr(recordStorePlace));
        }

        now = Calendar.getInstance();
        nowDate = DateUtility.getCustomFormatDate(now);
        tvDate.setText(getDateStr(nowDate));

        etInitiator.setText(recordInitiator);
        etTime.setText(recordTime);
        etContact.setText(recordContact);
        etOther.setText(recordContent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(stateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getLatLngData != null) {
            getLatLngData.onDestroy();
            getLatLngData = null;
        }

        if (customWaitPlayerRoom != null) {
            customWaitPlayerRoom.removeEventListener(customChildEventListener);
            customWaitPlayerRoom = null;
        }

        if (removeRoom != null) {
            removeRoom = null;
        }
    }


    private void switchInputAddressFragment() {

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("input_address_fragment")
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.custom_find_person_root_layout, InputAddressMapFragment.newInstance())
                .commit();

    }

    private void switchDetailFragment(WaitPlayerRoomDTO roomDTO) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("player_room_detail")
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.custom_find_person_root_layout, PlayerRoomDetailFragment.newInstance(roomDTO))
                .commit();

    }

    private String getPlaceStr(String place) {
        return String.format(getResources().getString(R.string.findperson_place), place);
    }

    private String getDateStr(String date) {
        return String.format(getString(R.string.findperson_date), date);
    }

    private SpannableStringBuilder getHighLightOfRed(String inputStr) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(inputStr);
        ForegroundColorSpan redForegroundSpan = new ForegroundColorSpan(Color.RED);
        stringBuilder.setSpan(redForegroundSpan, 0, inputStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    private void checkWaitPlayerRoomData(WaitPlayerRoomDTO roomDTO) {
        if (!roomDTO.isCompleteDTO()) {
            showErrorDataDialog();
        } else {
            upLoading(true);
            uploadData(roomDTO);

            if (!StringUtility.isEmpty(lastStoreId) && !lastStoreId.equals(FirebaseTableKey.CUSTOM_STORE_ID)) {
                removeData(lastStoreId, userId);
            }
        }
    }

    private void upLoading(boolean isUpLoading) {
        btnConfirm.setEnabled(!isUpLoading);
    }

    private void showDateDialog(View view, Calendar calendar) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        datePickerDialog = DatePickerDialog.newInstance(
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 2);
        Calendar min = Calendar.getInstance();
        datePickerDialog.setMinDate(min);
        datePickerDialog.setMaxDate(max);
        datePickerDialog.vibrate(false);

        datePickerDialog.show(getFragmentManager(), "KevinDatePicker");

    }

    private void uploadData(WaitPlayerRoomDTO waitPlayerRoomDTO) {

        if (userId != null && !userId.equals("")) {

            FireBaseUrl customUrl = new FireBaseUrl.Builder()
                    .addUrlNote(FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM)
                    .build();

            customWaitPlayerRoom = firebaseDatabase.getReferenceFromUrl(customUrl.getUrl());
            customWaitPlayerRoom.addChildEventListener(customChildEventListener);

            Map<String, Object> customChildUpdates = new HashMap<>();
            customChildUpdates.put("/" + userId, waitPlayerRoomDTO);

            customWaitPlayerRoom.updateChildren(customChildUpdates);

            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setStoreId(FirebaseTableKey.CUSTOM_STORE_ID);
            userInfoDTO.setWaitPlayerRoomDTO(waitPlayerRoomDTO);
            userinfo.setValue(userInfoDTO);
        }

    }

    private void removeData(String storeId, String userId) {
        if (!storeId.equals("") && !userId.equals("")) {

            if (storeId.equals(FirebaseTableKey.CUSTOM_STORE_ID)) {
                FireBaseUrl customRoomUrl = new FireBaseUrl.Builder()
                        .addUrlNote(FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM)
                        .addUrlNote(userId)
                        .build();
                removeRoom = firebaseDatabase.getReferenceFromUrl(customRoomUrl.getUrl());
                removeRoom.removeValue();
            } else {
                FireBaseUrl storeRoomUrl = new FireBaseUrl.Builder()
                        .addUrlNote(FirebaseTableKey.TABLE_WAITPLYERROOM)
                        .addUrlNote(storeId)
                        .addUrlNote(userId)
                        .build();
                removeRoom = firebaseDatabase.getReferenceFromUrl(storeRoomUrl.getUrl());
                removeRoom.removeValue();
            }

        }
    }

    private void SaveDataToSP(String initiator, String time, String contact, String content) {
        sp.edit()
                .putString(SharedPreferenceKey.KEY_SP_INITIATOR, initiator)
                .putString(SharedPreferenceKey.KEY_SP_TIME, time)
                .putString(SharedPreferenceKey.KEY_SP_CONTACT, contact)
                .putString(SharedPreferenceKey.KEY_SP_CONTENT, content)
                .apply();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            Calendar selectCalendar = Calendar.getInstance();
            selectCalendar.set(year, monthOfYear, dayOfMonth);
            nowDate = DateUtility.getCustomFormatDate(selectCalendar);
            tvDate.setText(getDateStr(nowDate));

        }
    };

    private void showUpLoadSuccessDialog() {

        if (isFinishing()) {
            return;
        }

        if (successDialog == null) {
            successDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.findperson_success_title)
                    .setMessage(R.string.findperson_success_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    }).create();
            successDialog.show();
        } else if (!successDialog.isShowing()) {
            successDialog.show();
        }

    }

    private void showErrorDataDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.custom_findperson_dialog_error_data_message)
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }

    @Override
    public void onFragmentResult(String storeName, String address, double storeLat, double storeLng) {
        storePlace = storeName;
        storeAddress = address;
        this.storeLat = storeLat;
        this.storeLng = storeLng;
        MyLog.i(TAG, "store = " + storeName + " address = " + address + " lat = " + storeLat + " lng = " + storeLng);

        tvPlace.setText(getPlaceStr(storePlace));

    }
}
