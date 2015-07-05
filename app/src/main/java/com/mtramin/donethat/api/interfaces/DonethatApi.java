package com.mtramin.donethat.api.interfaces;

import com.mtramin.donethat.data.Note;
import com.mtramin.donethat.data.Trip;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by m.ramin on 7/5/15.
 */
public interface DonethatApi {

    @GET("/trips")
    Observable<List<Trip>> getTrips();

    @GET("/trip/{tripId}/notes")
    Observable<List<Note>> getNotesForTrip(@Path("tripId") int tripId);
}
