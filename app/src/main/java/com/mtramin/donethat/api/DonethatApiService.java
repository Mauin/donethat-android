package com.mtramin.donethat.api;

import android.content.Context;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.data.Note;
import com.mtramin.donethat.data.Trip;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.http.Path;
import rx.Observable;

/**
 * Created by m.ramin on 7/5/15.
 */
@Singleton
public class DonethatApiService implements DonethatApi {

    @Inject
    DonethatApi api;

    @Override
    public Observable<List<Trip>> getTrips() {
        return Observable.just(Trip.Demo.trips(15));
    }

    @Override
    public Observable<List<Note>> getNotesForTrip(@Path("tripId") int tripId) {
        List<Note> notes = new ArrayList<>();

        return Observable.just(notes);
    }
}
