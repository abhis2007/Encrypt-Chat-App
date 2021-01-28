package com.example.routineapp;

public class permissions {
    String uid;
    boolean access;
    public permissions(String uid,boolean access){
        this.access=access;
        this.uid=uid;
    }

    public String getUid() {
        return uid;
    }

    public boolean isAccess() {
        return access;
    }
}
