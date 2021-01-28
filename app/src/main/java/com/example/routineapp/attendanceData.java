package com.example.routineapp;

public class attendanceData {
    private String roll,name,attendance;
    public attendanceData(String name,String attendance){
        this.name=name;
        this.attendance=attendance;
    }
    public attendanceData(String name,String attendance,String roll){
        this.name=name;
        this.attendance=attendance;
        this.roll=roll;
    }

    public String getName()
    {
        return name;
    }

    public String getAttendance() {
        return attendance;
    }

    public String getRoll(){
        return roll;
    }
}
