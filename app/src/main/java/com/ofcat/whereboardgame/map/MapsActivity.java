package com.ofcat.whereboardgame.map;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.adapter.MapInfoWindowAdapter;
import com.ofcat.whereboardgame.dataobj.EntryDTO;
import com.ofcat.whereboardgame.dataobj.StoreInfoDTO;
import com.ofcat.whereboardgame.findperson.FindPersonActivity;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;
import com.ofcat.whereboardgame.joinplay.PlayerRoomListActivity;
import com.ofcat.whereboardgame.joinplay.PlayerStoreRoomListActivity;
import com.ofcat.whereboardgame.login.UserLoginActivity;
import com.ofcat.whereboardgame.model.GetBoardGameStoreDataImpl;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = MapsActivity.class.getSimpleName();

    private final double defaultLat = 24.190254;
    private final double defaultLng = 120.811551;

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private FireBaseModelApiImpl fireBaseModelApi;
    private FirebaseAuth auth;

    private Location currentLocation;
    private LatLng defaultLatLan = new LatLng(defaultLat, defaultLng);

    private Marker currentMarker;

    private GetBoardGameStoreDataImpl boardGameStoreData;

    private LinearLayout llChooseLayout;

    private TextView tvFindPerson;
    private TextView tvWhoPlay;

    // 顯示列表的動畫，在第一次執行時初始化相關參數
    private AnimatorSet showAnimationSet = null;
    // 隱藏列表的動畫
    private AnimatorSet hideAnimationSet = null;

    private boolean isShowListChoose = false;
    private boolean isUserLogin = false;

    private int listChoooseHeight;

    private String boardGameStoreName, boardGameStoreId, boardGameStoreTag, boardGameStoreAddress;
    private double boardGameStoreLat, boardGameStoreLng;

    private HashMap<String, Marker> storeMarkerMap = new HashMap<>();

//    private ArrayList<MarkerOptions> markerOptionses;

    private GetBoardGameStoreDataImpl.StoreDataImplListener dataImplListener = new GetBoardGameStoreDataImpl.StoreDataImplListener() {
        @Override
        public void onStoreDataList(ArrayList<EntryDTO> entryDTOs) {

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

                    Marker marker = mMap.addMarker(options);
                    marker.setTag(storeInfoDTO);
                    storeMarkerMap.put(storeInfoDTO.getStoreId(), marker);
                }

                i++;
            }

//            fireBaseModelApi.addValueEventListener(playerRoomListValueEventListener);
            fireBaseModelApi.getDatabaseRef().addChildEventListener(playerRoomListChildEventListener);
        }

        @Override
        public void onUpdateData(String date) {
//            TimeZone.setDefault(TimeZone.getTimeZone("GTM+8"));

        }
    };

    private ChildEventListener playerRoomListChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

//            Log.i(TAG, "map child Added data snap key = " + dataSnapshot.getKey());
            String storeId = dataSnapshot.getKey();

            if (storeMarkerMap.containsKey(storeId)) {
                Marker marker = storeMarkerMap.get(storeId);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }

