package com.app.teachingassistant.model;

import java.util.Date;

public class NotificationInfor {
    private long createAt;
    private String className;
    private int type;//0 la vang hoc 1 la cam thi
    private String content;
    public NotificationInfor(){
        createAt = new Date().getTime();
        className = "";
        type = 0;
        content = "";
    }
    public NotificationInfor(long createAt,String className,int type,String content){
        this.createAt = createAt;
        this.className = className;
        this.type = type;
        this.content = content;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
