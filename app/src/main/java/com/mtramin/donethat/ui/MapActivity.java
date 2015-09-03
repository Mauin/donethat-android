package com.mtramin.donethat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mtramin.donethat.R;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String EXTRA_LATITUDE = "EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "EXTRA_LONGITUDE";

    @Bind(R.id.map)
    MapView map;

    private BehaviorSubject<GoogleMap> observableMap = BehaviorSubject.create();
    private Subscription mapSubscription;

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, MapActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // TODO zoom to location
        map.getMapAsync(this);
        map.onCreate(savedInstanceState);

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

        map.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        map.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();

        map.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mapSubscription.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        map.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        observableMap.onNext(googleMap);
    }
}
