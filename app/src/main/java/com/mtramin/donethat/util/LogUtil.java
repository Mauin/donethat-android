package com.mtramin.donethat.util;

import android.util.Log;

/**
 * Created by m.ramin on 7/5/15.
 */
public class LogUtil {

    public static void logException(Object caller, Throwable e) {
        if (e == null) {
            return;
        }

        String tag = caller.getClass().getSimpleName();
        String exceptionStackTrace = Log.getStackTraceString(e);
        Log.e(tag, exceptionStackTrace);
    }
}
