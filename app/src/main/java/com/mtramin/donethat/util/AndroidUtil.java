package com.mtramin.donethat.util;

/**
 * Created by m.ramin on 8/21/15.
 */
public class AndroidUtil {

    public static boolean isMarshmallow() {
        int deviceApi = android.os.Build.VERSION.SDK_INT;
        int marshmallow = android.os.Build.VERSION_CODES.M;

        return deviceApi >= marshmallow;
    }
}
