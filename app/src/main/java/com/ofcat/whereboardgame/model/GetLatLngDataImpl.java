package com.ofcat.whereboardgame.model;

import android.util.Log;

import com.ofcat.whereboardgame.config.AppConfig;
import com.ofcat.whereboardgame.task.GetHttpConnectionTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Created by kevin_hsieh on 2017/8/29.
 */

public class GetLatLngDataImpl {

    private final String API_KEY = AppConfig.GOOGLE_ADDRESS_API_KEY;

    private final String addressUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private final String LANGUAGE = "zh_TW";

    private String inputAddress;
    private double resultLat = 0.0, resultLng = 0.0;


    private GetHttpConnectionTask getTask;

    private LatLngDataImplListener latLngDataImplListener;

    private GetHttpConnectionTask.onMapTaskListener listener = new GetHttpConnectionTask.onMapTaskListener() {
        @Override
        public void onSuccess(String response) {
            Log.i("address", " response = " + response);
            JSONObject result;
            try {
                result = new JSONObject(response);

                if (!result.isNull("status")) {
                    String status = result.getString("status");

                    if (!status.equals("OK") && latLngDataImplListener != null) {
                        latLngDataImplListener.onFail(status);
                    } else {

                        if (!result.isNull("results")) {
//                            JSONObject addressResult = result.getJSONObject("results");
                            JSONArray addressResults = result.getJSONArray("results");

                            if (addressResults != null && addressResults.length() > 0) {

                                JSONObject addressResult = addressResults.getJSONObject(0);

                                if (!addressResult.isNull("geometry")) {
                                    JSONObject geometry = addressResult.getJSONObject("geometry");

                                    if (!geometry.isNull("location")) {
                                        JSONObject location = geometry.getJSONObject("location");

                                        if (!location.isNull("lat")) {
                                            resultLat = location.getDouble("lat");
                                        }

                                        if (!location.isNull("lng")) {
                                            resultLng = location.getDouble("lng");
                                        }

                                        if (latLngDataImplListener != null) {
                                            latLngDataImplListener.onResult(inputAddress, resultLat, resultLng);
                                        }
                                    }
                                }
                            }
                        }
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

    public GetLatLngDataImpl(LatLngDataImplListener listener) {
        this.latLngDataImplListener = listener;

    }

    public void getLatLngByAddress(String address) {

        inputAddress = address;
        try {
            String encodeAddress = URLEncoder.encode(inputAddress, "UTF-8");
            String url = addressUrl + encodeAddress + "&language=" + LANGUAGE + "&key=" + API_KEY;
//            if (getTask == null) {
            getTask = new GetHttpConnectionTask(url, listener);
            getTask.execute();
//            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (latLngDataImplListener != null) {
                latLngDataImplListener.onFail(e.getMessage());
            }
        }
    }


    public void onDestroy() {
        latLngDataImplListener = null;
        getTask = null;
    }


    public interface LatLngDataImplListener {
        void onResult(String address, double latitude, double longitude);

        void onFail(String status);
    }
}
