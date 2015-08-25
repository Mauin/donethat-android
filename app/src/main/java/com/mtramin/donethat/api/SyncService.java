package com.mtramin.donethat.api;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.DonethatCache;

import org.joda.time.DateTimeComparator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by m.ramin on 8/23/15.
 */
public class SyncService {

    private static final String TAG = SyncService.class.getName();
    private Context context;

    @Inject
    DonethatCache database;

    @Inject
    DonethatApi api;

    public SyncService(Context context) {
        this.context = context;

        ((Application) context.getApplicationContext()).getComponent().inject(this);
    }

    /**
     * Tries to sync everything with the backend by first downloading the last available data from the backend,
     * then updating all data that is outdated either on in the database or on the backend.
     *
     * @return Observable that will notify when the sync has been successfully completed
     */
    public Observable<List<Trip>> syncAll() {
        return api.getTrips()
                .flatMapIterable(trips -> trips)
                .flatMap(trip -> Observable.just(Pair.create(trip.id, isTripUpToDate(trip))))
                .flatMap(pair -> {
                    UUID tripId = pair.first;
                    boolean needsUpdate = !pair.second;

                    if (needsUpdate) {
                        return syncTrip(tripId);
                    } else {
                        return Observable.just(null);
                    }
                })
                .toList()
                .flatMap(o -> database.getTrips());
    }

    public Observable<Void> syncTrip(UUID tripId) {
        return api.getTrip(tripId)
                .flatMap(remote -> {
                    Trip local = database.getTripDetails(tripId);

                    if (local == null) {
                        database.storeTrip(remote);
                        return Observable.just(null);
                    }

                    List<Note> localNotes = database.getNotesForTrip(tripId);

                    // Check all notes from local and remote
                    Set<Note> notes = new HashSet<>();
                    notes.addAll(localNotes);
                    notes.addAll(remote.notes);

                    return updateNotes(tripId, notes, localNotes, remote.notes);
                });
    }

    private Observable<Void> updateNotes(UUID tripId, Set<Note> notes, List<Note> local, List<Note> remote) {
        return Observable.just(notes)
                .flatMapIterable(set -> set)
                .flatMap(note -> {
                    if (local.contains(note)) {

                        if (remote.contains(note)) {
                            // Note in both, update
                            return updateNote(tripId, note, remote, local);
                        }

                        // Note not in remote, upload note
                        return api.createNote(note.id, note);
                    }

                    // Note not in local, store note
                    database.storeNote(tripId, note);
                    return Observable.just(null);
                })
                .toList()
                .flatMap(o -> Observable.just(null));
    }

    private Observable<Void> updateNote(UUID tripId, Note note, List<Note> remoteNotes, List<Note> localNotes) {
        // We expect the note to be in both lists
        Note remote = remoteNotes.get(remoteNotes.indexOf(note));
        Note local = localNotes.get(localNotes.indexOf(note));

        int compare = DateTimeComparator.getInstance().compare(remote.updated, local.updated);

        if (compare == 0) {
            // Nothing to do here
            return Observable.just(null);
        } else if (compare < 0) {
            // Local wins
            return api.putNote(tripId, note.id, note);
        } else {
            // Remote wins
            database.storeNote(tripId, note);
            return Observable.just(null);
        }
    }

    private boolean isTripUpToDate(Trip trip) {
        Trip stored = database.getTripDetails(trip.id);

        // Does not exist
        if (stored == null) {
            return false;
        }

        int compared = DateTimeComparator.getInstance().compare(stored.updated, trip.updated);
        return (compared == 0);
    }
}
