package com.mtramin.donethat.data;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import net._01001111.text.LoremIpsum;

import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

/**
 * Created by m.ramin on 7/6/15.
 */
@JsonObject
public class TripDetails {
    @JsonField(name = "id")
    public UUID id;
    @JsonField(name = "created_at")
    public DateTime date;
    @JsonField(name = "updated_at")
    public DateTime updated;
    @JsonField
    public String title;
    @JsonField
    public String content;
    @JsonField
    public List<Note> notes;

    public TripDetails() {
    }

    public TripDetails(UUID id, String title, DateTime date, DateTime updated, String content, List<Note> notes) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.updated = updated;
        this.content = content;
        this.notes = notes;
    }

    public static class Demo {
        public static TripDetails tripDetails() {
            LoremIpsum jlorem = new LoremIpsum();
            return new TripDetails(UUID.randomUUID(), jlorem.words(3), DateTime.now(), DateTime.now(), jlorem.sentence(), Note.Demo.notes(25));
        }
    }
}
