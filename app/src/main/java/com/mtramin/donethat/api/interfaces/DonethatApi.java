package com.mtramin.donethat.api.interfaces;

import com.mtramin.donethat.data.Note;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.data.TripDetails;

import java.util.List;
import java.util.UUID;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by m.ramin on 7/5/15.
 */
public interface DonethatApi {

    @GET("/api/trips")
    Observable<List<Trip>> getTrips();

    @GET("/api/trips/{tripId}")
    Observable<TripDetails> getTrip(@Path("tripId") UUID tripId);
}
