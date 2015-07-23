package com.mtramin.donethat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.data.Note;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.util.LogUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/8/15.
 */
public class EditNoteActivity extends BaseActivity {

    public static final String EXTRA_TRIP = "extra_trip";

    @Bind(R.id.edit_note_title)
    EditText editTitle;

    @Bind(R.id.edit_note_content)
    EditText editContent;

    CompositeSubscription subscription;

    Trip trip;

    @Inject
    DonethatApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Application) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_note_edit);

        parseIntent();

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

    public void parseIntent() {
        Intent intent = getIntent();
        this.trip = intent.getParcelableExtra(EXTRA_TRIP);
    }

    public static Intent createIntent(Context context, Trip trip) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(EXTRA_TRIP, trip);
        return intent;
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        String title = editTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            return;
        }

        Note note = new Note(title, editContent.getText().toString(), new LatLng(0, 0));

        createNote(note);
    }

    private void createNote(Note note) {
        subscription.add(api.createNote(this.trip.id, note)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                aVoid -> finish(),
                                throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }
}
