package com.example.calendarapp_2.firebase;

import com.example.calendarapp_2.Events;

public interface FirebaseCallback {
    void onSuccess(Events events);

    interface FirebaseFinishListener{
        void onFinish();
    }
}


