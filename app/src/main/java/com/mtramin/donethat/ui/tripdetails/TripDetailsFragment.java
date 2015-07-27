package com.mtramin.donethat.ui.tripdetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.adapter.TripDetailAdapter;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.ui.BaseFragment;
import com.mtramin.donethat.ui.EditNoteActivity;
import com.mtramin.donethat.ui.MainActivity;
import com.mtramin.donethat.ui.note.NoteActivity;
import com.mtramin.donethat.util.LogUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/6/15.
 */
public class TripDetailsFragment extends BaseFragment {

    private static final String EXTRA_TRIP = "EXTRA_TRIP";

    Trip trip;

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.toolbar_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    public DonethatApiService api;
    private CompositeSubscription subscription;
    private TripDetailAdapter adapter;

    public TripDetailsFragment(Trip trip) {
        this.trip = trip;
    }

    public static TripDetailsFragment newInstance(Trip trip) {
        return new TripDetailsFragment(trip);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Application) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trip_detail, container, false);

        ButterKnife.bind(this, root);

        ((MainActivity) getActivity()).setToolbar(toolbar);

        collapsingToolbarLayout.setTitle(trip.title);

        adapter = new TripDetailAdapter(getActivity());
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        trip = intent.getParcelableExtra(EXTRA_TRIP);
    }

    @Override
    public void onStart() {
        super.onStart();

        subscription = new CompositeSubscription();

        subscribeToTripDetails();
        subscribeToNoteClick();
    }

    @Override
    public void onStop() {
        super.onStop();

        subscription.unsubscribe();
    }

    @OnClick(R.id.fab)
    public void onCreateNoteClicked() {
        startActivity(EditNoteActivity.createIntent(getActivity(), trip));
    }

    private void subscribeToNoteClick() {
        subscription.add(adapter.onNoteClicked()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                note -> {
                                    startActivity(NoteActivity.createIntent(getActivity(), note, trip));
                                },
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
