package com.mtramin.donethat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.mtramin.donethat.R;
import com.mtramin.donethat.data.Note;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.text.DateFormat;

import butterknife.Bind;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        handleIntent(getIntent());
        displayNote();
    }

    private void displayNote() {
        title.setText(note.title);
        content.setText(note.content);
        date.setText(DateUtils.formatSameDayTime(note.note_date.getMillis(), DateTime.now().getMillis(), DateFormat.DEFAULT, DateFormat.DEFAULT));
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
