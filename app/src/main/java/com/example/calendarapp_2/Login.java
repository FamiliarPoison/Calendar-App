package com.example.calendarapp_2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    public void ChangeLayout() {
        FirebaseAuth.getInstance().signOut();
        finish();
    }

}
