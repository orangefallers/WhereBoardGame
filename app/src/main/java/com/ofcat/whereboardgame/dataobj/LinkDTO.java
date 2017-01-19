package com.ofcat.whereboardgame.dataobj;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by orangefaller on 2017/1/14.
 */

public class LinkDTO implements Serializable {

    @SerializedName("rel")
    private String rel;

    @SerializedName("type")
    private String type;

    @SerializedName("href")
    private String href;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
