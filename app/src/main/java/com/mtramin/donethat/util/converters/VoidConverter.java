package com.mtramin.donethat.util.converters;

import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * Created by m.ramin on 7/20/15.
 */
public class VoidConverter implements TypeConverter<Void> {
    @Override
    public Void parse(JsonParser jsonParser) throws IOException {
        return jsonParser.readValueAs(Void.class);
    }

    @Override
    public void serialize(Void object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {

    }
}
