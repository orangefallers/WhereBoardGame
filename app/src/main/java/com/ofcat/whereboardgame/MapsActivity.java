package com.ofcat.whereboardgame;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.ofcat.whereboardgame.adapter.MapInfoWindowAdapter;
import com.ofcat.whereboardgame.dataobj.EntryDTO;
import com.ofcat.whereboardgame.dataobj.StoreInfoDTO;
import com.ofcat.whereboardgame.model.GetBoardGameStoreDataImpl;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Location currentLocation;

    private Marker currentMarker;

    private GetBoardGameStoreDataImpl boardGameStoreData;


//    private ArrayList<MarkerOptions> markerOptionses;

    private GetBoardGameStoreDataImpl.StoreDataImplListener dataImplListener = new GetBoardGameStoreDataImpl.StoreDataImplListener() {
        @Override
        public void onStoreDataList(ArrayList<EntryDTO> entryDTOs) {
//            if (markerOptionses == null) {
//                markerOptionses = new ArrayList<>();
//            }
//
//            markerOptionses.clear();

//            int i = 0;
//
//            for (EntryDTO entryDTO : entryDTOs) {
//
//                if (i > 0) {
//                    double lat = Double.parseDouble(entryDTO.getLatitude().getToString());
//                    double lng = Double.parseDouble(entryDTO.getLongitude().getToString());
//
//                    LatLng storeLatLng = new LatLng(lat, lng);
//
//                    MarkerOptions options = new MarkerOptions()
//                            .title(entryDTO.getStoreName().getToString())
//                            .snippet(entryDTO.getAddress().getToString())
//                            .position(storeLatLng);
//
//                    mMap.addMarker(options);
//                }
//
//                i++;
//            }


        }

        @Override
        public void onSimpleStoreDataList(ArrayList<StoreInfoDTO> storeInfoDTOs) {

            int i = 0;

            for (StoreInfoDTO storeInfoDTO : storeInfoDTOs) {

                if (i > 0) {
                    double lat = Double.parseDouble(storeInfoDTO.getLatitude());
                    double lng = Double.parseDouble(storeInfoDTO.getLongitude());

                    LatLng storeLatLng = new LatLng(lat, lng);

                    MarkerOptions options = new MarkerOptions()
                            .title(storeInfoDTO.getStoreName())
                            .snippet(storeInfoDTO.getAddress())
                            .position(storeLatLng);

                    mMap.addMarker(options);
                }

                i++;
            }
        }

        @Override
        public void onUpdateData(String date) {
//            Log.i("update", "date = " + date);
//            TimeZone.setDefault(TimeZone.getTimeZone("GTM+8"));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        connectLocationService();

        configLocationRequest();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (boardGameStoreData == null) {
            boardGameStoreData = new GetBoardGameStoreDataImpl(this, dataImplListener);
        }


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
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (boardGameStoreData != null) {
            boardGameStoreData.onDestroy();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(this));

        if (boardGameStoreData != null) {
            boardGameStoreData.startLoadData();
        }


//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                if (marker.equals(currentMarker)){
//
//                    Toast.makeText(MapsActivity.this, "This is my home",Toast.LENGTH_LONG).show();
//                    return true;
//                }
//                return false;
//            }
//        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

//                Toast.makeText(MapsActivity.this, "This is " + marker.getTitle(), Toast.LENGTH_LONG).show();
            }
        });


        // Add a marker in Sydney and move the camera
//        24.980980,121.546884


//        LatLng sydney = new LatLng(-34, 151);

//        LatLng sydney = new LatLng(24.974979, 121.543102);
//        moveMap(sydney);

//        mMap.addMarker(new MarkerOptions().position(sydney).title("Seven MRT Station"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void moveMap(LatLng place) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place)
                .zoom(15)
                .build();

//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //// TODO: 2017/1/15  android 6.0 permission  
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MapsActivity.this);
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
        currentLocation = location;
//        Log.i(TAG, "OnLocationChanged");

        LatLng latLan = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLan)
                    .title(getString(R.string.my_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            currentMarker.showInfoWindow();
            moveMap(latLan);
        } else {
            currentMarker.setPosition(latLan);
        }
//        currentMarker.showInfoWindow();


    }
}
