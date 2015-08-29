package com.mtramin.donethat.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * TODO: JAVADOC
 */
public class ViewUtil {

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
