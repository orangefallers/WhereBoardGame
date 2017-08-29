package com.ofcat.whereboardgame.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kevin_hsieh on 2017/8/29.
 */

public class GetLatLngFromAddressTask extends AsyncTask<Object, Object, String> {

    private onLatLngTaskListener listener;

    private String url;

    public GetLatLngFromAddressTask(String url, onLatLngTaskListener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Object... params) {

        HttpsURLConnection connection = null;
        String response = "";

        try {
            URL url = new URL(this.url);

            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(1500);
            connection.setConnectTimeout(1500);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestMethod("GET");

            int statusCode = connection.getResponseCode();

            if (statusCode == 200) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                response = sb.toString();
                return response;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onSuccess(result);
        }
    }

    public interface onLatLngTaskListener {
        void onSuccess(String response);

        void onFail();
    }
}
