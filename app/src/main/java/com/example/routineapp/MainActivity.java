package com.example.routineapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mRef;
    private String time,id,day,teacherName,userName,code,branchName,yearCode;
    private routineAdapter adapter;
    private ArrayList<routineDataType> lists;
    private FloatingActionButton mFab;
    private ListView rootLists;
    private View lView,pView;
    private routineDataType data;
    private Menu menus;
    private int position;
    private boolean isOnce=false;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRef=FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user ==null)
            startLoginActivity();
        else
            {
            id= user.getUid();
            userName=user.getDisplayName();
            showRoutine();
            day=todayName();
            lists=new ArrayList<>();
            adapter=new routineAdapter(this,lists);
            rootLists= findViewById(R.id.rootView);
            rootLists.setAdapter(adapter);
            rootLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                    final routineDataType currentData=lists.get(i);
                    view.setBackgroundResource(0);
                    menus.findItem(R.id.removeSubject).setVisible(false);
                    mRef.child("Access").child(id).addChildEventListener(new ChildEventListener()
                    {
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
            rootLists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    data=lists.get(i);
                    position=i;
                    if(pView!=null)
                        pView.setBackgroundResource(0);
                    isOnce=true;
                    isAccessToDeleteSubjectHelper(data,i);
                    lView=view;
                    pView=view;
                    return true;
                }
            });
        }

        mFab=findViewById(R.id.floatingButton);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,setting.class));
            }
        });
    }

    private void verifyDialogOnClick(String msg,final routineDataType currentData)
    {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage(msg);
        dialog.setTitle("Action Needed");
        View inflator=LayoutInflater.from(getApplicationContext()).inflate(R.layout.addverifycode,null);
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
                    callChatIntent(currentData.getmAccessCode(),currentData.getmSubject(),currentData.getmTeacherName());
                }else{
                    verifyDialogOnClick("Wrong Code",currentData);
                }
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menuList)
    {
        getMenuInflater().inflate(R.menu.menu_list,menuList);
        menus=menuList;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.signout:
                AuthUI.getInstance().signOut(getApplicationContext())
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    startLoginActivity();
                            }
                        });
                return true;
            case R.id.editBranchYearPreference:
                Intent intent=new Intent(this,edityearbranch.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.profile:
                startActivity(new Intent(this,profile.class));
                return true;
            case R.id.fTab:
                startActivity(new Intent(MainActivity.this,wholeweekroutine.class));
                return true;
            case R.id.ac:
                startActivity(new Intent(MainActivity.this,classesbyteacher.class));
                return true;
            case R.id.removeSubject:
                deleteQuery(branchName,yearCode,data,position);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startLoginActivity()
    {
        Intent intent=new Intent(this,login.class);
        startActivity(intent);
        this.finish();
    }

    private void callChatIntent(String time,String subject,String teacher)
    {
        Intent intent=new Intent(this,subjectchat.class);
        ArrayList<String> lists=new ArrayList<>();
        lists.add(time);lists.add(teacher);lists.add(subject);
        intent.putStringArrayListExtra("subjectLists",lists);
        startActivity(intent);
    }

    private String todayName()
    {
        Calendar calendar = Calendar.getInstance();
        int dayNumber= calendar.get(Calendar.DAY_OF_WEEK)-1;
        ArrayList<String> lists=new ArrayList<>();
        lists.add("SunDay");lists.add("MonDay");lists.add("TuesDay");lists.add("WedNesDay");lists.add("ThursDay");lists.add("FriDay");lists.add("SaturDay");
        return lists.get(dayNumber);
    }

    interface Callback
    {
        void firebaseResponseCallback(String result);//whatever your return type is.
    }

    private void yearAndBranch(final Callback callback)
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

    private void showRoutine()
    {
        yearAndBranch(new Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                branchName=arr[0];
                yearCode=arr[1];
                mRef.child("Routine").child(branchName).child(yearCode).child(day).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Iterator<DataSnapshot> data=snapshot.getChildren().iterator();
                        while(data.hasNext())
                        {
                            DataSnapshot depthChild=data.next();
                            if(depthChild.getKey().equals("mAccessCode")) code=depthChild.getValue().toString();
                            else if(depthChild.getKey().equals("mTeacherName")) teacherName=depthChild.getValue().toString();
                            else if(depthChild.getKey().equals("mTime")) time=depthChild.getValue().toString();
                        }
                        routineDataType currentData=new routineDataType(teacherName,time,snapshot.getKey(),code);//setting access code for each user for each subject
                        adapter.add(currentData);
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

    private void yearAndBranch(final subjectchat.Callback callback)
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

    private void isAccessToDeleteSubjectHelper(final routineDataType data, final int position)
    {
        yearAndBranch(new subjectchat.Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                branchName=arr[0];
                yearCode=arr[1];
                isAccessToDeleteSubject(data,branchName,yearCode,position);
            }
        });
    }

    private void isAccessToDeleteSubject(final routineDataType data, final String branch, final String year, final int position)
    {
        mRef.child("Routine").child(branch).child(year).child(day).child(data.getmSubject()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterator<DataSnapshot> child=snapshot.getChildren().iterator();
                while(child.hasNext())
                {
                    DataSnapshot childValue=child.next();
                    if(childValue.getKey().equals("uid"))
                    {
                        if(childValue.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && isOnce==true){
                            isOnce=false;
                            branchName=branch;yearCode=year;
                            lView.setBackgroundResource(R.drawable.sender);
                            menus.findItem(R.id.removeSubject).setVisible(true);
                        }
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

    private void deleteQuery(final String branch, final String year, final routineDataType data, final int position)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Action needed!");
        String message="You are trying to delete "+data.getmSubject()+" by "+data.getmTeacherName()+".";
        builder.setMessage(message);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteSubject(branch,year);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeResources();
            }
        });
        builder.show();
    }

    private void deleteSubject(String branch,String year)
    {
        mRef.child("Routine").child(branch).child(year).child(todayName()).child(data.getmSubject()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(findViewById(R.id.mainActivityParentLayout),"Deleted Successfully", BaseTransientBottomBar.LENGTH_SHORT).show();
                        lists.remove(lists.get(position));
                        adapter.notifyDataSetChanged();
                        menus.findItem(R.id.removeSubject).setVisible(false);
                    }
                });
    }

    private void removeResources()
    {
        lView.setBackgroundResource(0);
        menus.findItem(R.id.removeSubject).setVisible(false);
    }

}