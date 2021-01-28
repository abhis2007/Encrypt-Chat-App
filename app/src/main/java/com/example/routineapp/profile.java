package com.example.routineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String name=user.getDisplayName();
        String email=user.getEmail();
        TextView nameText= findViewById(R.id.profileName);
        TextView emailText= findViewById(R.id.profileEmail);
        nameText.setText(name);
        emailText.setText(email);
    }
}