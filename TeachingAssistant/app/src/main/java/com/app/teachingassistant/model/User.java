package com.app.teachingassistant.model;

public class User {
    private String UUID;
    private String email;
    private String name;
    private boolean hasProfileUrl = false;
    private String role;
    private String gender;
    private String sID;
    public User(){
        this.UUID = "";
        this.name = "";
        this.hasProfileUrl = false;
        this.email = "";
        this.role = "";
        this.gender = "";
        this.sID = "";
    }
    public User(String UUID,String email,String name,boolean hasProfileUrl,String role,String gender,String sID){
        this.UUID = UUID;
        this.name = name;
        this.sID = sID;
        this.hasProfileUrl = hasProfileUrl;
        this.email = email;
        this.role = role;
        this.gender = gender;
    }

    public boolean isHasProfileUrl() {
        return hasProfileUrl;
    }

    public void setHasProfileUrl(boolean hasProfileUrl) {
        this.hasProfileUrl = hasProfileUrl;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getSID() {
        return sID;
    }

    public void setSID(String sID) {
        this.sID = sID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
