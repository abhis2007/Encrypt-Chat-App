package com.example.routineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class login extends AppCompatActivity {
    private static final int RC_SIGN_IN=1;
    private String id,userName;
    private static String CHANNEL_ID="CHANNEL_ID",CHANNEL_NAME="CHANNEL_NAME";
    private static int notificationId=999;
    private static int PENDING_INTENT_REQUEST_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView loginButton=findViewById(R.id.showLoginProviders);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<AuthUI.IdpConfig> providers= Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                );
                Intent intent=AuthUI
                        .getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setAlwaysShowSignInMethodScreen(true)
                        .setLogo(R.drawable.logo)
                        .setTosAndPrivacyPolicyUrls("http://example.com/","http://example.com/")
                        .setLogo(R.drawable.common_full_open_on_phone)
                        .build();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN)
        {
            if(resultCode==RESULT_OK)
            {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                id=user.getUid();
                userName=user.getDisplayName();
                if(user.getMetadata().getCreationTimestamp()==user.getMetadata().getLastSignInTimestamp()) {
                    DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                    mRef.child("credentials").child(id).child("branchName").setValue("IT");//Default for new Users.
                    mRef.child("credentials").child(id).child("yearCode").setValue("3");   //DefaultForNew Users
                    setDefaultCode();
                    showNewUserWelcomeNotification();
                }
                startMainActivity();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"login failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity()
    {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void setDefaultCode()
    {
        final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
        mRef.child("Routine").child("IT").child("3").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final String day=snapshot.getKey();
                Iterator<DataSnapshot> child=snapshot.getChildren().iterator();
                while(child.hasNext())
                {
                    DataSnapshot childValue=child.next();
                    Iterator<DataSnapshot> depth=childValue.getChildren().iterator();
                    String subject=childValue.getKey();
                    mRef.child("Access").child(id).child(subject).setValue("0");
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

    private void showNewUserWelcomeNotification()
    {
        String contentText="Welcome "+userName+"\n"+"Manage your daily schedule to save your precious time.";
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Account created.")
                .setContentText("Welcome "+userName)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId,builder.build());
    }
}