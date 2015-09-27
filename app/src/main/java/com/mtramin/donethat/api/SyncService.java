package com.mtramin.donethat.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.DonethatCache;

import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
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
    @Inject
    DonethatCache database;
    @Inject
    DonethatApi api;

    public SyncService(Context context) {
        ((Application) context.getApplicationContext()).getComponent().inject(this);
    }

    /**
     * Tries to sync everything with the backend by first downloading the last available data from the backend,
     * then updating all data that is outdated either on in the database or on the backend.
     *
     * @return Observable that will notify when the sync has been successfully completed
     */
    public Observable<List<Trip>> syncAll() {
        return Observable.combineLatest(
                api.getTrips(),
                database.getTrips(),
                this::combineTripLists
        )
                .flatMapIterable(trips -> trips)
                .flatMap(trip -> {
                    switch (calculateSyncActionForTrip(trip)) {
                        case UPLOAD:
                            return api.createTrip(trip.trip);
                        case SYNC:
                        case DOWNLOAD:
                            return syncTrip(trip.trip.id);
                        case NO_ACTION:
                            return Observable.just(null);
                        default:
                            return Observable.just(null);
                    }
                })
                .toList()
                .flatMap(o -> database.getTrips());
    }

    @NonNull
    private List<SyncTrip> combineTripLists(List<Trip> remote, List<Trip> local) {
        List<SyncTrip> combined = new ArrayList<>();
        for (Trip remoteTrip : remote) {
            combined.add(new SyncTrip(TripSource.REMOTE, remoteTrip));
        }

        for (Trip localTrip : local) {
            if (!remote.contains(localTrip)) {
                combined.add(new SyncTrip(TripSource.LOCAL, localTrip));
            }
        }
        return combined;
    }

    public Observable<Void> syncTrip(UUID tripId) {
        return Observable.combineLatest(
                api.getTrip(tripId),
                Observable.just(database.getTripDetails(tripId)),
                TripDetailSync::new
        ).flatMap(tripDetailSync -> {
            switch (tripDetailSync.action) {
                case UPLOAD:
                    return api.createTrip(tripDetailSync.local);
                case SYNC:
                    database.storeTrip(tripDetailSync.remote);
                    return Observable.just(null);
                case DOWNLOAD:
                case NO_ACTION:
                    List<Note> localNotes = database.getNotesForTrip(tripId);
                    return updateNotes(tripId, combineNotes(localNotes, tripDetailSync.remote.notes), localNotes, tripDetailSync.remote.notes);
                default:
                    return Observable.just(null);
            }
        });
    }

    private Set<Note> combineNotes(List<Note> localNotes, List<Note> remoteNotes) {
        // Check all notes from local and remote
        Set<Note> notes = new HashSet<>();
        notes.addAll(localNotes);
        notes.addAll(remoteNotes);
        return notes;
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

    private SyncAction calculateSyncActionForTrip(SyncTrip syncTrip) {
        Trip stored = database.getTripDetails(syncTrip.trip.id);

        if (syncTrip.source.equals(TripSource.LOCAL)) {
            return SyncAction.UPLOAD;
        }

        // Does not exist locally
        if (stored == null) {
            return SyncAction.DOWNLOAD;
        }

        int compared = DateTimeComparator.getInstance().compare(stored.updated, syncTrip.trip.updated);
        if (compared == 0) {
            return SyncAction.NO_ACTION;
        } else {
            return SyncAction.SYNC;
        }
    }

    private enum SyncAction {
        UPLOAD, SYNC, DOWNLOAD, NO_ACTION
    }

    private enum TripSource {
        LOCAL, REMOTE
    }

    private class TripDetailSync {
        Trip local;
        Trip remote;
        SyncAction action;

        public TripDetailSync(Trip remote, Trip local) {
            this.remote = remote;
            this.local = local;
            this.action = determineSyncAction();
        }

        private SyncAction determineSyncAction() {
            if (local == null) {
                return SyncAction.DOWNLOAD;
            }

            if (remote == null) {
                return SyncAction.UPLOAD;
            }

            return SyncAction.SYNC;
        }
    }

    private class SyncTrip {
        Trip trip;
        TripSource source;

        public SyncTrip(TripSource source, Trip trip) {
            this.source = source;
            this.trip = trip;
        }
    }
}
