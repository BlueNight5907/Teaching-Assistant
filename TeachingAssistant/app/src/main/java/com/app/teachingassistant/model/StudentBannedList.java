package com.app.teachingassistant.model;

public class StudentBannedList {
    private String UUID;
    private String name;
    private int state;
    private float absentDates;

    public StudentBannedList() {
        this.UUID = "";
        this.name = "";
        this.state = 0;
        this.absentDates = 0.0f;
    }

    public StudentBannedList(String UUID, String name, int state, int absentDates) {
        this.UUID = UUID;
        this.name = name;
        this.state = state;
        this.absentDates = absentDates;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getAbsentDates() {
        return absentDates;
    }

    public void setAbsentDates(float absentDates) {
        this.absentDates = absentDates;
    }
}
