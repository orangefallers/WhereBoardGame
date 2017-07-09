package com.ofcat.whereboardgame.util;

import android.util.Log;

import com.ofcat.whereboardgame.config.AppConfig;

/**
 * Created by orangefaller on 2017/7/9.
 */

public class MyLog {

    private final static boolean isDebug = AppConfig.DEBUG_MODE;

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }


}
