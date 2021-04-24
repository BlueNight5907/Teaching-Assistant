package com.app.teachingassistant.model;

import android.graphics.Bitmap;

public class Student_Infor {
    String name;
    Bitmap image;
    public Student_Infor(){
        this.name = "";
        this.image = null;
    }
    public Student_Infor(String name){
        this.name = name;
        this.image = null;
    }

    public String getName() {
        return name;
    }
}
