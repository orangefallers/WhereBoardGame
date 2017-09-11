package com.ofcat.whereboardgame.firebase.dataobj;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by orangefaller on 2017/8/11.
 */

@IgnoreExtraProperties
public class AppUpdateDTO {

    private boolean OpenUpdate;
    private boolean ForcedUpdate;

    private String updateTitle;
    private String updateMessage;
    private String updateToVersion;

    public AppUpdateDTO() {
    }

    public boolean isOpenUpdate() {
        return OpenUpdate;
    }

    public boolean isForcedUpdate() {
        return ForcedUpdate;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String getUpdateToVersion() {
        return updateToVersion;
    }
}
