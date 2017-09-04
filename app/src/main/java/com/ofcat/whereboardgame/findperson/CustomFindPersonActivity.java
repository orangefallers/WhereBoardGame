package com.ofcat.whereboardgame.findperson;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.joinplay.PlayerRoomDetailFragment;
import com.ofcat.whereboardgame.map.InputAddressMapFragment;
import com.ofcat.whereboardgame.model.GetLatLngDataImpl;
import com.ofcat.whereboardgame.util.DateUtility;
import com.ofcat.whereboardgame.util.MyLog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


/**
 * 自訂揪團
 * Created by orangefaller on 2017/8/29.
 */

public class CustomFindPersonActivity extends AppCompatActivity implements InputAddressMapFragment.InputAddressMapFragmentListener {

    private final String TAG = CustomFindPersonActivity.class.getSimpleName();

    private final String TEST_ADDRESS = "新北市板橋區松江街72巷28號";

    private TextView tvdate;
    private EditText etStore;
    private EditText etInitiator;
    private EditText etTime;
    private EditText etContact;
    private EditText etOther;
    private Button btnAddress;


    private Calendar now;
    private String nowDate;


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
                case R.id.btn_input_address:
                    switchInputAddressFragment();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_find_person);
        initActionBar();
        initView();
        initDate();

        if (getLatLngData == null) {
            getLatLngData = new GetLatLngDataImpl(latLngDataImplListener);
        }
    }

    private void initView() {
        tvdate = (TextView) findViewById(R.id.tv_custom_find_person_date);
        etStore = (EditText) findViewById(R.id.et_place_info);
        etInitiator = (EditText) findViewById(R.id.et_initiator_info);
        etTime = (EditText) findViewById(R.id.et_time_info);
        etContact = (EditText) findViewById(R.id.et_contact_info);
        etOther = (EditText) findViewById(R.id.et_other_info);
        btnAddress = (Button) findViewById(R.id.btn_input_address);

        tvdate.setOnClickListener(clickListener);
        btnAddress.setOnClickListener(clickListener);

    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.custom_findperson_title);
        } catch (NullPointerException e) {

        }
    }

    private void initDate() {
        now = Calendar.getInstance();
        nowDate = DateUtility.getCustomFormatDate(now);
        tvdate.setText(getDateStr(nowDate));
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

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            Calendar selectCalendar = Calendar.getInstance();
            selectCalendar.set(year, monthOfYear, dayOfMonth);
            nowDate = DateUtility.getCustomFormatDate(selectCalendar);
            tvdate.setText(getDateStr(nowDate));

        }
    };

    @Override
    public void onFragmentResult(String storeName, String address, double storeLat, double storeLng) {
        MyLog.i(TAG, "store = " + storeName + " address = " + address + " lat = " + storeLat + " lng = " + storeLng);
    }
}
