package com.mtramin.donethat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.util.LogUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/16/15.
 */
public class CreateTripActivity extends BaseActivity {

    @Bind(R.id.edit_note_title)
    EditText editTitle;

    @Inject
    DonethatApiService api;

    private CompositeSubscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Application) getApplication()).getComponent().inject(this);

        // TODO Different layout and behavior
        setContentView(R.layout.activity_note_edit);

    }

    @Override
    protected void onStart() {
        super.onStart();
        subscription = new CompositeSubscription();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, CreateTripActivity.class);
        return intent;
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        String title = editTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            return;
        }
        Trip trip = new Trip.Builder().title(title).build();

        createTrip(trip);
    }

    private void createTrip(Trip trip) {
        subscription.add(api.createTrip(trip)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                aVoid -> finish(),
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }
}
