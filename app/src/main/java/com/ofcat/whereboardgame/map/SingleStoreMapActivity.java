package com.ofcat.whereboardgame.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.adapter.MapInfoWindowAdapter;

/**
 * Created by orangefaller on 2017/8/27.
 */

public class SingleStoreMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public final static String KEY_SINGLE_MAP_LAT = "key_single_map_lat";
    public final static String KEY_SINGLE_MAP_LNG = "key_single_map_lng";
    public final static String KEY_SINGLE_MAP_STORE_NAME = "key_single_map_store_name";
    public final static String KEY_SINGLE_MAP_STORE_ADDRESS = "key_single_map_store_address";

    private GoogleMap singleStoreMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private String storeName, storeAddress;
    private double storeLat, storeLng;
    private LatLng storeLatLng;

    private Marker myLocationMarker;
    private Marker storeMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_store_map);
        initView();

        storeLat = getIntent().getDoubleExtra(KEY_SINGLE_MAP_LAT, 0);
        storeLng = getIntent().getDoubleExtra(KEY_SINGLE_MAP_LNG, 0);
        storeName = getIntent().getStringExtra(KEY_SINGLE_MAP_STORE_NAME);
        storeAddress = getIntent().getStringExtra(KEY_SINGLE_MAP_STORE_ADDRESS);

        connectLocationService();

        configLocationRequest();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapSingleStoreFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_single_store);
        mapSingleStoreFragment.getMapAsync(this);

    }


    private void initView() {

    }

    private void connectLocationService() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setInterval(1000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //// TODO: 2017/8/27  android 6.0 permission
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, SingleStoreMapActivity.this);
        } catch (SecurityException e) {
//            Log.w(TAG, "Exception!!");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, R.string.google_play_service_miss,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLan = new LatLng(location.getLatitude(), location.getLongitude());

        if (myLocationMarker == null) {
            myLocationMarker = singleStoreMap.addMarker(new MarkerOptions()
                    .position(latLan)
                    .title(getString(R.string.my_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
//            myLocationMarker.showInfoWindow();
//            myLocationMarker .setTag(null);
        } else {
            myLocationMarker.setPosition(latLan);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        singleStoreMap = googleMap;
        singleStoreMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        singleStoreMap.setInfoWindowAdapter(new MapInfoWindowAdapter(this));

        if (storeLatLng == null) {
            storeLatLng = new LatLng(storeLat, storeLng);
        }

        if (storeMarker == null) {
            storeMarker = singleStoreMap.addMarker(new MarkerOptions()
                    .position(storeLatLng)
                    .title(storeName)
                    .snippet(storeAddress)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            storeMarker.showInfoWindow();
        }

        moveMap(storeLatLng, 15);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        singleStoreMap.setMyLocationEnabled(true);
    }

    private void moveMap(LatLng place, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place)
                .zoom(zoom)
                .build();

//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        singleStoreMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
