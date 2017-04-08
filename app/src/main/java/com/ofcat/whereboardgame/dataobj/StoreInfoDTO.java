package com.ofcat.whereboardgame.dataobj;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by orangefaller on 2017/1/18.
 */

public class StoreInfoDTO implements Serializable {

    @SerializedName("storeid")
    private String storeId;

    @SerializedName("storename")
    private String storeName;

    @SerializedName("address")
    private String address;

    //緯度
    @SerializedName("latitude")
    private String latitude;

    //經度
    @SerializedName("longitude")
    private String longitude;


    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
