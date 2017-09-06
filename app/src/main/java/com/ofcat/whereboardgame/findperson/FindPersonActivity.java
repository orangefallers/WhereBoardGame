package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

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
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;
import com.ofcat.whereboardgame.firebase.model.FireBaseUrl;
import com.ofcat.whereboardgame.joinplay.PlayerRoomDetailFragment;
import com.ofcat.whereboardgame.util.FirebaseTableKey;
import com.ofcat.whereboardgame.util.MyLog;
import com.ofcat.whereboardgame.util.SharedPreferenceKey;
import com.ofcat.whereboardgame.util.SystemUtility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by orangefaller on 2017/4/16.
 */

public class FindPersonActivity extends AppCompatActivity {
    public static final String KEY_FINDPERSON_BGS_PLACE = "key_findperson_bgs_place";
    public static final String KEY_FINDPERSON_BGS_ID = "key_findperson_bgs_id";
    public static final String KEY_FINDPERSON_BGS_PLACE_TAG = "key_findperson_bgs_place_tag";
    public static final String KEY_FINDPERSON_BGS_ADDRESS = "key_findperson_bgs_address";
    public static final String KEY_FINDPERSON_BGS_LAT = "key_findperson_bgs_lat";
    public static final String KEY_FINDPERSON_BGS_LNG = "key_findperson_bgs_lng";


    private static final String TABLE_WAITPLYERROOM = FirebaseTableKey.TABLE_WAITPLYERROOM;
    private static final String TABLE_USERINFO = FirebaseTableKey.TABLE_USER_INFO;

    private FireBaseModelApiImpl fireBaseModelApi;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private WaitPlayerRoomDTO userWaitPlayerRoomDTO;

    private RecyclerView rvFindPerson;
    private DatePickerDialog datePickerDialog;

    private String bgsPlace, bgsId, bgsTag, bgsAddress;
    private String recordInitiator, recordTime, recordContact, recordContent;
    private String userId = "", lastStoreId = "";
    private double bgsLat, bgsLng;

    private FindPersonAdapter adapter;

    private Button btnConfirm;
    private Button btnPreview;

    private SharedPreferences sp;

    private DatabaseReference databaseReference;

    private AlertDialog successDialog;


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_find_person_confirm:
                    if (adapter != null) {

                        if (userWaitPlayerRoomDTO == null) {
                            userWaitPlayerRoomDTO = new WaitPlayerRoomDTO();
                        }

                        userWaitPlayerRoomDTO.setStoreName(adapter.getInfoTextArray()[0]);
                        userWaitPlayerRoomDTO.setDate(adapter.getInfoTextArray()[1]);
                        userWaitPlayerRoomDTO.setInitiator(adapter.getInfoTextArray()[2]);
                        userWaitPlayerRoomDTO.setTime(adapter.getInfoTextArray()[3]);
                        userWaitPlayerRoomDTO.setContact(adapter.getInfoTextArray()[4]);
                        userWaitPlayerRoomDTO.setContent(adapter.getInfoTextArray()[5]);
                        userWaitPlayerRoomDTO.setAddressTag(bgsTag);
                        userWaitPlayerRoomDTO.setStoreAddress(bgsAddress);
                        userWaitPlayerRoomDTO.setTimeStamp(SystemUtility.getTimeStamp());
                        userWaitPlayerRoomDTO.setTimeStampOrder(ServerValue.TIMESTAMP);
                        userWaitPlayerRoomDTO.setLocation(new LocationDTO(bgsLat, bgsLng));

                        SaveDataToSP(adapter.getInfoTextArray()[2],
                                adapter.getInfoTextArray()[3],
                                adapter.getInfoTextArray()[4],
                                adapter.getInfoTextArray()[5]);

//                        uploadData(userWaitPlayerRoomDTO);
                        checkWaitPlayerRoomData(userWaitPlayerRoomDTO);

                    }
                    break;
                case R.id.btn_find_person_preview:

                    if (userWaitPlayerRoomDTO == null) {
                        userWaitPlayerRoomDTO = new WaitPlayerRoomDTO();
                    }

                    userWaitPlayerRoomDTO.setStoreName(adapter.getInfoTextArray()[0]);
                    userWaitPlayerRoomDTO.setDate(adapter.getInfoTextArray()[1]);
                    userWaitPlayerRoomDTO.setInitiator(adapter.getInfoTextArray()[2]);
                    userWaitPlayerRoomDTO.setTime(adapter.getInfoTextArray()[3]);
                    userWaitPlayerRoomDTO.setContact(adapter.getInfoTextArray()[4]);
                    userWaitPlayerRoomDTO.setContent(adapter.getInfoTextArray()[5]);
                    userWaitPlayerRoomDTO.setAddressTag(bgsTag);
                    userWaitPlayerRoomDTO.setTimeStamp(SystemUtility.getTimeStamp());
                    userWaitPlayerRoomDTO.setTimeStampOrder(ServerValue.TIMESTAMP);

