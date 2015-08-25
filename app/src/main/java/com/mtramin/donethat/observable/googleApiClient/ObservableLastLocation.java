package com.mtramin.donethat.observable.googleApiClient;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by m.ramin on 8/16/15.
 */
public class ObservableLastLocation extends ObservableGoogleApiClient<Location> {

    public static Observable<Location> create(Context context) {
        return Observable.create(new ObservableLastLocation(context));
    }

    private ObservableLastLocation(Context context) {
        super(context, LocationServices.API);
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient googleApiClient, Subscriber subscriber) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        subscriber.onNext(location);
        subscriber.onCompleted();
    }
}
