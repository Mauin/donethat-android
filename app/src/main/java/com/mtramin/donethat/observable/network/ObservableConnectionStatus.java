package com.mtramin.donethat.observable.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.net.ConnectivityManagerCompat;

import rx.Observable;

/**
 * Created by m.ramin on 8/16/15.
 */
public class ObservableConnectionStatus {

    public static Observable<ConnectionStatus> observableConnectionStatus(Context context) {

        return Observable.create(subscriber -> {
            final BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    subscriber.onNext(getConnectionStatus(context));
                }
            };

            context.registerReceiver(receiver, getConnectionIntentFilter());
        });
    }

    private static ConnectionStatus getConnectionStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return ConnectionStatus.OFFLINE;
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return ConnectionStatus.ONLINE_MOBILE;
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return ConnectionStatus.ONLINE_WIFI;
        }

        return ConnectionStatus.OFFLINE;
    }

    private static IntentFilter getConnectionIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        return filter;
    }

    public enum ConnectionStatus {
        OFFLINE, ONLINE_WIFI, ONLINE_MOBILE
    }
}
