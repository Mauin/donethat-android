package com.mtramin.donethat.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mtramin.donethat.R;
import com.mtramin.donethat.databinding.ActivityMapBinding;

import butterknife.OnClick;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String EXTRA_LATITUDE = "EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "EXTRA_LONGITUDE";

    private ActivityMapBinding binding;

    private BehaviorSubject<GoogleMap> observableMap = BehaviorSubject.create();
    private Subscription mapSubscription;

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, MapActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);

        // TODO zoom to location
        binding.map.getMapAsync(this);
        binding.map.onCreate(savedInstanceState);

    }

    @OnClick(R.id.fab)
    public void onSelectClicked() {
        mapSubscription = observableMap
                .subscribe(googleMap -> {
                    finishWithResult(googleMap.getCameraPosition().target);
                });
    }

    private void finishWithResult(LatLng target) {
        Intent result = new Intent();
        result.putExtra(EXTRA_LATITUDE, target.latitude);
        result.putExtra(EXTRA_LONGITUDE, target.longitude);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.map.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        binding.map.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();

        binding.map.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mapSubscription != null) {
            mapSubscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding.map.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        observableMap.onNext(googleMap);
    }
}
