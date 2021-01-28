package com.example.routineapp;

public class routineDataType {
    private String mSubject,mTeacherName,mTime,mAccessCode;
    public routineDataType(String name,String time,String code){
        this.mTeacherName=name;
        this.mTime=time;
        this.mAccessCode=code;
    }
    public routineDataType(String name,String time,String subject,String code){
        this.mTeacherName=name;
        this.mTime=time;
        this.mAccessCode=code;
        this.mSubject=subject;
    }

    public String getmTeacherName() {
        return mTeacherName;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmAccessCode() {
        return mAccessCode;
    }

    public String getmSubject() {
        return mSubject;
    }
}
