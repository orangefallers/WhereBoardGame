package com.ofcat.whereboardgame.dataobj;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by orangefaller on 2017/1/14.
 */

public class CategoryDTO implements Serializable {

    @SerializedName("scheme")
    private String schemeUrl;

    @SerializedName("term")
    private String termUrl;

    public String getSchemeUrl() {
        return schemeUrl;
    }

    public String getTermUrl() {
        return termUrl;
    }
}
