package com.mtramin.donethat.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.android.gms.maps.model.LatLng;

import net._01001111.text.LoremIpsum;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by m.ramin on 7/5/15.
 */
@JsonObject
public class Note implements Parcelable {

    @JsonField
    public String title;
    @JsonField
    public String content;
    @JsonField
    public LatLng location;
    @JsonField (name = "note_date")
    public DateTime date;
    @JsonField (name = "uid")
    public UUID id;
    @JsonField
    public Uri image;

    public Note() {
    }

    public Note(Parcel in) {
        title = in.readString();
        content = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
        date = new DateTime().withMillis(in.readLong());
        id = UUID.fromString(in.readString());
        image = Uri.parse(in.readString());
    }

    public Note(String title, String content, LatLng location) {
        this(title, content, location, DateTime.now(), UUID.randomUUID(), Uri.EMPTY);
    }

    public Note(String title, String content, LatLng location, DateTime date, UUID id, Uri image) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.date = date;
        this.id = id;
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeParcelable(location, flags);
        dest.writeLong(date.getMillis());
        dest.writeString(id.toString());
        dest.writeString(image.toString());
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public static class Demo {
        public static List<Note> notes(int count) {
            List<Note> trips = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                trips.add(note());
            }
            return trips;
        }

        public static Note note() {
            LoremIpsum jlorem = new LoremIpsum();
            Uri uri = Uri.parse("http://www.bankingsense.com/wp-content/uploads/2014/12/travel-search-engines.jpg");

            return new Note(jlorem.words(3), jlorem.paragraphs(2), new LatLng(0, 0), DateTime.now(), UUID.randomUUID(), uri);
        }

    }
}
