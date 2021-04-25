package com.app.teachingassistant.model;

public class Message {
    private String message;
    private String userUID;
    private String sender;
    private long createdAt;
    public Message(){
        this.message = "";
        this.sender ="";
        this.userUID = "";
        this.createdAt = 0;
    }

    public Message(String message,String sender,String userUID,long createdAt){
        this.message = message;
        this.sender = sender;
        this.userUID = userUID;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
