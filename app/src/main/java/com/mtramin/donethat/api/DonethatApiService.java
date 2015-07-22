package com.mtramin.donethat.api;

import android.content.Context;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.data.Note;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.data.TripDetails;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by m.ramin on 7/5/15.
 */
@Singleton
public class DonethatApiService implements DonethatApi {

    @Inject
    DonethatApi api;

    public DonethatApiService(Context context) {
        ((Application) context.getApplicationContext()).getComponent().inject(this);
    }

    @Override
    public Observable<List<Trip>> getTrips() {
        return api.getTrips();
    }

    @Override
    public Observable<TripDetails> getTrip(UUID tripId) {
        return api.getTrip(tripId);
    }

    @Override
    public Observable<Void> createTrip(Trip trip) {
        return api.createTrip(trip);
    }

    @Override
    public Observable<TripDetails> createNote(UUID tripId, Note note) {
        return api.createNote(tripId, note);
    }

    @Override
    public Observable<Void> putNote(UUID tripId, UUID noteId) {
        return api.putNote(tripId, noteId);
    }
}
