package com.mtramin.donethat.data.persist;

import android.util.Log;

import com.mtramin.donethat.data.mapper.NoteMapper;
import com.mtramin.donethat.data.mapper.TripMapper;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.realm.NoteDto;
import com.mtramin.donethat.data.persist.realm.TripDto;
import com.mtramin.donethat.util.comparator.NoteComparator;
import com.mtramin.donethat.util.comparator.TripComparator;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by m.ramin on 8/3/15.
 */
public class DonethatCache {

    public DonethatCache() {
    }

    public Observable<List<Trip>> getTrips() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TripDto> result = realm.where(TripDto.class)
                .findAll();


        List<Trip> trips = TripMapper.toTripList(result);
        Collections.sort(trips, new TripComparator());
        return Observable.just(Collections.unmodifiableList(trips));
    }

    public void storeTrip(Trip trip) {
        transaction(realm -> {
            realm.copyToRealmOrUpdate(TripMapper.toTripDto(trip));

            if (trip.notes == null) {
                return;
            }
            for (Note note : trip.notes) {
                realm.copyToRealmOrUpdate(NoteMapper.createNoteDto(trip.id, note));
            }
        });
    }

    public void storeNote(UUID tripId, Note note) {
        note.updated = DateTime.now();

        Log.e("TEST", "storeNote before " + getTripDetails(tripId).updated);

        Realm realm = Realm.getDefaultInstance();
        TripDto trip = realm.where(TripDto.class).equalTo("id", tripId.toString()).findFirst();
        realm.beginTransaction();
        trip.setUpdated(DateTime.now().getMillis());
        realm.commitTransaction();
        realm.close();

        Log.e("TEST", "storeNote after " + getTripDetails(tripId).updated);
    }

    public List<Note> getNotesForTrip(UUID id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<NoteDto> results = realm.where(NoteDto.class).equalTo("tripId", id.toString()).findAll();

        List<Note> result = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return result;
        }

        for (NoteDto dto : results) {
            result.add(NoteMapper.toNote(dto));
        }

        Collections.sort(result, new NoteComparator());

        return Collections.unmodifiableList(result);
    }

    public Note getNote(UUID id) {
        Realm realm = Realm.getDefaultInstance();
        NoteDto result = realm.where(NoteDto.class).equalTo("id", id.toString()).findFirst();

        if (result == null) {
            return null;
        }

        Note note = NoteMapper.toNote(result);
        realm.close();
        return note;
    }

    public Trip getTripDetails(UUID id) {
        Realm realm = Realm.getDefaultInstance();
        TripDto result = realm.where(TripDto.class).equalTo("id", id.toString()).findFirst();

        if (result == null) {
            return null;
        }

        Trip trip = TripMapper.toTrip(result);
        realm.close();
        return trip;
    }

    private void transaction(RealmTransaction transaction) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        transaction.performTransaction(realm);
        realm.commitTransaction();
        realm.close();
    }

    interface RealmTransaction {
        void performTransaction(Realm realm);
    }
}
