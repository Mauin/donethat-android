package com.mtramin.donethat.util.comparator;

import com.mtramin.donethat.data.model.Trip;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.Comparator;

/**
 * TODO: JAVADOC
 */
public class TripComparator implements Comparator<Trip> {
    @Override
    public int compare(Trip lhs, Trip rhs) {
        return DateTimeComparator.getInstance().compare(rhs.date, lhs.date);
    }
}
