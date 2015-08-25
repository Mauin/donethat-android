package com.mtramin.donethat.data.model;

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
    @JsonField (name = "content")
    public String content;
    @JsonField
    public LatLng location;
    @JsonField (name = "note_date")
    public DateTime date;
    @JsonField (name = "updated_at")
    public DateTime updated;
    @JsonField (name = "uid")
    public UUID id;
    @JsonField (name = "image")
    public Uri image;

    public UUID tripId;

    public Note() {
    }

    public Note(Parcel in) {
        title = in.readString();
        content = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
        date = new DateTime().withMillis(in.readLong());
        id = UUID.fromString(in.readString());
        image = Uri.parse(in.readString());
        tripId = UUID.fromString(in.readString());
    }

    public Note(String title, String content, LatLng location, UUID tripId) {
        this(title, content, location, DateTime.now(), DateTime.now(), UUID.randomUUID(), Uri.EMPTY, tripId);
    }

    public Note(String title, String content, LatLng location, DateTime date, DateTime updated, UUID id, Uri image, UUID tripId) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.date = date;
        this.updated = updated;
        this.id = id;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        return !(id != null ? !id.equals(note.id) : note.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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

        if (image == null) {
            image = Uri.EMPTY;
        }
        dest.writeString(image.toString());
        dest.writeString(tripId.toString());
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

    public static class Builder {
        public String title;
        public String content;
        public LatLng location;
        public DateTime date;
        public DateTime updated;
        public UUID id;
        public UUID tripId;
        public Uri image;

        public Builder() {
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder tripId(UUID id) {
            this.tripId = id;
            return this;
        }

        public Builder updated(DateTime updated) {
            this.updated = updated;
            return this;
        }

        public Builder date(DateTime date) {
            this.date = date;
            return this;
        }

        public Builder image(Uri image) {
            this.image = image;
            return this;
        }

        public Builder location(LatLng location) {
            this.location = location;
            return this;
        }

        public Note build() {
            return new Note(title, content, location, date, updated, id, image, tripId);
        }
    }

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

            return new Note(jlorem.words(3), jlorem.paragraphs(2), new LatLng(0, 0), UUID.randomUUID());
        }

    }
}
