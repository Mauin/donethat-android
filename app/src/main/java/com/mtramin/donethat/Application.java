package com.mtramin.donethat;

import com.bluelinelabs.logansquare.LoganSquare;
import com.mtramin.donethat.di.Component;
import com.mtramin.donethat.di.DaggerComponent;
import com.mtramin.donethat.di.modules.ApiModule;
import com.mtramin.donethat.di.modules.ApplicationModule;
import com.mtramin.donethat.di.modules.ServiceModule;
import com.mtramin.donethat.util.converters.DateTimeConverter;
import com.mtramin.donethat.util.converters.UuidConverter;
import com.mtramin.donethat.util.converters.VoidConverter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.UUID;

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

        JodaTimeAndroid.init(this);

        setUpJsonConverters();
    }

    private void setUpJsonConverters() {
        LoganSquare.registerTypeConverter(DateTime.class, new DateTimeConverter());
        LoganSquare.registerTypeConverter(UUID.class, new UuidConverter());
        LoganSquare.registerTypeConverter(Void.class, new VoidConverter());
    }

    public Component getComponent() {
        return component;
    }
}
