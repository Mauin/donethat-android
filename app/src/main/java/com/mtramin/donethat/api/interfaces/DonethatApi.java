package com.mtramin.donethat.api.interfaces;

import com.mtramin.donethat.R;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;

import java.util.List;
import java.util.UUID;

import eu.unicate.retroauth.annotations.Authenticated;
import eu.unicate.retroauth.annotations.Authentication;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by m.ramin on 7/5/15.
 */
@Authentication(accountType = R.string.auth_account_type, tokenType = R.string.auth_token_type)
public interface DonethatApi {

    /*
    GET    /api/trips.json
    POST   /api/trips.json
    GET    /api/trips/:id.json
    PATCH  /api/trips/:id.json
    PUT    /api/trips/:id.json
    DELETE /api/trips/:id.json
    POST   /api/trips/:trip_uid/notes.json
    PATCH  /api/trips/:trip_uid/notes/:id.json
    PUT    /api/trips/:trip_uid/notes/:id.json
     */

    @Authenticated
    @GET("/api/trips.json")
    Observable<List<Trip>> getTrips();

    @Authenticated
    @POST("/api/trips.json")
    Observable<Void> createTrip(@Body Trip trip);

    @Authenticated
    @GET("/api/trips/{tripId}.json")
    Observable<Trip> getTrip(@Path("tripId") UUID tripId);

    @Authenticated
    @POST("/api/trips/{tripId}/notes.json")
    Observable<Trip> createNote(@Path("tripId") UUID tripId, @Body Note note);

    @Authenticated
    @PUT("/api/trips/{tripId}/notes/{noteId}.json")
    Observable<Void> putNote(@Path("tripId") UUID tripId, @Path("noteId") UUID noteId, @Body Note note);

}
