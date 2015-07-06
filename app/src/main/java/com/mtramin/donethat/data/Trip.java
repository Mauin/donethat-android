package com.mtramin.donethat.data;

import net._01001111.text.LoremIpsum;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by m.ramin on 7/5/15.
 */
public class Trip {

    public String title;
    public UUID uid;
    public DateTime created_at;

    public Trip(String title, UUID uid, DateTime created_at) {
        this.title = title;
        this.uid = uid;
        this.created_at = created_at;
    }

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
