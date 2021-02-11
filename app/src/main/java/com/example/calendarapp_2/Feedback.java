package com.example.calendarapp_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.calendarapp_2.firebase.FirebaseCallback;
import com.example.calendarapp_2.firebase.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feedback extends AppCompatActivity {

    FirebaseDatabase mRootNode;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        String date = "";
        String time = "";
        String event = "";

        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");
            event = intent.getStringExtra("event");
        }

        final String finalDate = date;
        final String finalEvent = event;
        final String finalTime = time;
        final EditText feedback = findViewById(R.id.feedback_edit_text);

        findViewById(R.id.feedback_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRequestCode(finalDate, finalEvent, finalTime, new FirebaseCallback() {
                    @Override
                    public void onSuccess(Events events) {
                        FirebaseHelper.UpdateEventSaveFeedback(mDatabaseReference, finalDate, finalEvent, finalTime,
                                feedback.getText().toString(), new FirebaseFinishListener() {
                            @Override
                            public void onFinish() {
                                Toast.makeText(Feedback.this, "Feedback Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }


    private void getRequestCode(String date, final String event, String time, FirebaseCallback callback) {
        mRootNode = FirebaseDatabase.getInstance();
        mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.ReadIDEvents(mDatabaseReference, date, event, time, callback);
    }
}