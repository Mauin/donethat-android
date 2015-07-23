package com.mtramin.donethat.util.converters;

import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

/**
 * Created by m.ramin on 7/22/15.
 */
public class LatLngConverter implements TypeConverter<LatLng> {
    @Override
    public LatLng parse(JsonParser jsonParser) throws IOException {
        // "location":{"lon":"1.0","lat":"2.0"}
        jsonParser.nextToken();
        jsonParser.nextToken();
        float lat = Float.valueOf(jsonParser.getValueAsString());
        jsonParser.nextToken();
        jsonParser.nextToken();
        float lng = Float.valueOf(jsonParser.getValueAsString());

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            jsonParser.nextToken();
        }

        return new LatLng(lat, lng);
    }

    @Override
    public void serialize(LatLng object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
        // "location":{"lon":"1.0","lat":"2.0"}
        jsonGenerator.writeObjectFieldStart("location");

        jsonGenerator.writeFieldName("longitude");
        jsonGenerator.writeObject(String.valueOf(object.longitude));

        jsonGenerator.writeFieldName("latitude");
        jsonGenerator.writeObject(String.valueOf(object.latitude));

        jsonGenerator.writeEndObject();

    }
}
