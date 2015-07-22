package com.mtramin.donethat.util.converters;

import com.bluelinelabs.logansquare.typeconverters.LongBasedTypeConverter;
import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by m.ramin on 7/20/15.
 */
public class UuidConverter extends StringBasedTypeConverter<UUID> {
    @Override
    public UUID getFromString(String string) {
        return UUID.fromString(string);
    }

    @Override
    public String convertToString(UUID object) {
        return object.toString();
    }
}
