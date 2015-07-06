package com.mtramin.donethat.data;

import net._01001111.text.LoremIpsum;

import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

/**
 * Created by m.ramin on 7/6/15.
 */
public class TripDetails {

    public UUID uid;
    public String title;
    public DateTime created_at;
    public DateTime updated_at;
    public String content;
    public List<Note> notes;

    public TripDetails(UUID uid, String title, DateTime created_at, DateTime updated_at, String content, List<Note> notes) {
        this.uid = uid;
        this.title = title;
        this.created_at = created_at;
        this.updated_at = updated_at;
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
