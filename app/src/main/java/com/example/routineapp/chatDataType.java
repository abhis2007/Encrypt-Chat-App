package com.example.routineapp;

import android.graphics.Bitmap;

public class chatDataType {
    private String mName, mMessage, imageUrl,key;

    public chatDataType() {
    }

    public chatDataType(String mMessage, String imageUrl, String mName ,String key) {
        this.mMessage = mMessage;
        this.imageUrl = imageUrl;
        this.mName = mName;
        this.key=key;
    }

    public String getmImage() {
        return imageUrl;
    }

    public String getmName() {
        return mName;
    }

    public String getmMessage() {
        return mMessage;
    }

    public String getKey(){ return key; }
}