                    switchDetailFragment(userWaitPlayerRoomDTO);
                    break;
                default:
                    break;
            }

        }
    };

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            upLoading(false);
            String dataKey = dataSnapshot.getKey(); //應該要是UserId
            if (userId.equals(dataKey)) {  //表示是自己更新的資料
                showUpLoadSuccessDialog();
            }

//            Toast.makeText(FindPersonActivity.this, "onChildAdded = " + dataSnapshot.getKey(),
//                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            upLoading(false);
            String dataKey = dataSnapshot.getKey(); //應該要是UserId
            if (userId.equals(dataKey)) {  //表示是自己更新的資料
                showUpLoadSuccessDialog();
            }

//            Toast.makeText(FindPersonActivity.this, "onChildChange = " + dataSnapshot.getKey(),
//                    Toast.LENGTH_SHORT).show();


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

    private FirebaseAuth.AuthStateListener stateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                userId = user.getUid();
                databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + TABLE_USERINFO + "/" + userId);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("storeId")) {
                            lastStoreId = dataSnapshot.child("storeId").getValue(String.class);
//                            Log.i("kevintest", " lastStoreId = " + lastStoreId);
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
        setContentView(R.layout.activity_findperson);
        bgsPlace = getIntent().getStringExtra(KEY_FINDPERSON_BGS_PLACE);
        bgsAddress = getIntent().getStringExtra(KEY_FINDPERSON_BGS_ADDRESS);
        bgsId = getIntent().getStringExtra(KEY_FINDPERSON_BGS_ID);
        bgsTag = getIntent().getStringExtra(KEY_FINDPERSON_BGS_PLACE_TAG);
        bgsLat = getIntent().getDoubleExtra(KEY_FINDPERSON_BGS_LAT, 0);
        bgsLng = getIntent().getDoubleExtra(KEY_FINDPERSON_BGS_LNG, 0);

        sp = getSharedPreferences("BGS_DATA", MODE_PRIVATE);
        recordInitiator = sp.getString(SharedPreferenceKey.KEY_SP_INITIATOR, "");
        recordTime = sp.getString(SharedPreferenceKey.KEY_SP_TIME, "");
        recordContact = sp.getString(SharedPreferenceKey.KEY_SP_CONTACT, "");
        recordContent = sp.getString(SharedPreferenceKey.KEY_SP_CONTENT, "");

        initActionBar();
        initView();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
//        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + TABLE_WAITPLYERROOM + "/" + bgsId);
//        databaseReference.addChildEventListener(childEventListener);

//        databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + TABLE_WAITPLYERROOM + "/$$WaitPlayerRoom_id");

//        if (fireBaseModelApi == null) {
//            fireBaseModelApi = new FireBaseModelApiImpl().addApiNote(TABLE_WAITPLYERROOM).addApiNote(bgsId);
//            fireBaseModelApi.execute();
//            fireBaseModelApi.getDefaultDatabaseRef().addChildEventListener(childEventListener);
//        }

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
        if (databaseReference != null) {
            databaseReference.removeEventListener(childEventListener);
        }

        if (fireBaseModelApi != null) {
            fireBaseModelApi.getDefaultDatabaseRef().removeEventListener(childEventListener);
            fireBaseModelApi = null;
        }

        successDialog = null;
    }

    private void initView() {
        rvFindPerson = (RecyclerView) findViewById(R.id.rv_find_person_list);
        rvFindPerson.setLayoutManager(new LinearLayoutManager(this));

        if (adapter == null) {
            adapter = new FindPersonAdapter(this);
            adapter.setBoardGameStorePlace(bgsPlace);
            adapter.setUserEditRecord(recordInitiator, recordTime, recordContact, recordContent);
            adapter.setFindPersonAdapterListener(adapterListener);

        }
        rvFindPerson.setAdapter(adapter);
        btnConfirm = (Button) findViewById(R.id.btn_find_person_confirm);
        btnPreview = (Button) findViewById(R.id.btn_find_person_preview);
        btnConfirm.setOnClickListener(clickListener);
        btnPreview.setOnClickListener(clickListener);

    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.map_choose_button_find_person);
        } catch (NullPointerException e) {

        }
    }

    private void upLoading(boolean isUpLoading) {
        btnConfirm.setEnabled(!isUpLoading);
    }


    private void uploadData(WaitPlayerRoomDTO waitPlayerRoomDTO) {

//        String ur_key = fireBaseModelApi.getDefaultDatabaseRef().child(TABLE_WAITPLYERROOM + "/" + bgsId).push().getKey()
//        String ur_key = databaseReference.child(TABLE_WAITPLYERROOM + "/" + bgsId).push().getKey();

        if (userId != null && !"".equals(userId)) {

            databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + TABLE_WAITPLYERROOM + "/" + bgsId);
            databaseReference.addChildEventListener(childEventListener);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/" + userId, waitPlayerRoomDTO);
//            childUpdates.put("/" + ur_key, waitPlayerRoomDTO);

//            fireBaseModelApi.getDefaultDatabaseRef().updateChildren(childUpdates);
            databaseReference.updateChildren(childUpdates);


            databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + TABLE_USERINFO);

            Map<String, Object> userInfoChildUpdates = new HashMap<>();
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setStoreId(bgsId);
            userInfoDTO.setWaitPlayerRoomDTO(waitPlayerRoomDTO);
            userInfoChildUpdates.put("/" + userId, userInfoDTO);

            databaseReference.updateChildren(userInfoChildUpdates);

            if (!lastStoreId.equals(bgsId) && !lastStoreId.equals("")) {
                removeData(lastStoreId, userId);
            }

        } else {
            MyLog.e("kevintest", "no auth!!!!!!");
        }


    }

    private void removeData(String storeId, String userId) {

        if (!storeId.equals("") && !userId.equals("")) {

            if (storeId.equals(FirebaseTableKey.CUSTOM_STORE_ID)) {
                FireBaseUrl customRoomUrl = new FireBaseUrl.Builder()
                        .addUrlNote(FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM)
                        .addUrlNote(userId)
                        .build();
                databaseReference = database.getReferenceFromUrl(customRoomUrl.getUrl());
                databaseReference.removeValue();
            } else {
                FireBaseUrl storeRoomUrl = new FireBaseUrl.Builder()
                        .addUrlNote(FirebaseTableKey.TABLE_WAITPLYERROOM)
                        .addUrlNote(storeId)
                        .addUrlNote(userId)
                        .build();
                databaseReference = database.getReferenceFromUrl(storeRoomUrl.getUrl());
                databaseReference.removeValue();
            }

//            databaseReference = database.getReferenceFromUrl(AppConfig.FIREBASE_URL + "/" + TABLE_WAITPLYERROOM + "/" + storeId + "/" + userId);
//            databaseReference.removeValue();
        }
    }

    private void updateName() {
        fireBaseModelApi.getDefaultDatabaseRef().child("/" + TABLE_WAITPLYERROOM).child("100003").child("initiator").setValue("Kevin Hsieh");
    }

    private void addListData() {
        ArrayList<WaitPlayerRoomDTO> lists = new ArrayList<>();
        WaitPlayerRoomDTO waitPlayerRoomDTO = new WaitPlayerRoomDTO();
        waitPlayerRoomDTO.setContact("0979888720");
        waitPlayerRoomDTO.setContent("水世界團，禁帶外食，包日100元。拜託快來找我玩");
        waitPlayerRoomDTO.setDate("2017/5/21");
        waitPlayerRoomDTO.setInitiator("Orangefaller");

        LocationDTO locationDTO = new LocationDTO(25.123321, 121.746898);
//        waitPlayerRoomDTO.setLocation(locationDTO);

        lists.add(waitPlayerRoomDTO);
        lists.add(waitPlayerRoomDTO);
        lists.add(waitPlayerRoomDTO);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TABLE_WAITPLYERROOM + "/" + "100003", lists);

        fireBaseModelApi.getDefaultDatabaseRef().updateChildren(childUpdates);

    }

    private void checkWaitPlayerRoomData(WaitPlayerRoomDTO roomDTO) {
        if (!roomDTO.isCompleteDTO()) {
            showErrorDataDialog();
        } else {
            upLoading(true);
            uploadData(roomDTO);

        }
    }

    private void switchDetailFragment(WaitPlayerRoomDTO roomDTO) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("player_room_detail")
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.rl_find_person_root, PlayerRoomDetailFragment.newInstance(roomDTO))
                .commit();

    }


    private void SaveDataToSP(String initiator, String time, String contact, String content) {
        sp.edit()
                .putString(SharedPreferenceKey.KEY_SP_INITIATOR, initiator)
                .putString(SharedPreferenceKey.KEY_SP_TIME, time)
                .putString(SharedPreferenceKey.KEY_SP_CONTACT, contact)
                .putString(SharedPreferenceKey.KEY_SP_CONTENT, content)
                .apply();
    }

    private FindPersonAdapter.AdapterListener adapterListener = new FindPersonAdapter.AdapterListener() {

        @Override
        public void onEditClick(View view, boolean hasFocus, String text, int position) {


        }

        @Override
        public void onTextClick(View view, int position) {

        }

        @Override
        public void onDateClick(View view, Calendar calendar) {

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
    };

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            if (adapter != null) {
                adapter.setSelectDate(year, monthOfYear, dayOfMonth);
                adapter.notifyDataSetChanged();

            }
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
                .setMessage(R.string.findperson_dialog_error_data_message)
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }
}


