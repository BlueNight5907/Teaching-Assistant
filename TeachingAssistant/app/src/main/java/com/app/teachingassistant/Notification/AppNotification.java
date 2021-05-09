package com.app.teachingassistant.Notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.teachingassistant.R;
import com.app.teachingassistant.TeachingAssistantApp;
import com.app.teachingassistant.UserManage;

import java.sql.Time;
import java.util.Date;

public class AppNotification {
    Activity activity;
    private NotificationManagerCompat notificationManagerCompat;
    public AppNotification(Activity activity){
        this.activity = activity;
        this.notificationManagerCompat = NotificationManagerCompat.from(activity);
    }
    public void pushNotification(String title,String message){
        Intent targetIntent = new Intent(activity, UserManage.class);
        PendingIntent contentIntent = PendingIntent.getActivity(activity, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(activity, TeachingAssistantApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.hamburger_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Promotion
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(activity.getResources().getColor(R.color.electronic_blue))
                .build();
        int notificationId =(int) new Date().getTime();

        this.notificationManagerCompat.notify(notificationId, notification);
    }
}
