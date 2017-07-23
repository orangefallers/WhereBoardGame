package com.ofcat.whereboardgame.firebase.dataobj;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by orangefaller on 2017/7/22.
 */

@IgnoreExtraProperties
public class UserInfoDTO implements Serializable {

    private String storeId;

    private WaitPlayerRoomDTO waitPlayerRoomDTO;

    public WaitPlayerRoomDTO getWaitPlayerRoomDTO() {
        return waitPlayerRoomDTO;
    }

    public void setWaitPlayerRoomDTO(WaitPlayerRoomDTO waitPlayerRoomDTO) {
        this.waitPlayerRoomDTO = waitPlayerRoomDTO;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
