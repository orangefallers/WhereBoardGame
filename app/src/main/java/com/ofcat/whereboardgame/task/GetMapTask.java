package com.ofcat.whereboardgame.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by orangefaller on 2017/1/14.
 */

public class GetMapTask extends AsyncTask<Object, Object, String> {

    private String url;

    private onMapTaskListener listener;

    public GetMapTask(String url, onMapTaskListener listener) {

        this.url = url;

        this.listener = listener;

        // https://docs.google.com/spreadsheets/d/128wH1tdLJHe9D-EyPoyVcALt4I8s43DVuG5VulL9_cU/pubhtml?gid=0&single=true
    }

    @Override
    protected String doInBackground(Object[] objects) {

        HttpsURLConnection connection = null;
        String response = "";
        InputStream inputStream;

        try {
            URL url = new URL(this.url);

            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(1500);
            connection.setConnectTimeout(1500);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestMethod("GET");

            int statusCode = connection.getResponseCode();

            if (statusCode == 200) {
//                inputStream = new BufferedInputStream(connection.getInputStream());
//                String response = HttpUtil.convertInputStreamToString(inputStream);


                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
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
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.i("Result", "json = " + response);

        if (this.listener !=null){
            listener.onSuccess(response);
        }
    }

    public interface onMapTaskListener {
        void onSuccess(String response);

        void onFail();
    }

}
