package com.mtramin.donethat.di.modules;

import com.mtramin.donethat.api.interfaces.DonethatApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

/**
 * Created by m.ramin on 7/5/15.
 */
@Module
public class ApiModule {

    private static final String ENDPOINT_DONETHAT = "https://donethat.herokuapp.com";

    @Provides
    DonethatApi provideDonethatApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT_DONETHAT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter.create(DonethatApi.class);
    }

}
