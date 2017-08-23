package com.ofcat.whereboardgame.firebase.model;

import com.ofcat.whereboardgame.config.AppConfig;

/**
 * Created by orangefaller on 2017/8/23.
 */

public class FireBaseUrl {

    private String url;
    private String rootUrl = AppConfig.FIREBASE_URL;

    private FireBaseUrl(Builder builder) {
        url = rootUrl + builder.urlNote;
    }

    public String getUrl() {
        return url;
    }

    public static final class Builder {
        public String urlNote = "";

        public Builder() {
        }

        public Builder addUrlNote(String note) {
            urlNote = urlNote + "/" + note;
            return this;
        }

        public FireBaseUrl build() {
            return new FireBaseUrl(this);
        }
    }
}
