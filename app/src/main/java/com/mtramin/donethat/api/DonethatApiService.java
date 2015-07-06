package com.mtramin.donethat.api;

import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.data.TripDetails;

import java.util.List;
import java.util.UUID;

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
    public Observable<TripDetails> getTrip(@Path("tripId") UUID tripId) {
        return Observable.just(TripDetails.Demo.tripDetails());
    }
}
