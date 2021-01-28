package com.example.routineapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class attendanceListAdapter extends ArrayAdapter<attendanceData> {
    private TextView roll,name;
    public attendanceListAdapter(Activity Context, List<attendanceData>lists){
        super(Context,0,lists);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView=convertView;
        if(convertView==null)
            listView= LayoutInflater.from(getContext()).inflate(R.layout.showattendancelist,parent,false);

        attendanceData att=getItem(position);
        roll=listView.findViewById(R.id.headRoll);
        name=listView.findViewById(R.id.headName);
        roll.setText(att.getRoll());
        name.setText(att.getName());
        return listView;
    }
}
