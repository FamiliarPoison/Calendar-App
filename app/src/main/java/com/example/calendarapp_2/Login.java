package com.example.calendarapp_2;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

public class Login extends LinearLayout {
    Context context;
    Button LoginButton;
    EditText username,password;


    public Login(Context context) {
        super(context);

    }

    public Login(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout_2();

        LoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
            }
        });

    }
    private void InitializeLayout_2(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.login, this);
        LoginButton = view.findViewById(R.id.login_button);
        username = view.findViewById(R.id.user_name);
        password = view.findViewById(R.id.user_password);

    }
}
