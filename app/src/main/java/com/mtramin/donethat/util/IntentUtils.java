package com.mtramin.donethat.util;

import android.app.Activity;
import android.content.Intent;

import com.mtramin.donethat.ui.EditNoteActivity;

/**
 * TODO: JAVADOC
 */
public class IntentUtils {
    public static final int REQUEST_CODE_PICK_IMAGE = 1;

    public static void launchImagePicker(Activity activity) {
        Intent imagePicker = new Intent(Intent.ACTION_PICK);
        imagePicker.setType("image/*");
        activity.startActivityForResult(imagePicker, REQUEST_CODE_PICK_IMAGE);
    }
}
