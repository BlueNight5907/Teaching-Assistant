package com.app.teachingassistant.model;

import java.util.Date;

public class Attendance_Infor {
    private String name;
    private String describe;
    private  long createAt;
    private long endAt;
    private String type;
    private String keyID;
    public Attendance_Infor(){
        this.name = "";
        this.describe ="";
        this.createAt = new Date().getTime();
        this.endAt = new Date().getTime();
        this.type = "manual";
        keyID = "";
    }
    public Attendance_Infor(String name,String describe,long createAt,long endAt,String type,String keyID){
        this.name = name;
        this.describe = describe;
        this.createAt = createAt;
        this.endAt = endAt;
        this.type = type;
        this.keyID = keyID;
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
