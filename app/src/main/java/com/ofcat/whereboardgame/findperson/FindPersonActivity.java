package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.LocationDTO;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApi;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;
import com.ofcat.whereboardgame.util.FirebaseTableKey;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

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

    public static final String KEY_SP_INITIATOR = "sp_key_initiator";
    public static final String KEY_SP_TIME = "sp_key_time";
    public static final String KEY_SP_CONTACT = "sp_key_contact";
    public static final String KEY_SP_CONTENT = "sp_key_content";


    private static final String TABLE_WAITPLYERROOM = FirebaseTableKey.TABLE_WAITPLYERROOM;

    private FireBaseModelApi fireBaseModelApi;

    private WaitPlayerRoomDTO userWaitPlayerRoomDTO;

    private RecyclerView rvFindPerson;
    private DatePickerDialog datePickerDialog;

    private String bgsPlace, bgsId, bgsTag;
    private String recordInitiator, recordTime, recordContact, recordContent;

    private FindPersonAdapter adapter;

    private Button btnConfirm;

    private SharedPreferences sp;


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_find_person_confirm:
//                    uploadData();
                    if (adapter != null) {
//                        adapter.notifyDataSetChanged();
//                            Log.i("kevintest", "text = " + text);
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

                        SaveDataToSP(adapter.getInfoTextArray()[2],
                                adapter.getInfoTextArray()[3],
                                adapter.getInfoTextArray()[4],
                                adapter.getInfoTextArray()[5]);

                        uploadData(userWaitPlayerRoomDTO);


                    }
                    break;
                default:
                    break;
            }

        }
    };

    private ChildEventListener childEventListener = new ChildEventListener() {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findperson);
        bgsPlace = getIntent().getStringExtra(KEY_FINDPERSON_BGS_PLACE);
        bgsId = getIntent().getStringExtra(KEY_FINDPERSON_BGS_ID);
        bgsTag = getIntent().getStringExtra(KEY_FINDPERSON_BGS_PLACE_TAG);

        sp = getSharedPreferences("BGS_DATA", MODE_PRIVATE);
        recordInitiator = sp.getString(KEY_SP_INITIATOR, "");
        recordTime = sp.getString(KEY_SP_TIME, "");
        recordContact = sp.getString(KEY_SP_CONTACT, "");
        recordContent = sp.getString(KEY_SP_CONTENT, "");

        initActionBar();
        initView();


        if (fireBaseModelApi == null) {
            fireBaseModelApi = new FireBaseModelApiImpl();
            fireBaseModelApi.getDefaultDatabaseRef().addChildEventListener(childEventListener);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fireBaseModelApi != null) {
            fireBaseModelApi.getDefaultDatabaseRef().removeEventListener(childEventListener);
        }
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
//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                for (String text : adapter.getInfoTextArray()) {
//                    Log.i("kevintest", "text = " + text);
//                }
//            }
//        });

        btnConfirm = (Button) findViewById(R.id.btn_find_person_confirm);
        btnConfirm.setOnClickListener(clickListener);

    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.map_choose_button_find_person);
        } catch (NullPointerException e) {

        }
    }


    private void uploadData(WaitPlayerRoomDTO waitPlayerRoomDTO) {

//        WaitPlayerRoomDTO waitPlayerRoomDTO = new WaitPlayerRoomDTO();
//        waitPlayerRoomDTO.setContact("0979888720");
//        waitPlayerRoomDTO.setContent("水世界團，禁帶外食，包日100元。拜託快來找我玩");
//        waitPlayerRoomDTO.setDate("2017/5/21");
//        waitPlayerRoomDTO.setInitiator("Orangefaller");
//
//        LocationDTO locationDTO = new LocationDTO(25.123321, 121.746898);
//        waitPlayerRoomDTO.setLocation(locationDTO);

        String ur_key = fireBaseModelApi.getDefaultDatabaseRef().child(TABLE_WAITPLYERROOM + "/" + bgsId).push().getKey();


        ArrayList<WaitPlayerRoomDTO> waitPlayerRoomDTOs = new ArrayList<>();
        waitPlayerRoomDTOs.add(waitPlayerRoomDTO);
        waitPlayerRoomDTOs.add(waitPlayerRoomDTO);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TABLE_WAITPLYERROOM + "/" + bgsId + "/" + ur_key, waitPlayerRoomDTO);

        fireBaseModelApi.getDefaultDatabaseRef().updateChildren(childUpdates);

    }

    private void removeData() {
        fireBaseModelApi.getDefaultDatabaseRef().child("/" + TABLE_WAITPLYERROOM + "/" + "100003").removeValue();
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

    private void SaveDataToSP(String initiator, String time, String contact, String content) {
        sp.edit()
                .putString(KEY_SP_INITIATOR, initiator)
                .putString(KEY_SP_TIME, time)
                .putString(KEY_SP_CONTACT, contact)
                .putString(KEY_SP_CONTENT, content)
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
}


