package com.app.teachingassistant.config;

import com.app.teachingassistant.R;

public class BackgroundDrawable {
    //Mẫu thiết kế singleton
    private static BackgroundDrawable instance;
    private BackgroundDrawable() {}
    public static BackgroundDrawable getInstance() {
        if(instance == null) {
            synchronized(BackgroundDrawable.class) {
                if(null == instance) {
                    instance  = new BackgroundDrawable();
                }
            }
        }
        return instance;
    }

    public int getBackGround(int id) {
        switch (id) {
            case 2:
                return R.drawable.background02;
            case 3:
                return R.drawable.background03;
            case 4:
                return R.drawable.background04;
            case 5:
                return R.drawable.background05;
            case 6:
                return R.drawable.background06;
            case 7:
                return R.drawable.background07;
            case 8:
                return R.drawable.background08;
            case 9:
                return R.drawable.background09;
            case 10:
                return R.drawable.background10;
        }
        return R.drawable.chemistry_background;
    }
}
