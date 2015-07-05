package com.mtramin.donethat.data;

import net._01001111.text.LoremIpsum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m.ramin on 7/5/15.
 */
public class Trip {

    public String title;
    public String description;
    public int id;

    public Trip(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public static class Demo {

        public static Trip trip(int id) {
            LoremIpsum jlorem = new LoremIpsum();
            return new Trip(jlorem.words(3), jlorem.words(10), id);
        }

        public static List<Trip> trips(int count) {
            List<Trip> trips = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                trips.add(trip(i));
            }
            return trips;
        }
    }
}
