package com.example.calendarapp_2.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.calendarapp_2.Events;
import com.example.calendarapp_2.firebase.model.EventsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    public static final String EVENTS_REFERENCE = "Events";

    public static void SaveEvent(DatabaseReference reference, String id, String event,
                                 String description, String time, String date,
                                 String month, String year, String progress, String notif) {
        reference.child(id).setValue(new EventsModel(id, event, description, time, date, month, year, progress, notif));
    }

    public static void ReadEventsPerMonth(DatabaseReference reference, final String month, final String year, final FirebaseCallback callback, final FirebaseCallback.FirebaseFinishListener listener) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventsModel model = dataSnapshot.getValue(EventsModel.class);
                    if (model.getMonth().equals(month) && model.getYear().equals(year)) {
                        String event = model.getEvent();
                        String time = model.getTime();
                        String date = model.getDate();
                        String month = model.getMonth();
                        String Year = model.getYear();
                        String description = model.getDescription();
                        String id = model.getId();
                        String notify = model.getNotif();
                        String progress = model.getProgress();
                        Events events = new Events(event, description, time, date, month, Year, id, notify, progress);
                        callback.onSuccess(events);
                    }
                }
                listener.onFinish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("test", error.getMessage());
            }
        });
    }

    public static void ReadEvents(DatabaseReference reference, final String date, final FirebaseCallback callback, final FirebaseCallback.FirebaseFinishListener listener) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventsModel model = dataSnapshot.getValue(EventsModel.class);
                    if (model.getDate().equals(date)) {
                        String event = model.getEvent();
                        String time = model.getTime();
                        String date = model.getDate();
                        String month = model.getMonth();
                        String Year = model.getYear();
                        String description = model.getDescription();
                        String id = model.getId();
                        String notify = model.getNotif();
                        String progress = model.getProgress();
                        Events events = new Events(event, description, time, date, month, Year, id, notify, progress);
                        callback.onSuccess(events);
                    }
                    listener.onFinish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("test", error.getMessage());
            }
        });
    }

    public static void ReadIDEvents(DatabaseReference reference, final String date, final String event, final String time, final FirebaseCallback callback) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventsModel model = dataSnapshot.getValue(EventsModel.class);
                    if (model.getDate().equals(date) && model.getEvent().equals(event) && model.getTime().equals(time)) {
                        String event = model.getEvent();
                        String time = model.getTime();
                        String date = model.getDate();
                        String month = model.getMonth();
                        String Year = model.getYear();
                        String id = model.getId();
                        String description = model.getDescription();
                        String progress = model.getProgress();
                        String notify = model.getNotif();
                        Events events = new Events(event, description, time, date, month, Year, id, notify, progress);
                        callback.onSuccess(events);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("test", error.getMessage());
            }
        });
    }

    public static void UpdateEvent(final DatabaseReference reference, final String date,
                                   final String event, final String time, final String newNotif, final FirebaseCallback.FirebaseFinishListener listener) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventsModel model = dataSnapshot.getValue(EventsModel.class);
                    if (model.getDate().equals(date) && model.getEvent().equals(event) && model.getTime().equals(time)) {
                        reference.child(model.getId()).child("notif").setValue(newNotif);
                        listener.onFinish();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("test", error.getMessage());
            }
        });
    }
    public static void UpdateEvent2(final DatabaseReference reference, final String date,
                                   final String event, final String time, final String newProgress, final FirebaseCallback.FirebaseFinishListener listener) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventsModel model = dataSnapshot.getValue(EventsModel.class);
                    if (model.getDate().equals(date) && model.getEvent().equals(event) && model.getTime().equals(time)) {
                        reference.child(model.getId()).child("progress").setValue(newProgress);
                        listener.onFinish();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("test", error.getMessage());
            }
        });
    }


    public static void DeleteEvent(final DatabaseReference reference, final String date, final String event, final String time) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventsModel model = dataSnapshot.getValue(EventsModel.class);
                    if (model.getDate().equals(date) && model.getEvent().equals(event) && model.getTime().equals(time)) {
                        reference.child(model.getId()).removeValue();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("test", error.getMessage());
            }
        });
    }

}
