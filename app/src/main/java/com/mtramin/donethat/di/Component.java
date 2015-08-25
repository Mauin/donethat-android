package com.mtramin.donethat.di;

import android.content.Context;

import com.mtramin.donethat.adapter.TripsAdapter;
import com.mtramin.donethat.api.SyncService;
import com.mtramin.donethat.data.persist.DonethatCache;
import com.mtramin.donethat.service.BackgroundSyncService;
import com.mtramin.donethat.ui.CreateTripActivity;
import com.mtramin.donethat.ui.EditNoteActivity;
import com.mtramin.donethat.ui.LoginActivity;
import com.mtramin.donethat.ui.TripsFragment;
import com.mtramin.donethat.ui.note.NoteActivity;
import com.mtramin.donethat.ui.tripdetails.TripDetailFragment;
import com.mtramin.donethat.ui.MainActivity;
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
    public void inject(MainActivity activity);
    public void inject(TripsFragment fragment);
    public void inject(TripDetailFragment activity);
    public void inject(LoginActivity activity);
    public void inject(CreateTripActivity activity);
    public void inject(EditNoteActivity activity);
    public void inject(NoteActivity activity);

    public void inject(TripsAdapter adapter);
    public void inject(TwitterAuthService service);
    public void inject(DonethatApiService service);
    public void inject(SyncService service);
    public void inject(BackgroundSyncService service);
    public void inject(DonethatCache cache);

    DonethatApi donethatApi();

    DonethatApiService donethatApiService();

    TwitterAuthService twitterAuthService();


    Context context();
}
