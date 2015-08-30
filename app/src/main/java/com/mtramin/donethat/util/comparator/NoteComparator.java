package com.mtramin.donethat.util.comparator;

import com.mtramin.donethat.data.model.Note;

import org.joda.time.DateTimeComparator;

import java.util.Comparator;

/**
 * TODO: JAVADOC
 */
public class NoteComparator implements Comparator<Note> {
    @Override
    public int compare(Note lhs, Note rhs) {
        return DateTimeComparator.getInstance().compare(rhs.date, lhs.date);
    }
}
