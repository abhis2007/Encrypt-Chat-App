package com.example.routineapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class chatAdapter extends ArrayAdapter<chatDataType> {
    private boolean hasImage;
    private View senderListView, receiverListView;

    public chatAdapter(Activity context, List<chatDataType> lists) {
        super(context, 0, lists);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null)
            listView = LayoutInflater.from(getContext()).inflate(R.layout.subjectchatformat, parent, false);
        listView.setBackgroundResource(0);
        RelativeLayout sender_layout = listView.findViewById(R.id.sender_super_parent);
        RelativeLayout receiver_layout = listView.findViewById(R.id.receiver_super_parent);
        chatDataType currentChat = getItem(position);
        String[] key=currentChat.getmName().split("@");
        if(key.length>1)
        {
            //currentUser  sender
            sender_layout.setVisibility(View.VISIBLE);
            receiver_layout.setVisibility(View.GONE);
            hasImage = currentChat.getmImage() != null;
            if(hasImage){
                LinearLayout image_layout = listView.findViewById(R.id.sender_image_layout);
                LinearLayout text_layout = listView.findViewById(R.id.sender_text_layout);
                image_layout.setVisibility(View.VISIBLE);
                text_layout.setVisibility(View.GONE);
                TextView time=listView.findViewById(R.id.sender_chat_image_time);
                time.setText("05:12");
                ImageView image=listView.findViewById(R.id.sender_chat_image);
                Glide
                        .with(getContext())
                        .load(currentChat.getmImage())
                        .into(image);
            }else{
                LinearLayout image_layout = listView.findViewById(R.id.sender_image_layout);
                LinearLayout text_layout = listView.findViewById(R.id.sender_text_layout);
                text_layout.setVisibility(View.VISIBLE);
                image_layout.setVisibility(View.GONE);
                TextView time=listView.findViewById(R.id.sender_chat_text_time);
                time.setText("05:12");
                TextView chat=listView.findViewById(R.id.sender_chat_text);
                chat.setText(currentChat.getmMessage());
            }
        }

        else
        {
            //other user   receiver
            receiver_layout.setVisibility(View.VISIBLE);
            sender_layout.setVisibility(View.GONE);
            hasImage = currentChat.getmImage() != null;
            if(hasImage){
                LinearLayout image_layout = listView.findViewById(R.id.receiver_image_layout);
                LinearLayout text_layout = listView.findViewById(R.id.receiver_text_layout);
                image_layout.setVisibility(View.VISIBLE);
                text_layout.setVisibility(View.GONE);
                TextView name=listView.findViewById(R.id.receiver_chat_image_name);
                name.setText(currentChat.getmName());
                TextView time=listView.findViewById(R.id.receiver_chat_image_time);
                time.setText("05:12");
                ImageView image=listView.findViewById(R.id.receiver_chat_image);
                Glide
                        .with(getContext())
                        .load(currentChat.getmImage())
                        .into(image);
            }else{
                LinearLayout image_layout = listView.findViewById(R.id.receiver_image_layout);
                LinearLayout text_layout = listView.findViewById(R.id.receiver_text_layout);
                text_layout.setVisibility(View.VISIBLE);
                image_layout.setVisibility(View.GONE);
                TextView name=listView.findViewById(R.id.receiver_chat_text_name);
                name.setText(currentChat.getmName());
                TextView time=listView.findViewById(R.id.receiver_chat_text_time);
                time.setText("05:12");
                TextView chat=listView.findViewById(R.id.receiver_chat_text);
                chat.setText(currentChat.getmMessage());
            }
        }

        return listView;
    }
}
