package com.example.routineapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;

public class fragmentpage extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private String u="",id,yearCode,branchName,userName;
    private ArrayList<String> lists;
    private ArrayList<routineDataType>list;
    private TextView t;
    private routineAdapter adapter;

    public fragmentpage(){}

    public static fragmentpage newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragmentpage fragment = new fragmentpage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage=getArguments().getInt(ARG_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.selectedtab,container,false);

        lists=new ArrayList<>();
        lists.add("MonDay");lists.add("TuesDay");lists.add("WedNesDay");lists.add("ThursDay");
        lists.add("FriDay");lists.add("SaturDay");lists.add("SunDay");
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        userName=user.getDisplayName();
        id=user.getUid();
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();
        View toolBarView=LayoutInflater.from(getActivity()).inflate(R.layout.fragmenttoolbar,null);
        final EditText query=(EditText) toolBarView.findViewById(R.id.serachQuery);
        ImageView button=(ImageView) toolBarView.findViewById(R.id.fireSearch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),query.getText(),Toast.LENGTH_LONG).show();
            }
        });

        list=new ArrayList<>();
        adapter=new routineAdapter(getActivity(),list);
        ListView rootView=view.findViewById(R.id.rootView);
        rootView.setAdapter(adapter);
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                final routineDataType currentData=list.get(i);
                mRef.child("Access").child(id).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.getKey().equals(currentData.getmSubject())){
                            if(snapshot.getValue().toString().equals(currentData.getmAccessCode())){
                                callChatIntent(currentData.getmTime(),currentData.getmSubject(),currentData.getmTeacherName());
                            }else{
                                verifyDialogOnClick("Verify Code",currentData);
                            }
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
        });
        getWeekRoutine();
        return view;
    }

    interface Callback
    {
        void firebaseResponseCallback(String result);//whatever your return type is.
    }

    private void yearAndBranch(final fragmentpage.Callback callback)
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

    private void getWeekRoutine()
    {
        yearAndBranch(new Callback() {
            @Override
            public void firebaseResponseCallback(final String result) {
                String[] arr=result.split("@");
                branchName=arr[0];
                yearCode=arr[1];
                mRef.child("Routine").child(branchName).child(yearCode).child(lists.get(mPage-1))
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Iterator<DataSnapshot> subjectValueIterator=snapshot.getChildren().iterator();
                                String time = null,teacher = null,access = null;
                                while (subjectValueIterator.hasNext())
                                {
                                    DataSnapshot subject=subjectValueIterator.next();
                                    if(subject.getKey().equals("mAccessCode")) access=subject.getValue().toString();
                                    if(subject.getKey().equals("mTeacherName")) teacher=subject.getValue().toString();
                                    if(subject.getKey().equals("mTime")) time=subject.getValue().toString();
                                }
                                adapter.add(new routineDataType(teacher,time,snapshot.getKey(),access));
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

    private void verifyDialogOnClick(String msg,final routineDataType currentData)
    {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
        dialog.setMessage(msg);
        dialog.setTitle("Action Needed");
        View inflator=LayoutInflater.from(getActivity()).inflate(R.layout.addverifycode,null);
        dialog.setView(inflator);
        EditText addCode=inflator.findViewById(R.id.addCode);
        final EditText verifyCode=inflator.findViewById(R.id.verifyCode);
        addCode.setVisibility(View.GONE);
        dialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String code=verifyCode.getText().toString();
                if(code.equals(currentData.getmAccessCode())){
                    mRef.child("Access").child(id).child(currentData.getmSubject()).setValue(currentData.getmAccessCode());
//                    callChatIntent(currentData.getmAccessCode(),currentData.getmSubject(),currentData.getmTeacherName());
                }else{
                    verifyDialogOnClick("Wrong Code",currentData);
                }
            }
        });
        dialog.show();
    }

    private void callChatIntent(String time,String subject,String teacher)
    {
        Intent intent=new Intent(getActivity(),subjectchat.class);
        ArrayList<String> lists=new ArrayList<>();
        lists.add(time);lists.add(teacher);lists.add(subject);
        intent.putStringArrayListExtra("subjectLists",lists);
        startActivity(intent);
    }
}
