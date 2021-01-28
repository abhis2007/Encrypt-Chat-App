package com.example.routineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class edityearbranch extends AppCompatActivity {
    private String yearCode="0",branchName;
    private int yearStatus=0,branchStatus=0;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseUser User;
    private String id;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edityearbranch);
        id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef=FirebaseDatabase.getInstance().getReference();
        Spinner yearSpinner=(Spinner)findViewById(R.id.yearCode);
        Spinner branchSpinner=(Spinner)findViewById(R.id.branchName);
        ArrayList<String> yearLists=new ArrayList<>();
        yearLists.add("Year");yearLists.add("1");yearLists.add("2");yearLists.add("3");yearLists.add("4");
        ArrayList<String> branchLists=new ArrayList<>();
        branchLists.add("Branch");branchLists.add("IT");branchLists.add("CSE");branchLists.add("CE");branchLists.add("EE");branchLists.add("ME");
        ArrayAdapter<String> branchAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,branchLists);
        branchSpinner.setAdapter(branchAdapter);
        ArrayAdapter<String> yearAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,yearLists);
        yearSpinner.setAdapter(yearAdapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    yearCode = adapterView.getItemAtPosition(position).toString();
                    mRef.child("credentials").child(id).child("yearCode").setValue(yearCode).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            yearStatus=1;
                            if(yearStatus==1 && branchStatus==1)
                                startMainActivity();
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    branchName = adapterView.getItemAtPosition(position).toString();
                    mRef.child("credentials").child(id).child("branchName").setValue(branchName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    branchStatus=1;
                                    if(yearStatus==1 && branchStatus==1)
                                        startMainActivity();
                                }
                            });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(),"Nothing Selected",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startMainActivity()
    {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

}