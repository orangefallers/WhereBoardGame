package com.ofcat.whereboardgame.dataobj;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by orangefaller on 2017/1/14.
 */

public class TitleDTO implements Serializable {

    @SerializedName("type")
    private String type;

    @SerializedName("$t")
    private String title;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
