package com.mtramin.donethat.data;

import android.os.Parcel;
import android.os.Parcelable;

import net._01001111.text.LoremIpsum;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by m.ramin on 7/5/15.
 */
public class Trip implements Parcelable {

    public String title;
    public UUID uid;
    public DateTime created_at;

    public Trip(Parcel in) {
        title = in.readString();
        uid = UUID.fromString(in.readString());
        created_at = new DateTime().withMillis(in.readLong());
    }

    public Trip(String title, UUID uid, DateTime created_at) {
        this.title = title;
        this.uid = uid;
        this.created_at = created_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(uid.toString());
        dest.writeLong(created_at.getMillis());
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
