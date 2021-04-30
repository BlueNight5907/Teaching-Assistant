package com.app.teachingassistant.model;

public class StudentBannedInfor {
    private String UUID;
    private int state;//-1 với bị cấm thi, 0
    public StudentBannedInfor(){
        this.UUID = "";
        this.state = 0;
    }
    public StudentBannedInfor(String UUID, int state){
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
