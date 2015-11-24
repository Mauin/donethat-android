package com.mtramin.donethat.ui.tripdetails;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.mtramin.donethat.databinding.FragmentTripDetailBinding;
import com.mtramin.donethat.ui.BaseFragment;
import com.mtramin.donethat.ui.EditNoteActivity;
import com.mtramin.donethat.ui.MainActivity;
import com.mtramin.donethat.ui.MapActivity;
import com.mtramin.donethat.ui.animator.RecyclerViewItemAnimator;
import com.mtramin.donethat.ui.note.NoteActivity;
import com.mtramin.donethat.util.LogUtil;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static rx.Observable.combineLatest;

/**
 * Created by m.ramin on 7/6/15.
 */
public class TripDetailFragment extends BaseFragment implements OnMapReadyCallback {

    public static final String EXTRA_TRIP_ID = "EXTRA_TRIP_ID";
    @Inject
    public DonethatApiService api;
    @Inject
    public DonethatCache storage;
    Trip trip;
    List<Note> notes;
    FragmentTripDetailBinding binding;
    private BehaviorSubject<GoogleMap> observableMap = BehaviorSubject.create();
    private PublishSubject<Boolean> observableMapLayoutStep = PublishSubject.create();
    private BehaviorSubject<Trip> observableTripDetails = BehaviorSubject.create();
    private CompositeSubscription subscription;
    private TripDetailAdapter adapter;
    private boolean showMap = false;

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
        binding = DataBindingUtil.bind(root);

        ButterKnife.bind(this, root);

        ((MainActivity) getActivity()).setToolbar(binding.toolbar);
        getActivity().setTitle("");

        adapter = new TripDetailAdapter(getActivity());
        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.list.setItemAnimator(new RecyclerViewItemAnimator(getContext()));

        showMap(savedInstanceState, true);
        return root;
    }

    private void showMap(Bundle savedInstanceState, boolean show) {
        this.showMap = show;

        if (show) {
            binding.tripDetailHeaderMap.setVisibility(View.VISIBLE);
            binding.tripDetailHeaderMap.onCreate(savedInstanceState);
            binding.tripDetailHeaderMap.getMapAsync(this);
            binding.tripDetailHeaderMap.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    observableMapLayoutStep.onNext(true);
                    binding.tripDetailHeaderMap.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            if (this.isResumed()) {
                binding.tripDetailHeaderMap.onResume();
            }
        } else {
            binding.tripDetailHeaderMap.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setTripDetails(trip);
        adapter.setData(trip, notes);
        subscribeToNoteClick();

        if (showMap) {
            binding.tripDetailHeaderMap.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (showMap) {
            binding.tripDetailHeaderMap.onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (showMap) {
            binding.tripDetailHeaderMap.onPause();
        }
        subscription.unsubscribe();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (showMap) {
            binding.tripDetailHeaderMap.onLowMemory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        subscription = new CompositeSubscription();

        if (showMap) {
            initMap();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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
                        .subscribe(
                                note -> {
                                    startActivity(NoteActivity.createIntent(getActivity(), note.id, trip.id));
                                },
                                throwable -> {
                                    LogUtil.logException(this, throwable);
                                }
                        )
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(latLng -> {
            // TODO pass marker locations
            startActivity(MapActivity.createIntent(getActivity()));
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
                    int markerCount = 0;
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
                            markerCount++;
                        }
                    }

                    if (markerCount == 0) {
                        return;
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 25));

                }, throwable -> LogUtil.logException(this, throwable));

        this.subscription.add(subscription);
    }
}
