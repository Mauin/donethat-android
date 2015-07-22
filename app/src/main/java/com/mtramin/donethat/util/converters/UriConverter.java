package com.mtramin.donethat.util.converters;

import android.net.Uri;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

/**
 * Created by m.ramin on 7/22/15.
 */
public class UriConverter extends StringBasedTypeConverter<Uri> {
    @Override
    public Uri getFromString(String string) {
        return Uri.parse(string);
    }

    @Override
    public String convertToString(Uri object) {
        return object.toString();
    }
}
