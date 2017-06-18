package com.ofcat.whereboardgame.firebase.dataobj;

/**
 * Created by orangefaller on 2017/5/1.
 */

public class LocationDTO {

    private double latitude;
    private double longitude;

    public LocationDTO(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationDTO() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
