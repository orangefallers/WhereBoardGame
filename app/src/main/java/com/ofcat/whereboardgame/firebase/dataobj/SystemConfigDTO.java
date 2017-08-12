package com.ofcat.whereboardgame.firebase.dataobj;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by orangefaller on 2017/2/5.
 */
@IgnoreExtraProperties
public class SystemConfigDTO {

    private boolean OpenWatchMapFeature;

    private boolean OpenPlayerRoomListFeature;

    private boolean OpenUserInfoFeature;

    private boolean OpenSuggestionsFeature;

    private boolean OpenReportFeature;

    private AppUpdateDTO OpenAppUpdateFeature;

    public SystemConfigDTO() {
    }

    public boolean isOpenReportFeature() {
        return OpenReportFeature;
    }

    public boolean isOpenWatchMapFeature() {
        return OpenWatchMapFeature;
    }

    public boolean isOpenPlayerRoomListFeature() {
        return OpenPlayerRoomListFeature;
    }

    public boolean isOpenUserInfoFeature() {
        return OpenUserInfoFeature;
    }

    public boolean isOpenSuggestionsFeature() {
        return OpenSuggestionsFeature;
    }

    public AppUpdateDTO getOpenAppUpdateFeature() {
        if (OpenAppUpdateFeature == null) {
            return new AppUpdateDTO();
        }
        return OpenAppUpdateFeature;
    }


}
