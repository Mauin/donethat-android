package com.mtramin.donethat.data.persist;

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
import java.util.Date;
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

            for (Note note : trip.notes) {
                realm.copyToRealmOrUpdate(NoteMapper.createNoteDto(trip.id, note));
            }
        });
    }

    public void storeNote(UUID tripId, Note note) {
        note.updated = DateTime.now();
        transaction(realm -> realm.copyToRealmOrUpdate(NoteMapper.createNoteDto(tripId, note)));
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
