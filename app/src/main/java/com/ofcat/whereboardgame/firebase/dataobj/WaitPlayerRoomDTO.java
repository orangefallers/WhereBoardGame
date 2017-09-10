package com.ofcat.whereboardgame.firebase.dataobj;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by orangefaller on 2017/4/30.
 */

@IgnoreExtraProperties
public class WaitPlayerRoomDTO implements Serializable {

    private String initiator;

    private String storeName;

    private String contact;

    private String content;

    private String date;

    private String time;

    private LocationDTO location;

    private String addressTag;

    private String storeAddress;

    private String timeStamp;

    private Object timeStampOrder;

    //1 is 缺人     2 is 滿團
    private int roomStatus = 1;

    //目前人數
    private int currentPerson;

    //需要揪的人數
    private int needPerson;

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddressTag() {
        return addressTag;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Object getTimeStampOrder() {
        return timeStampOrder;
    }

    public void setTimeStampOrder(Object timeStampOrder) {
        this.timeStampOrder = timeStampOrder;
    }

    public int getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(int roomStatus) {
        this.roomStatus = roomStatus;
    }

    public int getCurrentPerson() {
        return currentPerson;
    }

    public void setCurrentPerson(int currentPerson) {
        this.currentPerson = currentPerson;
    }

    public int getNeedPerson() {
        return needPerson;
    }

    public void setNeedPerson(int needPerson) {
        this.needPerson = needPerson;
    }

    public boolean isCompleteDTO() {
        if (initiator.trim().equals("") || contact.trim().equals("") || time.trim().equals("") || storeName.trim().equals("")) {
            return false;
        }
        return true;
    }
}
