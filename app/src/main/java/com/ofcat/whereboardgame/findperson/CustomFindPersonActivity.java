package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.ofcat.whereboardgame.config.AppConfig;
import com.ofcat.whereboardgame.firebase.dataobj.LocationDTO;
import com.ofcat.whereboardgame.firebase.dataobj.UserInfoDTO;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseUrl;
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
    private Button btnConfirm;


    private String userId = "", lastStoreId = "";
    private Calendar now;
    private String nowDate;
    private String recordStorePlace, recordInitiator, recordTime, recordContact, recordContent;
    private String storePlace, storeAddress;
    private double storeLat, storeLng;

    private WaitPlayerRoomDTO customWaitPlayerRoomDTO;
    private GetLatLngDataImpl getLatLngData;

    private DatePickerDialog datePickerDialog;


    private ChildEventListener customChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

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
            switch (view.getId()) {
                case R.id.tv_custom_find_person_date:
                    showDateDialog(view, now);
                    break;
                case R.id.tv_custom_find_person_place:
                    switchInputAddressFragment();
                    break;
                case R.id.btn_custom_find_person_confirm:

                    String initiator = etInitiator.getText().toString();
                    String time = etTime.getText().toString();
                    String contact = etContact.getText().toString();
                    String content = etOther.getText().toString();

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
                    customWaitPlayerRoomDTO.setLocation(new LocationDTO(storeLat, storeLng));

                    uploadData(customWaitPlayerRoomDTO);

                    if (!StringUtility.isEmpty(lastStoreId) && !lastStoreId.equals(FirebaseTableKey.CUSTOM_STORE_ID)){
                        removeData(lastStoreId, userId);
                    }

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

        initFirebase();
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

        tvPlace.setOnClickListener(clickListener);
        tvDate.setOnClickListener(clickListener);
        btnConfirm.setOnClickListener(clickListener);


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
            tvPlace.setText(getPlaceStr(emptyStr));
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

    private void initFirebase() {
        FireBaseUrl customUrl = new FireBaseUrl.Builder()
                .addUrlNote(FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM)
                .build();

        customWaitPlayerRoom = firebaseDatabase.getReferenceFromUrl(customUrl.getUrl());
        customWaitPlayerRoom.addChildEventListener(customChildEventListener);

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

    private String getPlaceStr(String place) {
        return String.format(getResources().getString(R.string.findperson_place), place);
    }

    private String getDateStr(String date) {
        return String.format(getString(R.string.findperson_date), date);
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

        Map<String, Object> customChildUpdates = new HashMap<>();
        customChildUpdates.put("/" + userId, waitPlayerRoomDTO);

        customWaitPlayerRoom.updateChildren(customChildUpdates);


//        Map<String, Object> userInfoChildUpdates = new HashMap<>();
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setStoreId(FirebaseTableKey.CUSTOM_STORE_ID);
        userInfoDTO.setWaitPlayerRoomDTO(waitPlayerRoomDTO);
//        userInfoChildUpdates.put("/", userInfoDTO);

//        userinfo.updateChildren(userInfoChildUpdates);
        userinfo.setValue(userInfoDTO);

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
