package com.mtramin.donethat;

import com.mtramin.donethat.di.DaggerComponent;
import com.mtramin.donethat.di.modules.ApiModule;
import com.mtramin.donethat.di.Component;
import com.mtramin.donethat.di.modules.ApplicationModule;
import com.mtramin.donethat.di.modules.ServiceModule;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by m.ramin on 7/5/15.
 */
public class Application extends android.app.Application {

    Component component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerComponent.builder()
                .apiModule(new ApiModule())
                .serviceModule(new ServiceModule())
                .applicationModule(new ApplicationModule(this))
                .build();

        JodaTimeAndroid.init(this);
    }

    public Component getComponent() {
        return component;
    }
}
