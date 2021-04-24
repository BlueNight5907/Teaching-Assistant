package com.app.teachingassistant.model;

import java.util.Date;

public class Attendance_Infor {
    public String name;
    public  long date;
    public int status;
    public Attendance_Infor(){
        this.name = "";
        this.date = new Date().getTime();
        this.status = -1;
    }

}
