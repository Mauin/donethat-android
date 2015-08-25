package com.mtramin.donethat.api;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.DonethatCache;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by m.ramin on 7/5/15.
 */
@Singleton
public class DonethatApiService implements DonethatApi {

    private static final String TAG = DonethatApiService.class.getName();
    private final Context context;

    @Inject
    DonethatApi api;

    @Inject
    DonethatCache database;

    public DonethatApiService(Context context) {
        this.context = context;
        ((Application) context.getApplicationContext()).getComponent().inject(this);
    }

    @Override
    public Observable<List<Trip>> getTrips() {
        return api.getTrips();
    }

    @Override
    public Observable<Trip> getTrip(UUID tripId) {
        return api.getTrip(tripId);
    }

    @Override
    public Observable<Void> createTrip(Trip trip) {
        return api.createTrip(trip);
    }

    @Override
    public Observable<Trip> createNote(UUID tripId, Note note) {
        note.updated = DateTime.now();
        return api.createNote(tripId, note);
    }

    @Override
    public Observable<Void> putNote(UUID tripId, UUID noteId, Note note) {
        note.updated = DateTime.now();
        return api.putNote(tripId, noteId, note);
    }
}
