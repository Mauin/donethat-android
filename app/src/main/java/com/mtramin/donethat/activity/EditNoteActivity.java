package com.mtramin.donethat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mtramin.donethat.R;

/**
 * Created by m.ramin on 7/8/15.
 */
public class EditNoteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_edit);

    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        return intent;
    }
}
