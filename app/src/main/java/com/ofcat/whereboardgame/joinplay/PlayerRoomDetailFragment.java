package com.ofcat.whereboardgame.joinplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
import com.ofcat.whereboardgame.map.SingleStoreMapActivity;

/**
 * Created by orangefaller on 2017/6/24.
 */

public class PlayerRoomDetailFragment extends Fragment {

    private static final String KEY_BUNDLE_ROOMDTO = "key_bundle_roomdto";

    private WaitPlayerRoomDTO currentRoomDTO;

    private TextView tvInitiator;
    private TextView tvStore;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvContact;
    private TextView tvOtherContent;


    private String initiator = "主揪稱呼：%s";
    private String store = "揪團地點：%s";
    private String date = "日期：%s";
    private String time = "時間：%s";
    private String contact = "聯絡方式：%s";

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_room_detail_store:
                    double roomLat = currentRoomDTO.getLocation().getLatitude();
                    double roomLng = currentRoomDTO.getLocation().getLongitude();
                    String roomStoreName = currentRoomDTO.getStoreName();
                    String roomStoreAddress = currentRoomDTO.getStoreAddress();

                    Intent intent = new Intent(getActivity(), SingleStoreMapActivity.class);
                    intent.putExtra(SingleStoreMapActivity.KEY_SINGLE_MAP_LAT, roomLat);
                    intent.putExtra(SingleStoreMapActivity.KEY_SINGLE_MAP_LNG, roomLng);
                    intent.putExtra(SingleStoreMapActivity.KEY_SINGLE_MAP_STORE_NAME, roomStoreName);
                    intent.putExtra(SingleStoreMapActivity.KEY_SINGLE_MAP_STORE_ADDRESS, roomStoreAddress);
                    startActivity(intent);
                    break;
            }

        }
    };

    public static PlayerRoomDetailFragment newInstance(WaitPlayerRoomDTO roomDTO) {
        PlayerRoomDetailFragment fragment = new PlayerRoomDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_BUNDLE_ROOMDTO, roomDTO);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRoomDTO = (WaitPlayerRoomDTO) getArguments().getSerializable(KEY_BUNDLE_ROOMDTO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_room_detail, container, false);
        initView(view);
        setPlayerRoomDTO(currentRoomDTO);
        return view;
    }

    private void initView(View view) {
        tvInitiator = (TextView) view.findViewById(R.id.tv_room_detail_initiator);
        tvStore = (TextView) view.findViewById(R.id.tv_room_detail_store);
        tvDate = (TextView) view.findViewById(R.id.tv_room_detail_date);
        tvTime = (TextView) view.findViewById(R.id.tv_room_detail_time);
        tvContact = (TextView) view.findViewById(R.id.tv_room_detail_contact);
        tvOtherContent = (TextView) view.findViewById(R.id.tv_room_detail_other_content);

        tvStore.setOnClickListener(clickListener);

    }

    private void setPlayerRoomDTO(WaitPlayerRoomDTO roomDTO) {
        tvInitiator.setText(String.format(initiator, roomDTO.getInitiator()));
        tvStore.setText(String.format(store, roomDTO.getStoreName()));
        tvDate.setText(String.format(date, roomDTO.getDate()));
        tvTime.setText(String.format(time, roomDTO.getTime()));
        tvContact.setText(String.format(contact, roomDTO.getContact()));
        tvOtherContent.setText(roomDTO.getContent());
    }
}
