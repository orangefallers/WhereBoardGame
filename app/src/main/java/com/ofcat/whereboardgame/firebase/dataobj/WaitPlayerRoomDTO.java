package com.ofcat.whereboardgame.firebase.dataobj;


import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by orangefaller on 2017/4/30.
 */

@IgnoreExtraProperties
public class WaitPlayerRoomDTO {

    private String initiator;

    private String storeName;

    private String contact;

    private String content;

    private String date;

    private String time;

    private LocationDTO location;

    private String addressTag;

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
}
