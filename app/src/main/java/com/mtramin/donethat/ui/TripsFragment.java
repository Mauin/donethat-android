package com.mtramin.donethat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.adapter.TripsAdapter;
import com.mtramin.donethat.api.SyncService;
import com.mtramin.donethat.util.LogUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/27/15.
 */
public class TripsFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.list)
    RecyclerView list;

    @Inject
    SyncService syncService;

    private CompositeSubscription subscription;

    private TripsAdapter adapter;

    public TripsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Application) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trips, container, false);
        ButterKnife.bind(this, root);

        ((MainActivity) getActivity()).setToolbar(toolbar);
        getActivity().setTitle("You have done this");

        adapter = new TripsAdapter(getContext());
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        subscription = new CompositeSubscription();

        subscribeToTrips();
        subscribeToTripList();
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        startActivity(CreateTripActivity.createIntent(getActivity()));
    }

    private void subscribeToTripList() {
        subscription.add(adapter.onTripClick()
                .subscribe(
                        ((FragmentCallbacks) getActivity())::onShowTripDetails,
                        throwable -> LogUtil.logException(this, throwable)
                ));
    }


    private void subscribeToTrips() {
        subscription.add(syncService.syncAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                adapter::setData,
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }

    @Override
    public void onStop() {
        super.onStop();

        subscription.unsubscribe();
    }

    public static TripsFragment newInstance() {
        return new TripsFragment();
    }
}
