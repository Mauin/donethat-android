package com.mtramin.donethat.data.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import net._01001111.text.LoremIpsum;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by m.ramin on 7/6/15.
 */
@JsonObject
public class Trip {
    @JsonField(name = "uid")
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

    public Trip() {
    }

    public Trip(UUID id, String title, DateTime date, DateTime updated, String content, List<Note> notes) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.updated = updated;
        this.content = content;
        this.notes = notes;
    }

    public static class Builder {
        public UUID id;
        public DateTime date;
        public DateTime updated;
        public String title;
        public String content;
        public List<Note> notes;

        public Builder() {
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder id(UUID id) {
            this.id = id;
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

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder notes(List<Note> notes) {
            this.notes = notes;
            return this;
        }

        public Trip build() {
            return new Trip(id, title, date, updated, content, notes);
        }
    }

    public static class Demo {
        public static Trip tripDetails() {
            LoremIpsum jlorem = new LoremIpsum();
            return new Trip(UUID.randomUUID(), jlorem.words(3), DateTime.now(), DateTime.now(), jlorem.sentence(), Note.Demo.notes(25));
        }
    }
}
