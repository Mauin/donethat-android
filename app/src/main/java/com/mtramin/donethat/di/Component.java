package com.mtramin.donethat.di;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import com.google.gson.Gson;
import com.mtramin.donethat.activity.BaseActivity;
import com.mtramin.donethat.activity.CreateTripActivity;
import com.mtramin.donethat.activity.LoginActivity;
import com.mtramin.donethat.activity.TripDetailActivity;
import com.mtramin.donethat.activity.TripsActivity;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.api.TwitterAuthService;
import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.di.modules.ApiModule;
import com.mtramin.donethat.di.modules.ApplicationModule;
import com.mtramin.donethat.di.modules.ServiceModule;
import com.mtramin.donethat.di.modules.UtilModule;

import javax.inject.Singleton;

/**
 * Created by m.ramin on 7/5/15.
 */
@Singleton
@dagger.Component(modules = {
        ApplicationModule.class,
        ApiModule.class,
        ServiceModule.class,
        UtilModule.class
    }
)
public interface Component {
    public void inject(TripsActivity activity);
    public void inject(TripDetailActivity activity);
    public void inject(LoginActivity activity);
    public void inject(CreateTripActivity activity);

    public void inject(TwitterAuthService service);
    public void inject(DonethatApiService service);

    DonethatApi donethatApi();

    DonethatApiService donethatApiService();

    TwitterAuthService twitterAuthService();


    Context context();
}
