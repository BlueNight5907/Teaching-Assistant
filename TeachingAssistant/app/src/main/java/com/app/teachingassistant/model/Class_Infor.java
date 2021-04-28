package com.app.teachingassistant.model;

import java.util.ArrayList;

public class Class_Infor {
    private String className;
    private String teacherName;
    private int classPeriod;
    private String teacherUUID;
    private String keyID;
    ArrayList<String> studentList;
    ArrayList<String> studentToAttend;
    private int backgroundtheme;
    private String chatKey;
    public Class_Infor(String className,String teacherName,String teacherUUID,int classPeriod,String keyID
            , ArrayList<String> studentList, ArrayList<String> studentToAttend, int backgroundtheme,String chatKey){
            this.className = className;
            this.teacherName = teacherName;
            this.teacherUUID = teacherUUID;
            this.classPeriod = classPeriod;
            this.keyID = keyID;
            this.studentList = studentList;
            this.studentToAttend = studentToAttend;
            this.backgroundtheme = backgroundtheme;
            this.chatKey = chatKey;
    }
    public  Class_Infor(){
        this.className = "";
        this.teacherName = "";
        this.teacherUUID = "";
        this.classPeriod = 0;
        this.keyID = "";
        this.studentList = null;
        this.studentToAttend = null;
        this.backgroundtheme = 1;
        this.chatKey = "";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getClassPeriod() {
        return classPeriod;
    }

    public void setClassPeriod(int classPeriod) {
        this.classPeriod = classPeriod;
    }

    public String getTeacherUUID() {
        return teacherUUID;
    }

    public void setTeacherUUID(String teacherUUID) {
        this.teacherUUID = teacherUUID;
    }

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public ArrayList<String> getStudentList() {
        return studentList;
    }

    public void setStudentList(ArrayList<String> studentList) {
        this.studentList = studentList;
    }

    public ArrayList<String> getStudentToAttend() {
        return studentToAttend;
    }

    public void setStudentToAttend(ArrayList<String> studentToAttend) {
        this.studentToAttend = studentToAttend;
    }

    public int getBackgroundtheme() {
        return backgroundtheme;
    }

    public void setBackgroundtheme(int backgroundtheme) {
        this.backgroundtheme = backgroundtheme;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }
}
