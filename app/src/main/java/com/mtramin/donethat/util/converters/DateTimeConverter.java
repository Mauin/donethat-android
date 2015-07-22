package com.mtramin.donethat.util.converters;

import com.bluelinelabs.logansquare.typeconverters.LongBasedTypeConverter;
import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import org.joda.time.DateTime;

/**
 * Created by m.ramin on 7/20/15.
 */
public class DateTimeConverter extends StringBasedTypeConverter<DateTime> {
    @Override
    public DateTime getFromString(String string) {
        return DateTime.parse(string);
    }

    @Override
    public String convertToString(DateTime object) {
        return object.toString();
    }
}
