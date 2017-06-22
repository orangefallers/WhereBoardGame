package com.ofcat.whereboardgame.joinplay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by orangefaller on 2017/6/21.
 */

public class PlayerRoomListActivity extends AppCompatActivity {

    private FireBaseModelApiImpl fireBaseModelApi;

    private RecyclerView rvPlayerRoomList;
    private PlayerRoomListAdapter adapter;

    private Set<String> keySet = new LinkedHashSet<>();
    private List<WaitPlayerRoomDTO> waitPlayerRoomDTOList = new ArrayList<>();

    private ValueEventListener playerRoomListValueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Log.i("kevintest", "data = " + dataSnapshot.toString());

            for (DataSnapshot child : dataSnapshot.getChildren()) {

                GenericTypeIndicator<HashMap<String, WaitPlayerRoomDTO>> typeIndicator1 = new GenericTypeIndicator<HashMap<String, WaitPlayerRoomDTO>>() {
                };
                HashMap<String, WaitPlayerRoomDTO> waitPlayerRoomDTOHashMap = child.getValue(typeIndicator1);
                Log.i("kevintest", "map size = " + waitPlayerRoomDTOHashMap.size());

                for (Map.Entry entry : waitPlayerRoomDTOHashMap.entrySet()) {
                    WaitPlayerRoomDTO roomDTO = (WaitPlayerRoomDTO) entry.getValue();
                    String key = (String) entry.getKey();

                    if (null != keySet && !keySet.contains(key)) {
                        keySet.add(key);
                        waitPlayerRoomDTOList.add(roomDTO);
                    }
                    Log.i("kevintest", "room map key = " + entry.getKey() + " value = " + roomDTO.getStoreName());

                }

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
        setContentView(R.layout.activity_player_room_list);

        initActionBar();
        initView();

        if (keySet == null) {
            keySet = new LinkedHashSet<>();
        }

        if (fireBaseModelApi == null) {
            fireBaseModelApi = new FireBaseModelApiImpl().addApiNote("WaitPlayerRoom");
            fireBaseModelApi.execute();
            fireBaseModelApi.addValueEventListener(playerRoomListValueEventListener);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fireBaseModelApi != null) {
            fireBaseModelApi.getDefaultDatabaseRef().removeEventListener(playerRoomListValueEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (fireBaseModelApi != null) {
//            fireBaseModelApi.getDefaultDatabaseRef().removeEventListener(playerRoomListValueEventListener);
//        }

        keySet.clear();
        keySet = null;

        waitPlayerRoomDTOList.clear();
        waitPlayerRoomDTOList = null;
    }

    private void initView() {
        rvPlayerRoomList = (RecyclerView) findViewById(R.id.rv_player_room_list);
        rvPlayerRoomList.setLayoutManager(new LinearLayoutManager(this));
        rvPlayerRoomList.addItemDecoration(new LinearItemDecoration(this));

        if (adapter == null) {
            adapter = new PlayerRoomListAdapter();
        }
        rvPlayerRoomList.setAdapter(adapter);


    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.player_room_list_title);
        } catch (NullPointerException e) {

        }
    }

}
