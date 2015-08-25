package com.mtramin.donethat.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.api.SyncService;
import com.mtramin.donethat.util.LogUtil;

import java.util.UUID;

import javax.inject.Inject;

import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by m.ramin on 8/23/15.
 */
public class BackgroundSyncService extends Service {

    private static final String TAG = BackgroundSyncService.class.getName();
    private static final String EXTRA_TRIP_ID = "EXTRA_TRIP_ID";

    @Inject
    SyncService syncService;

    private Subscription syncSubscription;

    public static Intent fullSyncIntent(Context context) {
        return new Intent(context, BackgroundSyncService.class);
    }

    public static Intent tripSyncIntent(Context context, UUID tripId) {
        Intent intent = new Intent(context, BackgroundSyncService.class);
        intent.putExtra(EXTRA_TRIP_ID, tripId);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ((Application) getApplicationContext()).getComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(EXTRA_TRIP_ID)) {
            Log.e(TAG, "start trip sync");
            UUID tripId = (UUID) intent.getSerializableExtra(EXTRA_TRIP_ID);
            syncSubscription = syncService.syncTrip(tripId)
                    .subscribeOn(Schedulers.io())
                    .doOnNext(o -> Log.e(TAG, "trip sync complete"))
                    .subscribe(o -> stopSelf(), throwable -> LogUtil.logException(this, throwable));
        } else {
            Log.e(TAG, "start full sync");
            syncSubscription = syncService.syncAll()
                    .subscribeOn(Schedulers.io())
                    .doOnNext(o -> Log.e(TAG, "full sync complete"))
                    .subscribe(o -> stopSelf(), throwable -> LogUtil.logException(this, throwable));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        syncSubscription.unsubscribe();
        super.onDestroy();
    }

}
