package com.ofcat.whereboardgame.model;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ofcat.whereboardgame.dataobj.EntryDTO;
import com.ofcat.whereboardgame.dataobj.FeedDTO;
import com.ofcat.whereboardgame.dataobj.StoreInfoDTO;
import com.ofcat.whereboardgame.task.GetMapTask;
import com.ofcat.whereboardgame.util.SharedPreferenceKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by orangefaller on 2017/1/14.
 */

public class GetBoardGameStoreDataImpl {

    private final String storeUrlByCells = "https://spreadsheets.google.com/feeds/cells/128wH1tdLJHe9D-EyPoyVcALt4I8s43DVuG5VulL9_cU/1/public/values?alt=json";
    private final String storeUrlByList = "https://spreadsheets.google.com/feeds/list/128wH1tdLJHe9D-EyPoyVcALt4I8s43DVuG5VulL9_cU/1/public/values?alt=json";

    private Context context;
    private SharedPreferences sharedPreferences;

    private ArrayList<StoreInfoDTO> storeInfoDTOs;
    private String dateNewFormat;

    private GetMapTask getMapTask;

    private StoreDataImplListener storeDataImplListener;


    private GetMapTask.onMapTaskListener taskListener = new GetMapTask.onMapTaskListener() {
        @Override
        public void onSuccess(String response) {
            JSONObject result;

            try {
                result = new JSONObject(response);
                JSONObject feed = result.getJSONObject("feed");

                Gson gson = new Gson();

                FeedDTO feedDTO = gson.fromJson(feed.toString(), FeedDTO.class);

                if (feedDTO != null && storeDataImplListener != null) {
                    storeDataImplListener.onStoreDataList(feedDTO.getEntryDTOs());
                    storeDataImplListener.onSimpleStoreDataList(simplifyStoreData(feedDTO.getEntryDTOs()));
                    storeDataImplListener.onUpdateData(feedDTO.getUpdated().getToString());
                    dateNewFormat = changeDateFormat(feedDTO.getUpdated().getToString());
                    saveDate(dateNewFormat);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onFail() {

        }
    };


    public GetBoardGameStoreDataImpl(Context context, StoreDataImplListener listener) {
        this.context = context;
        this.storeDataImplListener = listener;
        initSharedPreference();
    }

    public void startLoadData() {
        if (getMapTask == null) {
            getMapTask = new GetMapTask(storeUrlByList, taskListener);
            getMapTask.execute();
        }

    }

    public String getDataUpdateDate() {
        if (sharedPreferences != null) {
           return sharedPreferences.getString(SharedPreferenceKey.DATA_UPDATE_DATE,"");
        }

        return "";
    }

    public void onDestroy() {
        storeDataImplListener = null;
    }

    private ArrayList<StoreInfoDTO> simplifyStoreData(ArrayList<EntryDTO> entryDTOArrayList) {

        if (storeInfoDTOs == null) {
            storeInfoDTOs = new ArrayList<>();
        }
        storeInfoDTOs.clear();

        for (EntryDTO entryDTO : entryDTOArrayList) {
            StoreInfoDTO storeInfoDTO = new StoreInfoDTO();
            storeInfoDTO.setStoreName(entryDTO.getStoreName().getToString());
            storeInfoDTO.setAddress(entryDTO.getAddress().getToString());
            storeInfoDTO.setLatitude(entryDTO.getLatitude().getToString());
            storeInfoDTO.setLongitude(entryDTO.getLongitude().getToString());
            storeInfoDTOs.add(storeInfoDTO);

        }

        return storeInfoDTOs;
    }

    private void initSharedPreference() {
        sharedPreferences = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
    }

    private void saveDate(String date) {
        if (sharedPreferences != null) {
            sharedPreferences.edit()
                    .putString(SharedPreferenceKey.DATA_UPDATE_DATE, date)
                    .apply();
        }
    }

    private String changeDateFormat(String oldDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM+8"));
        SimpleDateFormat sdf_tw = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {

            Date updateDate = sdf.parse(oldDate);
            String newDate = sdf_tw.format(updateDate);
            return newDate;

        } catch (ParseException e) {
            e.printStackTrace();
            return oldDate;
        }
    }

    public interface StoreDataImplListener {

        void onStoreDataList(ArrayList<EntryDTO> entryDTOs);

        void onSimpleStoreDataList(ArrayList<StoreInfoDTO> storeInfoDTOs);

        void onUpdateData(String date);

    }
}
