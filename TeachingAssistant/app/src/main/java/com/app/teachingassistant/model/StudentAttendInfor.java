package com.app.teachingassistant.model;

import android.graphics.Bitmap;

public class StudentAttendInfor {
    private String UUID;
    private int state;//-1 với vắng, 0 trễ, 1 là điểm danh đúng giờ
    public StudentAttendInfor(){
        this.UUID = "";
        this.state = 0;
    }
    public StudentAttendInfor(String UUID,int state){
        this.UUID = UUID;
        this.state = state;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
