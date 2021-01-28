package com.example.routineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.Inflater;

public class setting extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener{
    private String code,dayName=null,time="",userId,userName,yearCode=null,branchName=null,role=null,subjectName=null,eTime="",sTime="";
    private DatabaseReference mRef;
    private Button endTime,mButton;
    private AlertDialog.Builder myDialog;
    private DialogFragment timePickerFragment;
    private EditText addCode,verifyCode;
    private int start=0,Final=0,codestatus=0,itemVal=0,value=0,status=0;
    private routineDataType currentData;
    private ArrayAdapter<String> lists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReference();
        userName=user.getDisplayName();
        userId=user.getUid();
        timePickerFragment=new timePicker();
        mButton=findViewById(R.id.startTime);
        endTime=findViewById(R.id.endTime);
        endTime.setVisibility(View.INVISIBLE);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerFragment.show(getSupportFragmentManager(),"startTime");
                start=1;
                Final=0;
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerFragment.show(getSupportFragmentManager(),"endTime");
                Final=1;
                start=0;
            }
        });
        TextView day=findViewById(R.id.day);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog=new AlertDialog.Builder(setting.this);
                lists=new ArrayAdapter<>(getApplicationContext(),android.R.layout.select_dialog_singlechoice);
                lists.add("SunDay");lists.add("MonDay");lists.add("TuesDay");lists.add("WedNesDay");lists.add("ThursDay");lists.add("FriDay");lists.add("SaturDay");
                myDialog.setAdapter(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dayName=lists.getItem(i);
                        TextView data=findViewById(R.id.day);
                        data.setText(dayName);
                        itemVal=1;
                    }
                });
                myDialog.show();
            }
        });
    }
    public void streamClicked(View view)
    {
        boolean checked=((RadioButton)view).isChecked();
        switch (view.getId())
        {
            case R.id.cse:
                if(checked)
                    branchName="CSE";
                break;
            case R.id.it:
                if(checked)
                    branchName="IT";
                break;
            case R.id.ee:
                if(checked)
                    branchName="EE";
                break;
            case R.id.me:
                if(checked)
                    branchName="ME";
                break;
            case R.id.ce:
                if(checked)
                    branchName="CE";
                break;
        }
    }

    public void yearClicked(View view)
    {
        boolean checked=((RadioButton)view).isChecked();
        switch (view.getId())
        {
            case R.id.firstYear:
                if(checked)
                    yearCode="1";
                break;
            case R.id.secondYear:
                if(checked)
                    yearCode="2";
                break;
            case R.id.thirdYear:
                if(checked)
                    yearCode="3";
                break;
            case R.id.fourthYear:
                if(checked)
                    yearCode="4";
                break;
        }
    }

    public void roleClicked(View view)
    {
        boolean checked=((RadioButton)view).isChecked();
        switch (view.getId())
        {
            case R.id.teacherRole:
                if(checked)
                    role="teacher";
                break;
            case R.id.crRole:
                if(checked)
                    role="cr";
                break;
            case R.id.studentRole:
                if(checked)
                    role="student";
                break;
        }
    }

    private void showdialog()
    {
        AlertDialog.Builder dialogBox=new AlertDialog.Builder(setting.this);
        dialogBox.setMessage(R.string.completeInfo);
        dialogBox.setTitle("RoutineApp");
        dialogBox.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute)
    {

        if(status==1) {
            time += hour + ":" + minute;
            value=1;
        }
        else {
            time += hour + ":" + minute;
            status=1;
        }
        time="";
        if(start==1) {
            mButton.setText(time);
            sTime=time;
        }
        if(Final==1) {
            endTime.setText(time);
            eTime=time;
        }
        endTime.setVisibility(View.VISIBLE);
    }

    private void getAccessCode()
    {
        myDialog=new AlertDialog.Builder(setting.this);
        myDialog.setTitle("My Classroom");
        myDialog.setMessage("Create classroom code.");
        View inflator= LayoutInflater.from(this).inflate(R.layout.addverifycode,null);
        myDialog.setView(inflator);
        verifyCode=inflator.findViewById(R.id.verifyCode);
        addCode=inflator.findViewById(R.id.addCode);
        verifyCode.setVisibility(View.GONE);
        myDialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                code = addCode.getText().toString().trim();
                if(code.matches("")){
                    getAccessCode();
                }
                else codestatus=1;
            }
        });
        myDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        myDialog.show();
    }

    private void setDefaultAccessCode()
    {
        mRef.child("credentials").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterator<DataSnapshot> child=snapshot.getChildren().iterator();
                mRef.child("Access").child(snapshot.getKey()).child(subjectName).setValue("0");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.save,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId()==R.id.routine_saveData){
            saveData();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void saveData()
    {
        EditText subjectEdit=(EditText) findViewById(R.id.subjectName);
        subjectName=subjectEdit.getText().toString();
        if(yearCode!=null && branchName!=null && role!=null && (!subjectName.isEmpty()) && value==1 && itemVal==1){
            if(codestatus==0) getAccessCode();
            else if(codestatus==1) {
                currentData = new routineDataType(userName, sTime+eTime, code);
                mRef.child("Routine").child(branchName).child(yearCode).child(dayName).child(subjectName).setValue(currentData);
                mRef.child("Routine").child(branchName).child(yearCode).child(dayName).child(subjectName).child("permissions").setValue(new permissions(userId,true))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Added Successfully!",Toast.LENGTH_SHORT).show();
                            }
                        });
                setDefaultAccessCode();
            }
        }
        else
            showdialog();
    }
}