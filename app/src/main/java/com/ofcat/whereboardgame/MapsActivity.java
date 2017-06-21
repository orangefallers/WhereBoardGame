package com.ofcat.whereboardgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.adapter.MapInfoWindowAdapter;
import com.ofcat.whereboardgame.dataobj.EntryDTO;
import com.ofcat.whereboardgame.dataobj.StoreInfoDTO;
import com.ofcat.whereboardgame.findperson.FindPersonActivity;
import com.ofcat.whereboardgame.firebase.dataobj.WaitPlayerRoomDTO;
import com.ofcat.whereboardgame.firebase.model.FireBaseModelApiImpl;
import com.ofcat.whereboardgame.model.GetBoardGameStoreDataImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private FireBaseModelApiImpl fireBaseModelApi;

    private Location currentLocation;

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

    private int listChoooseHeight;

    private String boardGameStoreName, boardGameStoreId;

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
                }

                i++;
            }
        }

        @Override
        public void onUpdateData(String date) {
//            TimeZone.setDefault(TimeZone.getTimeZone("GTM+8"));

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
//        llChooseLayout.setVisibility(View.VISIBLE);
//        if (showAnimationSet == null) {
//            showAnimationSet = new AnimatorSet();
//            int hideY = llChooseLayout.getBottom();
//            int showY = (int) llChooseLayout.getY();
//            int height = llChooseLayout.getHeight();
//
//            // 設定顯示列表動畫
//            showAnimationSet = new AnimatorSet();
//            showAnimationSet.play(ObjectAnimator.ofFloat(llChooseLayout, "y", hideY, showY));
//            showAnimationSet.setDuration(500);
//            showAnimationSet.setInterpolator(new AccelerateDecelerateInterpolator());
//            showAnimationSet.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
////                    llChooseLayout.animate().setListener(null);
//                    isShowListChoose = true;
//                }
//            });
//
//        }
//
//        if (!showAnimationSet.isRunning()) {
//            showAnimationSet.start();
//        }
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
            }
        });

        if (!valueAnimation.isRunning()) {
            valueAnimation.start();
        }

    }

    private void hideListAnimation() {
//        if (hideAnimationSet == null) {
//            int hideY = llChooseLayout.getBottom();
//            int showY = (int) llChooseLayout.getY();
//            int height = llChooseLayout.getHeight();
//            hideAnimationSet = new AnimatorSet();
//            hideAnimationSet.play(ObjectAnimator.ofFloat(llChooseLayout, "y", hideY, 0));
//            hideAnimationSet.setDuration(500);
//            hideAnimationSet.setInterpolator(new AccelerateDecelerateInterpolator());
//            hideAnimationSet.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    llChooseLayout.setVisibility(View.INVISIBLE);
////                    llChooseLayout.animate().setListener(null);
//                    isShowListChoose = false;
//                }
//            });
//        }
//
//        if (!hideAnimationSet.isRunning()) {
//            hideAnimationSet.start();
//        }

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
            }
        });

        if (!valueAnimation.isRunning()) {
            valueAnimation.start();
        }
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

//        if (fireBaseModelApi != null) {
//            fireBaseModelApi.removeValueEventListener(systemConfigValueEventListener);
//            fireBaseModelApi.removeValueEventListener(systemNotifyValueEventListener);
//        }
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


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                boardGameStoreName = marker.getTitle();

                if (marker.getTag() != null) {
                    StoreInfoDTO infoDTO = (StoreInfoDTO) marker.getTag();

                    boardGameStoreId = infoDTO.getStoreId();

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
                    StoreInfoDTO infoDTO = (StoreInfoDTO) marker.getTag();

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
            currentMarker.setTag(null);
            moveMap(latLan);
        } else {
            currentMarker.setPosition(latLan);
        }
//        currentMarker.showInfoWindow();


    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.map_list_find_person_play:
                    Intent intent = new Intent(MapsActivity.this, FindPersonActivity.class);
                    intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_PLACE, boardGameStoreName);
                    intent.putExtra(FindPersonActivity.KEY_FINDPERSON_BGS_ID, boardGameStoreId);
                    startActivity(intent);
                    break;
                case R.id.map_list_who_play:
                    if (fireBaseModelApi == null) {
                        fireBaseModelApi = new FireBaseModelApiImpl().addApiNote("WaitPlayerRoom").addApiNote("100003");
                        fireBaseModelApi.execute();
                        fireBaseModelApi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

//                                GenericTypeIndicator<List<WaitPlayerRoomDTO>> typeIndicator = new GenericTypeIndicator<List<WaitPlayerRoomDTO>>() {
//                                };
//                                List<WaitPlayerRoomDTO> storeDTOs = dataSnapshot.getValue(typeIndicator);
//                                if (!storeDTOs.isEmpty()) {
////                                    Log.i("kevintest", "store size = " + storeDTOs.size());
//                                }

                                GenericTypeIndicator<HashMap<String, WaitPlayerRoomDTO>> typeIndicator = new GenericTypeIndicator<HashMap<String, WaitPlayerRoomDTO>>() {
                                };

                                HashMap<String, WaitPlayerRoomDTO> roomDTOHashMap = dataSnapshot.getValue(typeIndicator);
//                                Map.Entry entry = (Map.Entry) roomDTOHashMap.entrySet();
                                for (Map.Entry entry : roomDTOHashMap.entrySet()) {
                                    if (entry.getValue() instanceof WaitPlayerRoomDTO) {
                                        WaitPlayerRoomDTO roomDTO = (WaitPlayerRoomDTO) entry.getValue();
                                        Log.i("kevintest", "room map key = " + entry.getKey() + " value = " + roomDTO.getStoreName());
                                    }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });
                    }
                    break;
                default:
                    break;
            }

        }
    };
}
