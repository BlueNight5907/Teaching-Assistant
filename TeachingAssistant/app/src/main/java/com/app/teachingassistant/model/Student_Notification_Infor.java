package com.app.teachingassistant.model;

import java.util.Date;

public class Student_Notification_Infor{
    long date;
    String className;
    String notificationName;
    String content;
    String type;
    public Student_Notification_Infor(){
        this.date = new Date().getTime();
        this.className ="";
        this.notificationName ="";
        this.content = "";
        this.type ="Thông báo";
    }

}
