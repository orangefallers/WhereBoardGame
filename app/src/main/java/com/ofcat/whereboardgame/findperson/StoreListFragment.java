package com.ofcat.whereboardgame.findperson;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.dataobj.EntryDTO;
import com.ofcat.whereboardgame.dataobj.StoreInfoDTO;
import com.ofcat.whereboardgame.model.GetBoardGameStoreDataImpl;
import com.ofcat.whereboardgame.util.MyLog;

import java.util.ArrayList;


/**
 * Created by orangefaller on 2017/9/13.
 */

public class StoreListFragment extends Fragment {

    private final String TAG = StoreListFragment.class.getSimpleName();

    private static final String KEY_BUNDLE_API_INDEX = "key_api_index";

    private int apiIndex;

    private GetBoardGameStoreDataImpl getBoardGameStoreData;

    private TextView tvMessage;
    private RecyclerView rvStoreList;

    private StoreListRecyclerAdapter adapter;

    private GetBoardGameStoreDataImpl.StoreDataImplListener storeDataImplListener = new GetBoardGameStoreDataImpl.StoreDataImplListener() {
        @Override
        public void onStoreDataList(ArrayList<EntryDTO> entryDTOs) {

        }

        @Override
        public void onSimpleStoreDataList(ArrayList<StoreInfoDTO> storeInfoDTOs) {
            showLoading(false);
            if (adapter != null) {
                //第一筆資料為欄位名稱，故移除
                storeInfoDTOs.remove(0);

                adapter.setStoreData(storeInfoDTOs);
                rvStoreList.setAdapter(adapter);
            } else {
                showError("資料有錯", true);
            }
        }

        @Override
        public void onUpdateData(String date) {

        }

        @Override
        public void onFail(String errorMessage) {
            if (tvMessage != null) {
                showLoading(false);

                String modifyError = errorMessage+"\n請嘗試離開頁面重新讀取";
                showError(modifyError, true);
            }
        }
    };

    private StoreListRecyclerAdapter.AdapterListener adapterListener = new StoreListRecyclerAdapter.AdapterListener() {
        @Override
        public void onClickItem(View view, StoreInfoDTO storeInfoDTO) {

            String addressTag = storeInfoDTO.getAddress().substring(0, 2);
            double lat = Double.parseDouble(storeInfoDTO.getLatitude());
            double lng = Double.parseDouble(storeInfoDTO.getLongitude());

            Intent intent = new Intent(getActivity(), FindPersonActivity.class);
            intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_PLACE, storeInfoDTO.getStoreName());
            intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_ID, storeInfoDTO.getStoreId());
            intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_PLACE_TAG, addressTag);
            intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_LAT, lat);
            intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_LNG, lng);
            intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_ADDRESS, storeInfoDTO.getAddress());
            startActivity(intent);
        }
    };

    public static StoreListFragment newInstance(int apiIndex) {
        StoreListFragment fragment = new StoreListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_BUNDLE_API_INDEX, apiIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiIndex = getArguments().getInt(KEY_BUNDLE_API_INDEX, -1);

        if (getBoardGameStoreData == null) {
            getBoardGameStoreData = new GetBoardGameStoreDataImpl(getActivity(), storeDataImplListener);
        }

        if (adapter == null) {
            adapter = new StoreListRecyclerAdapter(getActivity());
            adapter.setAdapterListener(adapterListener);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_list, container, false);
        initView(view);
//        tvMessage.setText("this is " + apiIndex + "page");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoading(true);
        getBoardGameStoreData.startLoadData(apiIndex);
    }

    private void initView(View view) {
        tvMessage = (TextView) view.findViewById(R.id.tv_store_list_message);
        rvStoreList = (RecyclerView) view.findViewById(R.id.rv_store_list);
        rvStoreList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvStoreList.addItemDecoration(new StoreListItemDecoration());
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(getResources().getString(R.string.loading));
        } else {
            tvMessage.setVisibility(View.GONE);
            tvMessage.setText("");
        }
    }

    private void showError(String errorMsg, boolean isShow) {
        tvMessage.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (isShow) {
            SpannableString spannableString = new SpannableString(errorMsg);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, errorMsg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvMessage.setText(spannableString);
        } else {
            tvMessage.setText(errorMsg);
        }

    }
}
