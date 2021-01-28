package com.example.routineapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class subjectchat extends AppCompatActivity {
    private EditText mMessageText;
    private String day,subject,mId,mName,mBranchName,mYearCode,currentUser;
    private boolean status=false;
    private DatabaseReference mRef;
    private chatDataType chat;
    private Menu menus;
    private Button selectImage,sendChat;
    private static int REQUEST_MEDIA_SELECTED=1,REQUEST_BACKGROUND_COLOR=2;
    private StorageReference mFirebaseStorageRef;
    private FirebaseStorage mFirebaseStorage;
    private chatAdapter messageAdapter;
    private ArrayList<chatDataType> lists;
    private ProgressBar progressBar;
    private View pView,lView;
    private int position;
    private boolean isOnce=false;
    private ChildEventListener mChildEventListener;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjectchat);
        Toolbar toolbar = findViewById(R.id.customToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar=(ProgressBar)findViewById(R.id.imageUploadProgress);
        currentUser=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        lists=new ArrayList<>();
        ListView rootView= findViewById(R.id.child2);
        messageAdapter =new chatAdapter(subjectchat.this,lists);
        rootView.setAdapter(messageAdapter);
        rootView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                chat=lists.get(i);
                position=i;
                if(pView!=null)
                    pView.setBackgroundResource(0);
                isOnce=true;
                isAccessToDeleteSubjectHelper(chat,i);
                lView=view;
                pView=view;
                return true;
            }
        });
        mFirebaseStorage=FirebaseStorage.getInstance();
        mFirebaseStorageRef=mFirebaseStorage.getReference().child("chat_photos");
        ArrayList<String> list=getIntent().getStringArrayListExtra("subjectLists");//time teacher subject
        TextView teacherView= findViewById(R.id.customToolBarTeacher);
        TextView subjectView= findViewById(R.id.customToolBarSubject);
        TextView timeView= findViewById(R.id.customToolBarTime);
        day=todayName();
        subject=list.get(2);
        timeView.setText(list.get(0));
        teacherView.setText(list.get(1));
        subjectView.setText(subject);
        mRef= FirebaseDatabase.getInstance().getReference();
        mMessageText= findViewById(R.id.chatText);
        FirebaseUser User=FirebaseAuth.getInstance().getCurrentUser();
        mName=User.getDisplayName();
        mId=User.getUid();
        showMessages();
        selectImage=findViewById(R.id.imagePicker);
        sendChat=findViewById(R.id.sendChatMessage);
        sendChat.setEnabled(false);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"SELECT ONE IMAGE ONLY"),REQUEST_MEDIA_SELECTED);
            }
        });
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMessages();
            }
        });

        mMessageText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendChat.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_MEDIA_SELECTED && resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            final StorageReference photoRef=mFirebaseStorageRef.child(imageUri.getLastPathSegment());
            photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl=uri.toString();
                            chatDataType myChat=new chatDataType(
                                    null,
                                    downloadUrl,
                                    mName,
                                    Calendar.getInstance().getTimeInMillis()+"@"+mId
                            );
                            String currentTimeInMiliSecond=Calendar.getInstance().getTimeInMillis()+"";
                            mRef.child("Chat").child(mBranchName).child(mYearCode).child(subject)
                                    .child(currentTimeInMiliSecond+"@"+mId).setValue(myChat);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    int progressPercentage=(int)((100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount());
                    ProgressBar progressBar=(ProgressBar)findViewById(R.id.imageUploadProgress);
                    TextView percentStatus=findViewById(R.id.percentStatus);
                    if(progressPercentage>=100) {
                        progressBar.setVisibility(View.GONE);
                        percentStatus.setVisibility(View.GONE);
                    }
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        percentStatus.setVisibility(View.VISIBLE);
                        percentStatus.setText((int)(snapshot.getBytesTransferred()/1000)+"KB/"+(int)(snapshot.getTotalByteCount()/1000)+"KB");
                        progressBar.setProgress(progressPercentage);
                    }
                }
            });
        }

        if(requestCode==REQUEST_BACKGROUND_COLOR && resultCode==RESULT_OK)
        {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.chatmenu,menu);
        menus=menu;
        checkstatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.chatMenuLogout:
                AuthUI.getInstance().signOut(getApplicationContext())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),login.class));
                                finish();
                            }
                        });
                return true;
            case R.id.chatMenuAccount:
                startActivity(new Intent(this,profile.class));
                return true;
            case R.id.takeAttendance:
                Intent intent=new Intent(this,takeattendance.class);
                Bundle data=new Bundle();
                data.putString("subject",subject);
                intent.putExtras(data);
                startActivity(intent);
                return true;
            case R.id.attendanceList:
                Intent attedanceIntent=new Intent(this,attendance.class);
                attedanceIntent.putExtra("subject",subject);
                startActivity(attedanceIntent);
                return true;
            case R.id.bgColor:
                ListView list=(ListView) findViewById(R.id.child2);
                setBackground();
                return true;
            case R.id.removeChat:
                deleteQuery(mBranchName,mYearCode,chat,position);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    interface Callback
    {
        void firebaseResponseCallback(String result);
    }

    private void addMessages()
    {
        getYearAndBranch(new Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                mBranchName=arr[0];
                mYearCode=arr[1];
                setAddMessage(mBranchName,mYearCode);
            }
        });
    }

    private void getYearAndBranch(final Callback callback)
    {
        mRef.child("credentials").child(mId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equals("branchName")) {
                    mBranchName=snapshot.getValue().toString();
                }
                else {
                    mYearCode=snapshot.getValue().toString();
                    callback.firebaseResponseCallback(mBranchName+"@"+mYearCode);
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

    private void showMessages()
    {
        getYearAndBranch(new Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                mBranchName=arr[0];
                mYearCode=arr[1];
                setShowMessage(mBranchName,mYearCode);
            }
        });
    }

    private void checkstatus()
    {
        getYearAndBranch(new Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                mBranchName=arr[0];
                mYearCode=arr[1];
                checkAccessToChat(mBranchName,mYearCode);
            }
        });
    }

    private String todayName()
    {
        Calendar calendar = Calendar.getInstance();
        int dayNumber= calendar.get(Calendar.DAY_OF_WEEK)-1;
        ArrayList<String> lists=new ArrayList<>();
        lists.add("SunDay");lists.add("MonDay");lists.add("TuesDay");lists.add("WedNesDay");lists.add("ThursDay");lists.add("FriDay");lists.add("SaturDay");
        return lists.get(dayNumber);
    }

    private void checkAccessToChat(String branch,String year)
    {
        mRef.child("Routine").child(branch).child(year).child(todayName()).child(subject).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterator<DataSnapshot> child=snapshot.getChildren().iterator();
                while(child.hasNext())
                {
                    DataSnapshot childValue=child.next();
                    if (!childValue.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        menus.findItem(R.id.attendanceList).setVisible(false);
                        menus.findItem(R.id.takeAttendance).setVisible(false);
                    }else{
                        menus.findItem(R.id.attendanceList).setVisible(true);
                        menus.findItem(R.id.takeAttendance).setVisible(true);
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

    private void setBackground()
    {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(intent.createChooser(intent,"SELECT IMAGE"),REQUEST_BACKGROUND_COLOR);
    }

    private void setShowMessage(String branchName,String yearCode)
    {
        mChildEventListener=mRef.child("Chat").child(branchName).child(yearCode).child(subject).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterator<DataSnapshot> data=snapshot.getChildren().iterator();
                String id=null,message=null,image=null,uniqueKey=null,name=null;
                while(data.hasNext())
                {
                    DataSnapshot depthChild=data.next();
                    if(depthChild.getKey().equals("mMessage")) message=depthChild.getValue().toString();
                    else if(depthChild.getKey().equals("mImage")) image=depthChild.getValue().toString();
                    else if(depthChild.getKey().equals("mName")) {
                        name = depthChild.getValue().toString();
                        if(id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            name+="@cur";
                        chatDataType currentData=new chatDataType(message,image,name,uniqueKey);//setting access code for each user for each subject
                        messageAdapter.add(currentData);
                    }
                    else if(depthChild.getKey().equals("key")) {
                        uniqueKey=depthChild.getValue().toString();
                        id=uniqueKey.split("@")[1];
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

    private void deleteSubject(String branch,String year,String uniqueKey)
    {
        mRef.child("Chat").child(branch).child(year).child(subject).child(uniqueKey).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(findViewById(R.id.subjectChatParent),"Deleted Successfully", BaseTransientBottomBar.LENGTH_SHORT).show();
                        lists.remove(lists.get(position));
                        messageAdapter.notifyDataSetChanged();
                        menus.findItem(R.id.removeChat).setVisible(false);
                    }
                });
    }

    private void setAddMessage(String branchName,String yearCode)
    {
        String uniqueKey=Calendar.getInstance().getTimeInMillis()+"@"+mId;
        chatDataType currentData = new chatDataType(mMessageText.getText().toString(),null,mName,uniqueKey);
        mRef.child("Chat").child(branchName).child(yearCode).child(subject).child(uniqueKey).setValue(currentData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mMessageText.setText("");
                    }
                });
    }

    private void isAccessToDeleteSubjectHelper(final chatDataType data, final int position)
    {
        getYearAndBranch(new subjectchat.Callback() {
            @Override
            public void firebaseResponseCallback(String result) {
                String[] arr=result.split("@");
                mBranchName=arr[0];
                mYearCode=arr[1];
                isAccessToDeleteSubject(data,mBranchName,mYearCode,position);
            }
        });
    }

    private void isAccessToDeleteSubject(final chatDataType data, final String branch, final String year, final int position)
    {
        if(data.getKey().split("@")[1].equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            isOnce = false;
            mBranchName = branch;
            mYearCode = year;
            lView.setBackgroundResource(R.drawable.sender);
            menus.findItem(R.id.removeChat).setVisible(true);
        }
        else
            menus.findItem(R.id.removeChat).setVisible(false);
    }

    private void deleteQuery(final String branch, final String year, final chatDataType data, final int position)
    {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setTitle("Action needed!");
        String message="You are trying to delete "+data.getmMessage()+".";
        builder.setMessage(message);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteSubject(branch,year,data.getKey());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                lView.setBackgroundResource(0);
                menus.findItem(R.id.removeChat).setVisible(false);
            }
        });
        builder.show();
    }

}