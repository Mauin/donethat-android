package com.mtramin.donethat.ui.tripdetails;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.adapter.TripDetailAdapter;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.DonethatCache;
import com.mtramin.donethat.ui.BaseFragment;
import com.mtramin.donethat.ui.EditNoteActivity;
import com.mtramin.donethat.ui.MainActivity;
import com.mtramin.donethat.ui.animator.AppearAnimator;
import com.mtramin.donethat.ui.note.NoteActivity;
import com.mtramin.donethat.util.LogUtil;
import com.mtramin.donethat.util.PermissionUtil;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static rx.Observable.combineLatest;

/**
 * Created by m.ramin on 7/6/15.
 */
public class TripDetailFragment extends BaseFragment implements OnMapReadyCallback {

    public static final String EXTRA_TRIP_ID = "EXTRA_TRIP_ID";

    Trip trip;
    List<Note> notes;

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.toolbar_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.trip_detail_header_map)
    MapView mapView;

    @Inject
    public DonethatApiService api;

    @Inject
    public DonethatCache storage;

    private BehaviorSubject<GoogleMap> observableMap = BehaviorSubject.create();
    private PublishSubject<Boolean> observableMapLayoutStep = PublishSubject.create();
    private BehaviorSubject<Trip> observableTripDetails = BehaviorSubject.create();
    private CompositeSubscription subscription;
    private TripDetailAdapter adapter;
    private boolean showMap = false;
    private Snackbar permissionSnackbar;

    public TripDetailFragment() {
        // Default constructor
    }

    public static TripDetailFragment newInstance(UUID tripId) {
        TripDetailFragment fragment = new TripDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_TRIP_ID, tripId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Application) getActivity().getApplication()).getComponent().inject(this);

        observableMap = BehaviorSubject.create();
        observableTripDetails = BehaviorSubject.create();

        handleIntent();
    }

    private void handleIntent() {
        if (getArguments().isEmpty()) {
            throw new IllegalStateException("Arguments should contain trips");
        }
        UUID tripId = (UUID) getArguments().getSerializable(EXTRA_TRIP_ID);
        this.trip = storage.getTripDetails(tripId);
        this.notes = storage.getNotesForTrip(tripId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trip_detail, container, false);

        ButterKnife.bind(this, root);

        ((MainActivity) getActivity()).setToolbar(toolbar);
        getActivity().setTitle("");

        adapter = new TripDetailAdapter(getActivity());
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.setItemAnimator(new AppearAnimator());

        subscription = new CompositeSubscription();

        if (PermissionUtil.shouldRequestPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // We don't have the permission

            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show description
                this.permissionSnackbar = Snackbar.make(root, "We want to show you a map of your trip. We need this permission for that.", Snackbar.LENGTH_INDEFINITE);
                this.permissionSnackbar.show();
            }

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            showMap(null, false);
            return root;
        }

        showMap(savedInstanceState, true);
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (this.permissionSnackbar != null) {
            this.permissionSnackbar.dismiss();
        }

        if (permissions.length == 0) {
            // No permissions granted/denied
            return;
        }

        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {
                if (PermissionUtil.permissionGranted(grantResults)) {
                    showMap(null, true);
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showMap(Bundle savedInstanceState, boolean show) {
        this.showMap = show;

        if (show) {
            mapView.setVisibility(View.VISIBLE);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    observableMapLayoutStep.onNext(true);
                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            if (this.isResumed()) {
                mapView.onResume();
            }
        } else {
            mapView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setTripDetails(trip);
        adapter.setData(trip, notes);

        if (showMap) {
            mapView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (showMap) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (showMap) {
            mapView.onPause();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (showMap) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        subscribeToNoteClick();
        if (showMap) {
            initMap();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        subscription.unsubscribe();
    }

    @OnClick(R.id.fab)
    public void onCreateNoteClicked() {
        startActivity(EditNoteActivity.createIntent(getActivity(), trip.id));
    }

    private void setTripDetails(Trip details) {
        observableTripDetails.onNext(details);
    }

    private void subscribeToNoteClick() {
        subscription.add(adapter.onNoteClicked()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                note -> startActivity(NoteActivity.createIntent(getActivity(), note.id, trip.id)),
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(latLng -> {
            // TODO launch map view with markers (or just Google Maps?)
            // TODO check if GMaps accepts marker locations intent
        });

        observableMap.onNext(googleMap);
    }

    private void initMap() {
        Subscription subscription = combineLatest(
                        observableMap,
                        observableMapLayoutStep,
                        (googleMap, o) -> googleMap
                )
                        .subscribe(map -> {

                            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
                            for (Note note : this.notes) {
                                LatLng location = note.location;
                                if (location != null) {
                                    boundsBuilder.include(location);
                                    MarkerOptions marker = new MarkerOptions();
                                    marker.position(location);
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location));
                                    marker.anchor(0.5f, 0.5f);
                                    map.addMarker(marker);
                                }
                            }

                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 25));

                        }, throwable -> LogUtil.logException(this, throwable));

        this.subscription.add(subscription);
    }

    public class MapNotReadyException extends RuntimeException {
        public MapNotReadyException() {
        }
    }
}
