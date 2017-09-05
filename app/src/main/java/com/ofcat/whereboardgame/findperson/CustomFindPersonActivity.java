package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.map.InputAddressMapFragment;
import com.ofcat.whereboardgame.model.GetLatLngDataImpl;
import com.ofcat.whereboardgame.util.DateUtility;
import com.ofcat.whereboardgame.util.MyLog;
import com.ofcat.whereboardgame.util.SharedPreferenceKey;
import com.ofcat.whereboardgame.util.StringUtility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


/**
 * 自訂揪團
 * Created by orangefaller on 2017/8/29.
 */

public class CustomFindPersonActivity extends AppCompatActivity implements InputAddressMapFragment.InputAddressMapFragmentListener {

    private final String TAG = CustomFindPersonActivity.class.getSimpleName();

    private final String TEST_ADDRESS = "新北市板橋區松江街72巷28號";
    private SharedPreferences sp;

    private TextView tvPlace;
    private TextView tvDate;
    private EditText etStore;
    private EditText etInitiator;
    private EditText etTime;
    private EditText etContact;
    private EditText etOther;
//    private Button btnAddress;


    private Calendar now;
    private String nowDate;
    private String recordStorePlace, recordInitiator, recordTime, recordContact, recordContent;
    private String storePlace, storeAddress;
    private double storeLat, storeLng;


    private GetLatLngDataImpl getLatLngData;


    private DatePickerDialog datePickerDialog;

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
                case R.id.btn_custom_input_confirm:

                    String initiator = etInitiator.getText().toString();
                    String time = etTime.getText().toString();
                    String contact = etContact.getText().toString();
                    String content = etOther.getText().toString();


                    SaveDataToSP(initiator, time, contact, content);
                    break;
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
//        etStore = (EditText) findViewById(R.id.et_place_info);
        etInitiator = (EditText) findViewById(R.id.et_initiator_info);
        etTime = (EditText) findViewById(R.id.et_time_info);
        etContact = (EditText) findViewById(R.id.et_contact_info);
        etOther = (EditText) findViewById(R.id.et_other_info);
//        btnAddress = (Button) findViewById(R.id.btn_input_address);

        tvPlace.setOnClickListener(clickListener);
        tvDate.setOnClickListener(clickListener);
//        btnAddress.setOnClickListener(clickListener);

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
            tvPlace.setText(getPlaceStr(emptyStr));
        } else {
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
    protected void onDestroy() {
        super.onDestroy();
        if (getLatLngData != null) {
            getLatLngData.onDestroy();
            getLatLngData = null;
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
