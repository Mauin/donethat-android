package com.mtramin.donethat.observable.googleApiClient;

import android.content.Context;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by m.ramin on 8/16/15.
 */
public abstract class ObservableGoogleApiClient<T> implements Observable.OnSubscribe<T> {

    private final Context context;
    private final Api api;

    protected ObservableGoogleApiClient(Context context, Api api) {
        this.context = context;
        this.api = api;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        GoogleApiClient googleApiClient = createGoogleApiClient(subscriber);

        googleApiClient.connect();
    }

    private GoogleApiClient createGoogleApiClient(Subscriber subscriber) {
        GoogleApiClientCallbacks callbacks = new GoogleApiClientCallbacks(this, subscriber);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(api)
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(callbacks)
                .build();

        callbacks.setGoogleApiClient(googleApiClient);

        return googleApiClient;
    }

    protected abstract void onGoogleApiClientReady(GoogleApiClient googleApiClient, Subscriber subscriber);
}
