package com.mtramin.donethat.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by m.ramin on 8/18/15.
 */
public class PermissionUtil {

    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 2;

    public static boolean shouldRequestPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean permissionGranted(int[] grantResults) {
        return grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
