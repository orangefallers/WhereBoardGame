package com.ofcat.whereboardgame.joinplay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;
import com.ofcat.whereboardgame.firebase.model.FireBaseUrl;
import com.ofcat.whereboardgame.util.FirebaseTableKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 只顯示單一桌遊店家的揪團列表
 * Created by orangefaller on 2017/9/9.
 */

public class PlayerStoreRoomListActivity extends AppCompatActivity {

    public static final String KEY_PLAYERROOMLIST_STORE_ID = "key_player_room_list_store_id";

//    private FireBaseModelApiImpl fireBaseModelApi;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference playerRoomList;

    private RecyclerView rvPlayerStoreRoomList;
    private PlayerRoomListAdapter adapter;

    private TextView tvEmptyMessage;

    private Set<String> keySet = new LinkedHashSet<>();
    private List<WaitPlayerRoomDTO> waitPlayerRoomDTOList = new ArrayList<>();

    private String queryStoreId = "";

    private ValueEventListener playerStoreRoomListValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (!dataSnapshot.exists()) {
                return;
            }

//            dataSnapshot.getRef().orderByChild("date");

            GenericTypeIndicator<HashMap<String, WaitPlayerRoomDTO>> typeIndicator1 = new GenericTypeIndicator<HashMap<String, WaitPlayerRoomDTO>>() {
            };
            HashMap<String, WaitPlayerRoomDTO> waitPlayerRoomDTOHashMap = dataSnapshot.getValue(typeIndicator1);

            for (Map.Entry entry : waitPlayerRoomDTOHashMap.entrySet()) {
                WaitPlayerRoomDTO roomDTO = (WaitPlayerRoomDTO) entry.getValue();
                String key = (String) entry.getKey();

                if (null != keySet && !keySet.contains(key)) {
                    keySet.add(key);
                    waitPlayerRoomDTOList.add(roomDTO);
                }

            }

            if (waitPlayerRoomDTOList != null) {
                showEmptyMessage(waitPlayerRoomDTOList.isEmpty());
                sortListByDate(waitPlayerRoomDTOList);
            }

            adapter.setDataList(waitPlayerRoomDTOList);
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_store_room_list);
        queryStoreId = getIntent().getStringExtra(KEY_PLAYERROOMLIST_STORE_ID);
        initActionBar();
        initView();

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (keySet == null) {
            keySet = new LinkedHashSet<>();
        }

        if (queryStoreId != null && !queryStoreId.equals("")) {

            FireBaseUrl storeRoomListUrl = new FireBaseUrl.Builder()
                    .addUrlNote(FirebaseTableKey.TABLE_WAITPLYERROOM)
                    .addUrlNote(queryStoreId)
                    .build();

            playerRoomList = firebaseDatabase.getReferenceFromUrl(storeRoomListUrl.getUrl());
            playerRoomList.addValueEventListener(playerStoreRoomListValueEventListener);

            tvEmptyMessage.setText(getString(R.string.player_room_empty_message_by_store));

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (playerRoomList != null) {
            playerRoomList.removeEventListener(playerStoreRoomListValueEventListener);
            playerRoomList = null;
        }

        keySet.clear();
        keySet = null;

        waitPlayerRoomDTOList.clear();
        waitPlayerRoomDTOList = null;
    }

    private void initView() {
        rvPlayerStoreRoomList = (RecyclerView) findViewById(R.id.rv_player_single_store_room_list);
        rvPlayerStoreRoomList.setLayoutManager(new LinearLayoutManager(this));
        rvPlayerStoreRoomList.addItemDecoration(new LinearItemDecoration(this));

        if (adapter == null) {
            adapter = new PlayerRoomListAdapter(this);
            adapter.setAdapterListener(adapterListener);
        }
        rvPlayerStoreRoomList.setAdapter(adapter);

        tvEmptyMessage = (TextView) findViewById(R.id.tv_player_single_store_room_empty_msg);

    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.player_room_list_title);
        } catch (NullPointerException e) {

        }
    }


    private void showEmptyMessage(boolean isEmpty) {
        tvEmptyMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void switchDetailFragment(WaitPlayerRoomDTO roomDTO) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("player_room_detail")
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.rl_player_store_room_list_root, PlayerRoomDetailFragment.newInstance(roomDTO))
                .commit();

    }

    private PlayerRoomListAdapter.AdapterListener adapterListener = new PlayerRoomListAdapter.AdapterListener() {
        @Override
        public void onClickItem(View view, WaitPlayerRoomDTO waitPlayerRoomDTO) {
            switchDetailFragment(waitPlayerRoomDTO);
        }
    };

    private void sortListByDate(List<WaitPlayerRoomDTO> list) {
        Collections.sort(list, new Comparator<WaitPlayerRoomDTO>() {
            @Override
            public int compare(WaitPlayerRoomDTO t0, WaitPlayerRoomDTO t1) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                try {
                    Date date0 = sdf.parse(t0.getTimeStamp());
                    Date date1 = sdf.parse(t1.getTimeStamp());
                    return date1.compareTo(date0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        });
    }

}
