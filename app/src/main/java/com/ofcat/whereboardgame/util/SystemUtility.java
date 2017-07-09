package com.ofcat.whereboardgame.util;

import java.sql.Timestamp;

/**
 * Created by orangefaller on 2017/6/28.
 */

public class SystemUtility {

    public static String getTimeStamp(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString();
    }
}
