package com.ofcat.whereboardgame.firebase.dataobj;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by orangefaller on 2017/2/4.
 */

@IgnoreExtraProperties
public class StoreDTO {

    private String storeName;
    private String storeAddress;
    private String storeStatus;

    public StoreDTO() {
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(String storeStatus) {
        this.storeStatus = storeStatus;
    }
}
