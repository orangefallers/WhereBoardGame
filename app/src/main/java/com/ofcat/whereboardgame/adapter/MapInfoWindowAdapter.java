package com.ofcat.whereboardgame.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.ofcat.whereboardgame.R;

/**
 * Created by orangefaller on 2017/1/15.
 */

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity act;

    public MapInfoWindowAdapter(Activity activity) {
        this.act = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = act.getLayoutInflater().inflate(R.layout.custom_marker, null);

        TextView tvStoreName = (TextView) view.findViewById(R.id.marker_store_name);
        TextView tvAddress = (TextView) view.findViewById(R.id.marker_store_address);

        tvStoreName.setText(marker.getTitle());

        String address = marker.getSnippet();
        if (null == address || address.equals("")) {
            tvAddress.setVisibility(View.GONE);
        } else {
            tvAddress.setText(marker.getSnippet());
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
