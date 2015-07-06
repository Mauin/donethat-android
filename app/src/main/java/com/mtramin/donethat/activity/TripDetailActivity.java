package com.mtramin.donethat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.adapter.TripDetailAdapter;
import com.mtramin.donethat.adapter.TripsAdapter;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.util.LogUtil;

import java.util.UUID;

import javax.inject.Inject;

import butterknife.Bind;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/6/15.
 */
public class TripDetailActivity extends BaseActivity {

    Trip trip = Trip.Demo.trip();

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Inject
    public DonethatApiService api;
    private CompositeSubscription subscription;
    private TripDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Application) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_trip_detail);

        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(trip.title);

        adapter = new TripDetailAdapter();
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public static Intent getIntent(Context context, Trip trip) {
        Intent intent = new Intent(context, TripDetailActivity.class);
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();

        subscription = new CompositeSubscription();

        subscribeToTripDetails();
    }

    @Override
    protected void onStop() {
        super.onStop();

        subscription.unsubscribe();
    }

    private void subscribeToTripDetails() {
        // TODO call correct endpoint
        subscription.add(api.getTrip(UUID.randomUUID())
                        .subscribe(
                                adapter::setData,
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }
}
