package com.mtramin.donethat.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.adapter.TripsAdapter;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.util.LogUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

public class TripsActivity extends BaseActivity {

    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    DonethatApiService apiService;

    private CompositeSubscription subscription;

    private TripsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Application) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_trips);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        adapter = new TripsAdapter();
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onStart() {
        super.onStart();

        subscription = new CompositeSubscription();

        subscribeToTrips();
    }

    @Override
    protected void onStop() {
        subscription.unsubscribe();

        super.onStop();
    }

    private void subscribeToTrips() {
        subscription.add(apiService.getTrips()
                        .subscribe(adapter::addData,
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }
}
