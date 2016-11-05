package net.herchenroether.hikinggps.utils;

import android.util.Log;

/**
 * Simple class to output to logcat
 *
 * Created by Adam Herchenroether on 11/5/2016.
 */
public class Logger {
    private static final String TAG = "HikingGPS";

    public static void info(String text) {
        Log.i(TAG, text);
    }

    public static void warn(String text) {
        Log.w(TAG, text);
    }

    public static void error(String text) {
        Log.e(TAG, text);
    }
}
