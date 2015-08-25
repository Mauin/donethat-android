package com.mtramin.donethat.observable.googleApiClient;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import rx.Subscriber;

/**
 * Created by m.ramin on 8/16/15.
 */
public class GoogleApiClientCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = GoogleApiClientCallbacks.class.getName();

    private final Subscriber subscriber;
    private final ObservableGoogleApiClient client;
    private GoogleApiClient googleApiClient;

    public GoogleApiClientCallbacks(ObservableGoogleApiClient client, Subscriber subscriber) {
        this.client = client;
        this.subscriber = subscriber;
    }

    @Override
    public void onConnected(Bundle bundle) {
        client.onGoogleApiClientReady(googleApiClient, subscriber);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended ");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed " + connectionResult.toString());
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }
}
