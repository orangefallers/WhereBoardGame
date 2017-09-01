package com.ofcat.whereboardgame.findperson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.model.GetLatLngDataImpl;
import com.ofcat.whereboardgame.util.MyLog;


/**
 * 自訂揪團
 * Created by orangefaller on 2017/8/29.
 */

public class CustomFindPersonActivity extends AppCompatActivity {

    private final String TAG = CustomFindPersonActivity.class.getSimpleName();

    private final String TEST_ADDRESS = "新北市板橋區松江街72巷28號";

//    private TextView tvStoreAddress;

    private GetLatLngDataImpl getLatLngData;


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
//            switch (view.getId()) {
//                case R.id.tv_custom_find_person_store:
//                    getLatLngData.getLatLngByAddress(tvStoreAddress.getText().toString());
//                    break;
//            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_find_person);
        initActionBar();
        initView();

        if (getLatLngData == null) {
            getLatLngData = new GetLatLngDataImpl(latLngDataImplListener);
        }
    }

    private void initView() {
//        tvStoreAddress = (TextView) findViewById(R.id.tv_custom_find_person_store);
//        tvStoreAddress.setText(TEST_ADDRESS);

//        tvStoreAddress.setOnClickListener(clickListener);

    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.custom_findperson_title);
        } catch (NullPointerException e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getLatLngData != null) {
            getLatLngData.onDestroy();
            getLatLngData = null;
        }
    }
}
