package com.example.routineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class takeattendance extends AppCompatActivity {
    private showAttendanceAdapter adapter;
    private ArrayList<attendanceData> lists;
    private DatabaseReference mRef;
    private String subject,branchName,yearCode,id,roll,name,status;
    private FirebaseUser user;
    private ListView root;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeattendance);
        mRef= FirebaseDatabase.getInstance().getReference();
        user= FirebaseAuth.getInstance().getCurrentUser();
        id=user.getUid();
        lists=new ArrayList<>();
        showAttendance();
        subject=getIntent().getExtras().getString("subject");
    }
    interface Callback
    {
        void firebaseResponseCallback(String result);//whatever your return type is.
    }

    private void yearAndBranch(final MainActivity.Callback callback)
    {
        mRef.child("credentials").child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equals("branchName")) {
                    branchName=snapshot.getValue().toString();
                }
                else {
                    yearCode=snapshot.getValue().toString();
                    callback.firebaseResponseCallback(branchName+"@"+yearCode);
                }
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

    private void showAttendance()
    {
        yearAndBranch(new MainActivity.Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                branchName=arr[0];
                yearCode=arr[1];
                adapter=new showAttendanceAdapter(takeattendance.this,lists,branchName,yearCode,id);
                root=findViewById(R.id.studentAttendanceRecord);
                root.setAdapter(adapter);
                root.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        index=i;
                        attendanceData currentData=lists.get(i);
                        final String update;
                        if(currentData.getAttendance().equals("1")) update="2";
                        else update="1";
                        String dateTime=getDate();
                        mRef.child("dailyAttendance").child(branchName).child(yearCode).child(subject).child(dateTime).child(currentData.getRoll()).child("attendance").setValue(update);
                    }
                });
                String dateTime=getDate();
                mRef.child("dailyAttendance").child(branchName).child(yearCode).child(subject).child(dateTime).addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                        roll=snapshot.getKey();
                        Iterator<DataSnapshot> data=snapshot.getChildren().iterator();
                        while(data.hasNext())
                        {
                            DataSnapshot depthChild=data.next();
                            if(depthChild.getKey().equals("attendance")) status=depthChild.getValue().toString();
                            else if(depthChild.getKey().equals("name")) name=depthChild.getValue().toString();
                        }
                        attendanceData currentData=new attendanceData(name,status,roll);
                        adapter.add(currentData);
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                        roll=snapshot.getKey();
                        Iterator<DataSnapshot> data=snapshot.getChildren().iterator();
                        while(data.hasNext())
                        {
                            DataSnapshot depthChild=data.next();
                            if(depthChild.getKey().equals("attendance")) status=depthChild.getValue().toString();
                            else if(depthChild.getKey().equals("name")) name=depthChild.getValue().toString();
                        }
                        attendanceData currentdata=new attendanceData(name,status,roll);
                        lists.set(index,currentdata);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

            }
        });
    }

    private String getDate(){
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        Date date=new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd yyyy");
        String dateTime=simpleDateFormat.format(date);
        return dateTime;
    }
}