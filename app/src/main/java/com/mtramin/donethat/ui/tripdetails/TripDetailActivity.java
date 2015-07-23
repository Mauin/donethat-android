package com.mtramin.donethat.ui.tripdetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.adapter.TripDetailAdapter;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.ui.BaseActivity;
import com.mtramin.donethat.ui.EditNoteActivity;
import com.mtramin.donethat.ui.note.NoteActivity;
import com.mtramin.donethat.util.LogUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/6/15.
 */
public class TripDetailActivity extends BaseActivity {

    private static final String EXTRA_TRIP = "EXTRA_TRIP";

    Trip trip;

    @Bind(R.id.list)
    RecyclerView list;

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

        handleIntent(getIntent());

        collapsingToolbarLayout.setTitle(trip.title);

        adapter = new TripDetailAdapter(this);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        trip = intent.getParcelableExtra(EXTRA_TRIP);
    }

    public static Intent getIntent(Context context, Trip trip) {
        Intent intent = new Intent(context, TripDetailActivity.class);
        intent.putExtra(EXTRA_TRIP, trip);
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();

        subscription = new CompositeSubscription();

        subscribeToTripDetails();
        subscribeToNoteClick();
    }

    @Override
    protected void onStop() {
        super.onStop();

        subscription.unsubscribe();
    }

    @OnClick(R.id.fab)
    public void onCreateNoteClicked() {
        startActivity(EditNoteActivity.createIntent(this, trip));
    }

    private void subscribeToNoteClick() {
        subscription.add(adapter.onNoteClicked()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                note -> startActivity(NoteActivity.createIntent(this, note, trip)),
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }

    private void subscribeToTripDetails() {
        subscription.add(api.getTrip(trip.id)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                adapter::setData,
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }
}
