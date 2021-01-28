package com.example.routineapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class routineAdapter extends ArrayAdapter<routineDataType> {
    public routineAdapter(Activity Context, List<routineDataType> lists){
        super(Context,0,lists);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView=convertView;
        if(listView==null)
            listView= LayoutInflater.from(getContext()).inflate(R.layout.showroutine,parent,false);
        listView.setBackgroundResource(0);
        routineDataType currentElement=getItem(position);
        TextView timeText= listView.findViewById(R.id.time);
        TextView teacherText= listView.findViewById(R.id.teacher);
        TextView subjectText= listView.findViewById(R.id.subject);
        timeText.setText(currentElement.getmTime());
        teacherText.setText(currentElement.getmTeacherName());
        subjectText.setText(currentElement.getmSubject());
        return listView;
    }
}
