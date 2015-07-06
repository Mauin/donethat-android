package com.mtramin.donethat.di;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import com.mtramin.donethat.activity.BaseActivity;
import com.mtramin.donethat.activity.TripDetailActivity;
import com.mtramin.donethat.activity.TripsActivity;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.di.modules.ApiModule;
import com.mtramin.donethat.di.modules.ServiceModule;

/**
 * Created by m.ramin on 7/5/15.
 */
@dagger.Component(modules = {
        ApiModule.class,
        ServiceModule.class
    }
)
public interface Component {
    public void inject(TripsActivity activity);
    public void inject(TripDetailActivity activity);

    DonethatApi donethatApi();

    DonethatApiService donethatApiService();
}
