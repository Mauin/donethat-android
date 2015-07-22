package com.mtramin.donethat.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import net._01001111.text.LoremIpsum;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by m.ramin on 7/5/15.
 */
@JsonObject
public class Trip implements Parcelable {

    @JsonField
    public String title;
    @JsonField(name = "uid")
    public UUID id;
    @JsonField(name = "updated_at")
    public DateTime updated;

    public Trip(Parcel in) {
        title = in.readString();
        id = UUID.fromString(in.readString());
        updated = new DateTime().withMillis(in.readLong());
    }

    public Trip() {
    }

    public Trip(String title) {
        this(title, UUID.randomUUID(), DateTime.now());
    }

    public Trip(String title, UUID uid, DateTime updated) {
        this.title = title;
        this.id = uid;
        this.updated = updated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(id.toString());
        dest.writeLong(updated.getMillis());
    }

    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel source) {
            return new Trip(source);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public static class Demo {

        public static Trip trip() {
            LoremIpsum jlorem = new LoremIpsum();
            return new Trip(jlorem.words(3), UUID.randomUUID(), DateTime.now());
        }

        public static List<Trip> trips(int count) {
            List<Trip> trips = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                trips.add(trip());
            }
            return trips;
        }
    }
}
