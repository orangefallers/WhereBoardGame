package com.ofcat.whereboardgame.dataobj;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by orangefaller on 2017/1/14.
 */

public class EntryDTO implements Serializable {

    @SerializedName("id")
    private TstringDTO id;

    @SerializedName("updated")
    private TstringDTO updated;

    @SerializedName("category")
    private ArrayList<CategoryDTO> categoryDTOs;

    @SerializedName("title")
    private TypeDTO title;

    @SerializedName("content")
    private TypeDTO content;

    @SerializedName("link")
    private ArrayList<LinkDTO> linkDTOs;

    @SerializedName("gsx$storename")
    private TstringDTO storeName;

    @SerializedName("gsx$address")
    private TstringDTO address;

    //緯度
    @SerializedName("gsx$latitude")
    private TstringDTO latitude;

    //經度
    @SerializedName("gsx$longitude")
    private TstringDTO longitude;

    public TstringDTO getUpdated() {
        return updated;
    }

    public void setUpdated(TstringDTO updated) {
        this.updated = updated;
    }

    public TypeDTO getTitle() {
        return title;
    }

    public void setTitle(TypeDTO title) {
        this.title = title;
    }

    public TypeDTO getContent() {
        return content;
    }

    public void setContent(TypeDTO content) {
        this.content = content;
    }

    public TstringDTO getStoreName() {
        return storeName;
    }

    public void setStoreName(TstringDTO storeName) {
        this.storeName = storeName;
    }

    public TstringDTO getAddress() {
        return address;
    }

    public void setAddress(TstringDTO address) {
        this.address = address;
    }

    public TstringDTO getLatitude() {
        return latitude;
    }

    public void setLatitude(TstringDTO latitude) {
        this.latitude = latitude;
    }

    public TstringDTO getLongitude() {
        return longitude;
    }

    public void setLongitude(TstringDTO longitude) {
        this.longitude = longitude;
    }
}
