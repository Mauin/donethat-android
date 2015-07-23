package com.mtramin.donethat.ui.trips;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.adapter.TripsAdapter;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.ui.BaseActivity;
import com.mtramin.donethat.ui.CreateTripActivity;
import com.mtramin.donethat.ui.LoginActivity;
import com.mtramin.donethat.ui.tripdetails.TripDetailActivity;
import com.mtramin.donethat.util.AccountUtil;
import com.mtramin.donethat.util.LogUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TripsActivity extends BaseActivity {

    @Bind(R.id.list)
    RecyclerView list;

    @Inject
    DonethatApiService apiService;

    private CompositeSubscription subscription;

    private TripsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Application) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_trips);

        adapter = new TripsAdapter();
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (!AccountUtil.hasAccount(this)) {
            startActivity(LoginActivity.createIntent(this));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        subscription = new CompositeSubscription();

        subscribeToTrips();
        subscribeToTripList();
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        startActivity(CreateTripActivity.createIntent(this));
    }

    private void subscribeToTripList() {
        subscription.add(adapter.onTripClick()
                .subscribe(
                        trip -> {
                            Intent intent = TripDetailActivity.getIntent(this, trip);
                            startActivity(intent);
                        }, throwable -> LogUtil.logException(this, throwable)
                ));
    }

    @Override
    protected void onStop() {
        subscription.unsubscribe();

        super.onStop();
    }

    private void subscribeToTrips() {
        subscription.add(apiService.getTrips()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                adapter::addData,
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, TripsActivity.class);
    }
}
