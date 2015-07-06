package com.mtramin.donethat.data;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import net._01001111.text.LoremIpsum;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by m.ramin on 7/5/15.
 */
public class Note {

    public String title;
    public String content;
    public LatLng location;
    public DateTime note_date;
    public UUID uid;
    public Uri image;

    public Note(String title, String content, LatLng location, DateTime note_date, UUID uid, Uri image) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.note_date = note_date;
        this.uid = uid;
        this.image = image;
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

            return new Note(jlorem.words(3), jlorem.paragraphs(2), new LatLng(0, 0), DateTime.now(), UUID.randomUUID(), uri);
        }

    }
}
