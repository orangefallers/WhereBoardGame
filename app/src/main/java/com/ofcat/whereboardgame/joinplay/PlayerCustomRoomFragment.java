package com.ofcat.whereboardgame.joinplay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
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
 * Created by orangefaller on 2017/9/9.
 */

public class PlayerCustomRoomFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference customRoomAllList;

    private RecyclerView rvPlayerRoomList;
    private PlayerRoomListAdapter adapter;

    private TextView tvEmptyMessage;

    private Set<String> keySet = new LinkedHashSet<>();
    private List<WaitPlayerRoomDTO> waitPlayerRoomDTOList = new ArrayList<>();


    private ValueEventListener customRoomListValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

//
//            GenericTypeIndicator<HashMap<String, WaitPlayerRoomDTO>> typeIndicator1 = new GenericTypeIndicator<HashMap<String, WaitPlayerRoomDTO>>() {
//            };
//            HashMap<String, WaitPlayerRoomDTO> waitPlayerRoomDTOHashMap = dataSnapshot.getValue(typeIndicator1);

            for (DataSnapshot child : dataSnapshot.getChildren()) {
                WaitPlayerRoomDTO roomDTO = child.getValue(WaitPlayerRoomDTO.class);
                if (waitPlayerRoomDTOList != null) {
                    waitPlayerRoomDTOList.add(roomDTO);
                }
            }

//            for (Map.Entry entry : waitPlayerRoomDTOHashMap.entrySet()) {
//                WaitPlayerRoomDTO roomDTO = (WaitPlayerRoomDTO) entry.getValue();
//                if (waitPlayerRoomDTOList != null) {
//                    waitPlayerRoomDTOList.add(roomDTO);
//                }
//            }


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

    public static PlayerCustomRoomFragment newInstance() {
        PlayerCustomRoomFragment fragment = new PlayerCustomRoomFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_room_list, container, false);
        initView(view);

        if (keySet == null) {
            keySet = new LinkedHashSet<>();
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FireBaseUrl customRoomAllListUrl = new FireBaseUrl.Builder()
                .addUrlNote(FirebaseTableKey.TABLE_CUSTOM_WAITPLAYERROOM)
                .build();
        customRoomAllList = firebaseDatabase.getReferenceFromUrl(customRoomAllListUrl.getUrl());
        customRoomAllList.addValueEventListener(customRoomListValueEventListener);
    }

    private void initView(View view) {

        rvPlayerRoomList = (RecyclerView) view.findViewById(R.id.rv_player_custom_room_list);
        rvPlayerRoomList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPlayerRoomList.addItemDecoration(new LinearItemDecoration(getActivity()));

        if (adapter == null) {
            adapter = new PlayerRoomListAdapter(getActivity());
            adapter.setAdapterListener(adapterListener);
        }
        rvPlayerRoomList.setAdapter(adapter);

        tvEmptyMessage = (TextView) view.findViewById(R.id.tv_player_custom_room_empty_msg);
    }

    private void showEmptyMessage(boolean isEmpty) {
        tvEmptyMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void switchDetailFragment(WaitPlayerRoomDTO roomDTO) {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("player_room_detail")
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.rl_player_room_list_root, PlayerRoomDetailFragment.newInstance(roomDTO))
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