//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//                Log.i(TAG, "map child add key = " + child.getKey());
//            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//                Log.i(TAG, "map child change key = " + child.getKey());
//            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
//            Log.i(TAG, "map child Removed data snap key = " + dataSnapshot.getKey());

            String storeId = dataSnapshot.getKey();

            if (storeMarkerMap.containsKey(storeId)) {
                Marker marker = storeMarkerMap.get(storeId);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//                Log.i(TAG, "map child Removed key = " + child.getKey());
//            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            isUserLogin = user != null;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initView();

        connectLocationService();

        configLocationRequest();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (boardGameStoreData == null) {
            boardGameStoreData = new GetBoardGameStoreDataImpl(this, dataImplListener);
        }

        if (fireBaseModelApi == null) {
            fireBaseModelApi = new FireBaseModelApiImpl().addApiNote("WaitPlayerRoom");
            fireBaseModelApi.execute();
//            fireBaseModelApi.addValueEventListener(playerRoomListValueEventListener);
        }

        auth = FirebaseAuth.getInstance();


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

    private void initView() {
        llChooseLayout = (LinearLayout) findViewById(R.id.map_list_choose_button);
        llChooseLayout.post(new Runnable() {
            @Override
            public void run() {
                listChoooseHeight = llChooseLayout.getHeight();
//                Log.i(TAG, "height = " + listChoooseHeight);
            }
        });

        tvFindPerson = (TextView) findViewById(R.id.map_list_find_person_play);
        tvWhoPlay = (TextView) findViewById(R.id.map_list_who_play);

        tvFindPerson.setOnClickListener(clickListener);
        tvWhoPlay.setOnClickListener(clickListener);


    }

    private void showListAnimation() {

        enableListChooseButton(false);

        ValueAnimator valueAnimation = ValueAnimator.ofObject(new IntEvaluator(), 0, listChoooseHeight);
        valueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params1 = llChooseLayout.getLayoutParams();
                params1.height = (int) animation.getAnimatedValue();
                llChooseLayout.setLayoutParams(params1);
                if ((int) animation.getAnimatedValue() > 4) {
                    llChooseLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        valueAnimation.setDuration(500);
        valueAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShowListChoose = true;
                enableListChooseButton(true);
            }
        });

        if (!valueAnimation.isRunning()) {
            valueAnimation.start();
        }

    }

    private void hideListAnimation() {

        enableListChooseButton(false);

        ValueAnimator valueAnimation = ValueAnimator.ofObject(new IntEvaluator(), listChoooseHeight, 0);
        valueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params1 = llChooseLayout.getLayoutParams();
                params1.height = (int) animation.getAnimatedValue();
                llChooseLayout.setLayoutParams(params1);
            }
        });

        valueAnimation.setDuration(500);
        valueAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                llChooseLayout.setVisibility(View.INVISIBLE);
                isShowListChoose = false;
                enableListChooseButton(true);
            }
        });

        if (!valueAnimation.isRunning()) {
            valueAnimation.start();
        }
    }

    private void enableListChooseButton(boolean isEnable) {
        tvFindPerson.setEnabled(isEnable);
        tvWhoPlay.setEnabled(isEnable);
    }


    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (boardGameStoreData != null) {
            boardGameStoreData.onDestroy();
        }

        if (fireBaseModelApi != null) {
//            fireBaseModelApi.removeValueEventListener(playerRoomListValueEventListener);
            fireBaseModelApi.getDatabaseRef().removeEventListener(playerRoomListChildEventListener);
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
        moveMap(defaultLatLan, 10);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(this));

        if (boardGameStoreData != null) {
            boardGameStoreData.startLoadData();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                boardGameStoreName = marker.getTitle();
                boardGameStoreLat = marker.getPosition().latitude;
                boardGameStoreLng = marker.getPosition().longitude;

                if (marker.getTag() != null) {
                    StoreInfoDTO infoDTO = (StoreInfoDTO) marker.getTag();

                    boardGameStoreId = infoDTO.getStoreId();
                    boardGameStoreAddress = infoDTO.getAddress();
                    boardGameStoreTag = infoDTO.getAddress().substring(0, 2);


                } else {
                    boardGameStoreId = null;
                    boardGameStoreTag = null;
                    boardGameStoreAddress = null;
                    if (isShowListChoose) {
                        hideListAnimation();
                    }
                }

//                if (marker.equals(currentMarker)){
//
//                    Toast.makeText(MapsActivity.this, "This is my home",Toast.LENGTH_LONG).show();
//                    return true;
//                }
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (marker.getTag() != null) {
//                    StoreInfoDTO infoDTO = (StoreInfoDTO) marker.getTag();

//                    boardGameStoreName = marker.getTitle();

                    if (isShowListChoose) {
                        hideListAnimation();
                    } else {
                        showListAnimation();
                    }
                }

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

    private void moveMap(LatLng place, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place)
                .zoom(zoom)
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
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            currentMarker.showInfoWindow();
            currentMarker.setTag(null);
            moveMap(latLan, 15);
        } else {
            currentMarker.setPosition(latLan);
        }
//        currentMarker.showInfoWindow();


    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent;
            switch (view.getId()) {
                case R.id.map_list_find_person_play:

                    if (!isUserLogin) {
                        intent = new Intent(MapsActivity.this, UserLoginActivity.class);
                        startActivity(intent);
                        break;
                    }

                    if (boardGameStoreId != null) {
                        intent = new Intent(MapsActivity.this, FindPersonActivity.class);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_PLACE, boardGameStoreName);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_ID, boardGameStoreId);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_PLACE_TAG, boardGameStoreTag);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_LAT, boardGameStoreLat);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_LNG, boardGameStoreLng);
                        intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_ADDRESS, boardGameStoreAddress);
                        startActivity(intent);
                    }
                    break;
                case R.id.map_list_who_play:
                    if (boardGameStoreId != null) {
                        intent = new Intent(MapsActivity.this, PlayerStoreRoomListActivity.class);
                        intent.putExtra(PlayerStoreRoomListActivity.KEY_PLAYERROOMLIST_STORE_ID, boardGameStoreId);
                        startActivity(intent);
                    }

                    break;
                default:
                    break;
            }

        }
    };
}
