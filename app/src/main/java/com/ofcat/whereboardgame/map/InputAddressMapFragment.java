package com.ofcat.whereboardgame.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.adapter.MapInfoWindowAdapter;
import com.ofcat.whereboardgame.findperson.CustomFindPersonActivity;
import com.ofcat.whereboardgame.model.GetLatLngDataImpl;
import com.ofcat.whereboardgame.util.MyLog;

/**
 * Created by orangefaller on 2017/9/3.
 */

public class InputAddressMapFragment extends Fragment implements OnMapReadyCallback {

    private final String TAG = InputAddressMapFragment.class.getSimpleName();

    private LatLng defaultPosition = new LatLng(23.9179637, 120.6775054);

    private GetLatLngDataImpl getLatLngData;

    private InputAddressMapFragmentListener listener;

    private ProgressDialog progressDialog;

    private FrameLayout flmapPreview;

    private EditText etStoreName;
    private EditText etStoreAddress;
    private Button btnMapPreview;
    private Button btnConfirm;

    private GoogleMap map;
    private Marker storeMarker;

    private String storeName;
    private String storeAddress;
    private String previewAddress;
    private double storeLat = 0.0, storeLng = 0.0;

    private boolean isShowPreview;


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_custom_input_position_preview:
                    isShowPreview = true;
                    closeKeyBorad(view);


                    storeName = etStoreName.getText().toString();
                    previewAddress = etStoreAddress.getText().toString().trim();
                    if (!previewAddress.equals("")) {
                        flmapPreview.setVisibility(View.VISIBLE);
                        previewMap(previewAddress);
                    } else {
                        showErrorEmptyAddressDialog();
                    }

                    break;
                case R.id.btn_custom_input_confirm:
                    isShowPreview = false;

                    storeName = etStoreName.getText().toString();
                    storeAddress = etStoreAddress.getText().toString().trim();

                    if (storeName.equals("")) {
                        showErrorEmptyStoreNameDialog();
                        break;
                    }

                    if (!previewAddress.equals(storeAddress)) {
                        previewMap(storeAddress);
                    } else {
                        if (listener != null) {
                            listener.onFragmentResult(storeName, storeAddress, storeLat, storeLng);
                            getActivity().onBackPressed();
                        }
                    }

                    break;
            }

        }
    };


    public static InputAddressMapFragment newInstance() {
        InputAddressMapFragment fragment = new InputAddressMapFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof CustomFindPersonActivity) {
            listener = (InputAddressMapFragmentListener) getActivity();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLatLngData == null) {
            getLatLngData = new GetLatLngDataImpl(latLngDataImplListener);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_address, container, false);
        initView(view);
        view.findViewById(R.id.map_custom_input_preview);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MapFragment mapFragment = MapFragment.newInstance();
        getActivity().getFragmentManager()
                .beginTransaction()
                .replace(R.id.map_custom_input_preview, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
    }

    private void initView(View view) {
        flmapPreview = (FrameLayout) view.findViewById(R.id.map_custom_input_preview);
        etStoreName = (EditText) view.findViewById(R.id.et_custom_input_store_name);
        etStoreAddress = (EditText) view.findViewById(R.id.et_custom_input_store_address);
        btnMapPreview = (Button) view.findViewById(R.id.btn_custom_input_position_preview);
        btnConfirm = (Button) view.findViewById(R.id.btn_custom_input_confirm);

        btnMapPreview.setOnClickListener(clickListener);
        btnConfirm.setOnClickListener(clickListener);
    }

    private void previewMap(String address) {
        showProgress(true);
        btnMapPreview.setEnabled(false);
        if (getLatLngData != null) {
            getLatLngData.getLatLngByAddress(address);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        moveMap(defaultPosition, 5);
        map.setInfoWindowAdapter(new MapInfoWindowAdapter(getActivity()));
        MyLog.i(TAG, "onMapReady finish");
    }

    private void moveMap(LatLng place, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place)
                .zoom(zoom)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if (storeMarker != null && storeAddress != null) {
                    storeMarker.showInfoWindow();
                }

            }

            @Override
            public void onCancel() {

            }
        });
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void showNoAddressRemindDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.custom_findperson_dialog_error_empty_store_message)
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }


    private void showErrorEmptyStoreNameDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.custom_findperson_dialog_error_empty_store_message)
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }

    private void showErrorEmptyAddressDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.custom_findperson_dialog_error_empty_address_message)
                .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }

    private void closeKeyBorad(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showProgress(boolean isShow) {
        if (isShow) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("位置計算中");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            } else if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.hide();
            }
        }
    }

    private GetLatLngDataImpl.LatLngDataImplListener latLngDataImplListener = new GetLatLngDataImpl.LatLngDataImplListener() {
        @Override
        public void onResult(String address, double latitude, double longitude) {
            showProgress(false);
            btnMapPreview.setEnabled(true);
            if (map != null) {
                storeLat = latitude;
                storeLng = longitude;

                if (isShowPreview) {
                    LatLng position = new LatLng(storeLat, storeLng);


                    if (storeMarker == null) {
                        storeMarker = map.addMarker(new MarkerOptions()
                                .position(position)
                                .title(storeName)
                                .snippet(address)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    } else {
                        storeMarker.setPosition(position);
                        storeMarker.setTitle(storeName);
                        storeMarker.setSnippet(address);
                    }

                    moveMap(position, 17);
                } else {
                    if (listener != null) {
                        listener.onFragmentResult(storeName, storeAddress, storeLat, storeLng);
                        getActivity().onBackPressed();
                    }
                }
            }

        }

        @Override
        public void onFail(String status) {
            btnMapPreview.setEnabled(true);

        }
    };

    public interface InputAddressMapFragmentListener {
        void onFragmentResult(String storeName, String address, double storeLat, double storeLng);
    }
}
