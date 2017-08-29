package com.ofcat.whereboardgame.model;

import com.ofcat.whereboardgame.task.GetMapTask;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by kevin_hsieh on 2017/8/29.
 */

public class GetLatLngDataImpl {

    private GetMapTask getTask;

    private LatLngDataImplListener latLngDataImplListener;

    private GetMapTask.onMapTaskListener listener = new GetMapTask.onMapTaskListener() {
        @Override
        public void onSuccess(String response) {
            JSONObject result;
            try {
                result = new JSONObject(response);

                if (!result.isNull("status")) {
                    String status = result.getString("status");

                    if (!status.equals("OK") && latLngDataImplListener != null) {
                        latLngDataImplListener.onFail(status);
                    } else {

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFail() {

        }
    };

    public GetLatLngDataImpl() {

    }

    public void getLatLngByAddress(String address) {

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=API_KEY";

        if (getTask == null) {
            getTask = new GetMapTask(url, listener);
            getTask.execute();
        }
    }


    public interface LatLngDataImplListener {
        void onResult(String address, double latitude, double longitude);

        void onFail(String status);
    }
}
