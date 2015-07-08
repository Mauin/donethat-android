package com.mtramin.donethat.api.interfaces;

import com.mtramin.donethat.R;
import com.mtramin.donethat.data.Note;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.data.TripDetails;

import java.util.List;
import java.util.UUID;

import eu.unicate.retroauth.annotations.Authenticated;
import eu.unicate.retroauth.annotations.Authentication;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by m.ramin on 7/5/15.
 */
@Authentication(accountType = R.string.auth_account_type, tokenType = R.string.auth_token_type)
public interface DonethatApi {

    @Authenticated
    @GET("/api/trips")
    Observable<List<Trip>> getTrips();

    @Authenticated
    @GET("/api/trips/{tripId}")
    Observable<TripDetails> getTrip(@Path("tripId") UUID tripId);
}
