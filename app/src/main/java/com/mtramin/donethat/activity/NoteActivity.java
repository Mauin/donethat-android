package com.mtramin.donethat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.mtramin.donethat.R;
import com.mtramin.donethat.data.Note;

import org.joda.time.DateTime;

import java.text.DateFormat;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by m.ramin on 7/7/15.
 */
public class NoteActivity extends BaseActivity {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";

    Note note;

    @Bind(R.id.note_title)
    TextView title;

    @Bind(R.id.note_content)
    TextView content;

    @Bind(R.id.note_date)
    TextView date;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        handleIntent(getIntent());
        displayNote();
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View v) {
        startActivity(EditNoteActivity.createIntent(this));
    }

    private void displayNote() {
        title.setText(note.title);
        content.setText(note.content);
        date.setText(DateUtils.formatSameDayTime(note.date.getMillis(), DateTime.now().getMillis(), DateFormat.DEFAULT, DateFormat.DEFAULT));
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        this.note = intent.getParcelableExtra(EXTRA_NOTE);
    }

    public static Intent createIntent(Context context, Note note) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

}
