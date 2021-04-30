package com.app.teachingassistant.model;

import com.app.teachingassistant.MainActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Attendance_Infor {
    private String name;
    private String describe;
    private  long createAt;
    private long endAt;
    private String type;
    private String keyID;
    private Map<String,Object> StudentStateList;
    public Attendance_Infor(){
        this.name = "";
        this.describe ="";
        this.createAt = new Date().getTime();
        this.endAt = new Date().getTime();
        this.type = "manual";
        keyID = "";
        this.StudentStateList = new HashMap<>();
    }
    public Attendance_Infor(String name,String describe,long createAt,long endAt,String type,String keyID,Map<String,Object>StudentStateList){
        this.name = name;
        this.describe = describe;
        this.createAt = createAt;
        this.endAt = endAt;
        this.type = type;
        this.keyID = keyID;
        this.StudentStateList = StudentStateList;
    }

    public Map<String, Object> getStudentStateList() {
        return StudentStateList;
    }

    public void setStudentStateList(Map<String, Object> studentStateList) {
        StudentStateList = studentStateList;
    }

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
