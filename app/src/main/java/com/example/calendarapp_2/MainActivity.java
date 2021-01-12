package com.example.calendarapp_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity{
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CustomCalendarView customCalendarView;
    CustomCalendarView_2 customCalendarView_2;
    Button signIn;
    EditText Email,Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        signIn = findViewById(R.id.login_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signInWithEmailAndPassword(Email.getText().toString(),Password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        //checkUserAccessLevel(authResult.getUser().getUid());
                        setContentView(R.layout.activity_main);
                        customCalendarView = findViewById(R.id.custom_calendar_view);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }
    /*private void checkUserAccessLevel(String uid){
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","onSuccess: " + documentSnapshot.getData());
                if(documentSnapshot.getString("admin") != null){
                    setContentView(R.layout.activity_main);
                    customCalendarView = findViewById(R.id.custom_calendar_view);
                }else{
                    setContentView(R.layout.activity_main);
                    customCalendarView_2 = findViewById(R.id.custom_calendar_view_2);
                }
            }
        });
    } */


}