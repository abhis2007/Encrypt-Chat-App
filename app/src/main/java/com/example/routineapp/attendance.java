package com.example.routineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class attendance extends AppCompatActivity {
    private EditText name,rollNo;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private String subject,sName,sRoll,branchName,yearCode,id,mName,mRoll,status,lAttendance,lName,lRoll;
    private attendanceListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ArrayList<attendanceData> lists=new ArrayList<>();
        adapter=new attendanceListAdapter(this,lists);
        subject=getIntent().getExtras().getString("subject");
        ListView dataList=findViewById(R.id.subjectList);
        dataList.setAdapter(adapter);
        mRef= FirebaseDatabase.getInstance().getReference();
        user=FirebaseAuth.getInstance().getCurrentUser();
        id=user.getUid();
        name=findViewById(R.id.name);
        rollNo=findViewById(R.id.roll);
        showAttendanceList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.attendance,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.saveAttendance:
                sName = name.getText().toString();
                sRoll = rollNo.getText().toString();
                addAttendance();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    interface Callback
    {
        void firebaseResponseCallback(String result);
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

    private void addAttendance()
    {
        yearAndBranch(new MainActivity.Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                branchName=arr[0];
                yearCode=arr[1];
                //show data(attendance) in listview
                mRef.child("AttendanceList").child(branchName).child(yearCode).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Iterator<DataSnapshot> child=snapshot.getChildren().iterator();
                        while (child.hasNext())
                        {
                            DataSnapshot childvalue=child.next();
                            if(childvalue.getKey()=="attendance") status=childvalue.getValue().toString();
                            else mName=childvalue.getValue().toString();
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
                attendanceData att=new attendanceData(sName,"1");
                mRef.child("AttendanceList").child(branchName).child(yearCode).child(sRoll).setValue(att)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Added Successfully",Toast.LENGTH_SHORT).show();
                            }
                        });
                name.setText("");
                rollNo.setText("");
                String dateTime=getDate();
                mRef.child("dailyAttendance").child(branchName).child(yearCode).child(subject).child(dateTime).child(sRoll).setValue(att);
//                addAttendanceInEachSubjectHelper(branchName,yearCode,sRoll,att);
            }
        });
    }

    private void showAttendanceList()
    {
        yearAndBranch(new MainActivity.Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                branchName=arr[0];
                yearCode=arr[1];
                mRef.child("AttendanceList").child(branchName).child(yearCode).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Iterator<DataSnapshot> child=snapshot.getChildren().iterator();
                        while (child.hasNext())
                        {
                            DataSnapshot childvalue=child.next();
                            if(childvalue.getKey()=="attendance") status=childvalue.getValue().toString();
                            else mName=childvalue.getValue().toString();
                        }
                        adapter.add(new attendanceData(mName,status,snapshot.getKey()));
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
        });
    }

    private void addAttendanceInEachSubjectHelper(final String branch, final String year, final String roll,final attendanceData att)
    {
        Calendar calendar=Calendar.getInstance();
        final String date=calendar.getTime().toString();
        mRef.child("Routine").child(branch).child(year).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterator<DataSnapshot> child=snapshot.getChildren().iterator();
                while(child.hasNext())
                {
                    DataSnapshot childvalue=child.next();
                    String subject=childvalue.getKey();
                    mRef.child("dailyAttendance").child(branch).child(year).child(subject).child("date").child(roll).setValue(att);
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

    private String getDate(){
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        Date date=new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd yyyy");
        String dateTime=simpleDateFormat.format(date);
        return dateTime;
    }

}