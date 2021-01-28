package com.example.routineapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;
import java.util.List;

public class showAttendanceAdapter extends ArrayAdapter<attendanceData> {
    private TextView roll,name,status;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private String branchName,yearCode,id;
    private int colorCode;
    public showAttendanceAdapter(Activity Context, List<attendanceData> lists,String mBranchName,String mYearCode,String mId)
    {
        super(Context,0,lists);
        branchName=mBranchName;
        yearCode=mYearCode;
        id=mId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        user=FirebaseAuth.getInstance().getCurrentUser();
        id=user.getUid();
        mRef= FirebaseDatabase.getInstance().getReference();
        View listView=convertView;
        if(convertView==null)
            listView= LayoutInflater.from(getContext()).inflate(R.layout.studentattendance,parent,false);
        attendanceData currentData=getItem(position);
        roll=listView.findViewById(R.id.roll);
        name=listView.findViewById(R.id.name);
        status=listView.findViewById(R.id.attendanceStatus);
        roll.setText(currentData.getRoll());
        name.setText(currentData.getName());
        if(currentData.getAttendance().equals("1"))
        {
            colorCode=ContextCompat.getColor(getContext(),R.color.absent);
            status.setText(R.string.absent);
        }
        else
        {
            colorCode=ContextCompat.getColor(getContext(),R.color.present);
            status.setText(R.string.present);
        }
        status.setTextColor(colorCode);
        return listView;
    }
}
