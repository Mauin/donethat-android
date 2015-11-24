package com.mtramin.donethat;

import android.net.Uri;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.mtramin.donethat.di.Component;
import com.mtramin.donethat.di.DaggerComponent;
import com.mtramin.donethat.di.modules.ApiModule;
import com.mtramin.donethat.di.modules.ApplicationModule;
import com.mtramin.donethat.di.modules.ServiceModule;
import com.mtramin.donethat.util.converters.DateTimeConverter;
import com.mtramin.donethat.util.converters.LatLngConverter;
import com.mtramin.donethat.util.converters.UriConverter;
import com.mtramin.donethat.util.converters.UuidConverter;
import com.mtramin.donethat.util.converters.VoidConverter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by m.ramin on 7/5/15.
 */
public class Application extends android.app.Application {

    Component component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerComponent.builder()
                .apiModule(new ApiModule(this))
                .serviceModule(new ServiceModule())
                .applicationModule(new ApplicationModule(this))
                .build();

        MapsInitializer.initialize(this);

        JodaTimeAndroid.init(this);

        setUpJsonConverters();
        setUpRealm();
    }

    private void setUpRealm() {
        // Configure Realm for the application
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.deleteRealm(realmConfiguration); // Start clean
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private void setUpJsonConverters() {
        LoganSquare.registerTypeConverter(DateTime.class, new DateTimeConverter());
        LoganSquare.registerTypeConverter(UUID.class, new UuidConverter());
        LoganSquare.registerTypeConverter(Void.class, new VoidConverter());
        LoganSquare.registerTypeConverter(Uri.class, new UriConverter());
        LoganSquare.registerTypeConverter(LatLng.class, new LatLngConverter());
    }

    public Component getComponent() {
        return component;
    }
}
