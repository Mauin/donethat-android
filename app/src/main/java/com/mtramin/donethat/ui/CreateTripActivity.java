package com.mtramin.donethat.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.DonethatCache;
import com.mtramin.donethat.databinding.ActivityTripCreateBinding;
import com.mtramin.donethat.service.BackgroundSyncService;

import org.joda.time.DateTime;

import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/16/15.
 */
public class CreateTripActivity extends BaseActivity {

    @Inject
    DonethatApiService api;

    @Inject
    DonethatCache storage;


    private CompositeSubscription subscription;
    private ActivityTripCreateBinding binding;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, CreateTripActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Application) getApplication()).getComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_create);
        ButterKnife.bind(this);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create a new trip");
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscription = new CompositeSubscription();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        String title = binding.createTripTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            return;
        }
        Trip trip = new Trip.Builder()
                .title(title)
                .content(binding.createTripContent.getText().toString())
                .date(DateTime.now())
                .updated(DateTime.now())
                .id(UUID.randomUUID())
                .build();

        storeTrip(trip);
    }

    private void storeTrip(Trip trip) {
        storage.storeTrip(trip);
        startService(BackgroundSyncService.tripSyncIntent(this, trip.id));
        finish();
    }
}
