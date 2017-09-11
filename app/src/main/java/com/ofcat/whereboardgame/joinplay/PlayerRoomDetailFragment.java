package com.ofcat.whereboardgame.joinplay;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.dataobj.LocationDTO;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
import com.ofcat.whereboardgame.map.SingleStoreMapActivity;
import com.ofcat.whereboardgame.util.MyLog;

import java.util.Locale;

/**
 * Created by orangefaller on 2017/6/24.
 */

public class PlayerRoomDetailFragment extends Fragment {

    private final String TAG = PlayerRoomDetailFragment.class.getSimpleName();
    private static final String KEY_BUNDLE_ROOMDTO = "key_bundle_roomdto";

    private WaitPlayerRoomDTO currentRoomDTO;

    private TextView tvInitiator;
    private TextView tvStore;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvContact;
    private TextView tvOtherContent;

    private LinearLayout llCurrentPerson;
    private LinearLayout llNeedPerson;
    private TextView tvCurrentPerson;
    private TextView tvNeedPerson;


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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocationDTO locationDTO = currentRoomDTO.getLocation();
        showLocationIcon(locationDTO != null);
        showCurrentPerson(currentRoomDTO.getCurrentPerson());
        showNeedPerson(currentRoomDTO.getNeedPerson());

    }

    private void initView(View view) {
        tvInitiator = (TextView) view.findViewById(R.id.tv_room_detail_initiator);
        tvStore = (TextView) view.findViewById(R.id.tv_room_detail_store);
        tvDate = (TextView) view.findViewById(R.id.tv_room_detail_date);
        tvTime = (TextView) view.findViewById(R.id.tv_room_detail_time);
        tvContact = (TextView) view.findViewById(R.id.tv_room_detail_contact);
        tvOtherContent = (TextView) view.findViewById(R.id.tv_room_detail_other_content);

        llCurrentPerson = (LinearLayout) view.findViewById(R.id.ll_current_person_area);
        llNeedPerson = (LinearLayout) view.findViewById(R.id.ll_need_person_area);
        tvCurrentPerson = (TextView) view.findViewById(R.id.tv_room_detail_current_person);
        tvNeedPerson = (TextView) view.findViewById(R.id.tv_room_detail_need_person);

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

    private void showLocationIcon(boolean isShow) {
        if (isShow) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_location_on_black_24dp);
            tvStore.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            tvStore.setEnabled(true);
        } else {
            tvStore.setCompoundDrawables(null, null, null, null);
            tvStore.setEnabled(false);
        }

    }

    private void showCurrentPerson(int currentPerson) {
        if (currentPerson > 0 && currentPerson < 21) {
            llCurrentPerson.setVisibility(View.VISIBLE);
            tvCurrentPerson.setText(String.format(Locale.getDefault(), "目前人數：%d人", currentPerson));
        } else if (currentPerson >= 21) {
            llCurrentPerson.setVisibility(View.VISIBLE);
            tvCurrentPerson.setText(String.format(Locale.getDefault(), "目前人數：%d人以上", currentPerson));
        } else {
            llCurrentPerson.setVisibility(View.GONE);
        }

    }

    private void showNeedPerson(int needPerson) {
        if (needPerson > 0 && needPerson < 21) {
            llNeedPerson.setVisibility(View.VISIBLE);
            tvNeedPerson.setText(String.format(Locale.getDefault(), "徵求人數：%d人", needPerson));
        } else if (needPerson >= 21) {
            llNeedPerson.setVisibility(View.VISIBLE);
            tvNeedPerson.setText(String.format(Locale.getDefault(), "徵求人數：%d人以上", needPerson));
        } else {
            llNeedPerson.setVisibility(View.GONE);
        }

    }

}
