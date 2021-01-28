package com.example.routineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class classesbyteacher extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private String uId;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> lists;
    private ListView root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classesbyteacher);

        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();
        user= FirebaseAuth.getInstance().getCurrentUser();
        uId=user.getUid();
        root=findViewById(R.id.rootListView);
        lists=new ArrayList<>();

        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lists);
        root.setAdapter(adapter);

        mRef.child("assignedSubjectToTeacher").child("college").child(uId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String subject,branch,year;
                        adapter.notifyDataSetChanged();
                        String[] data=snapshot.getValue().toString().split("@");
                        subject=data[0];
                        branch=data[1];
                        year=data[2];
                        lists.add(subject+"  "+branch+" "+year);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}