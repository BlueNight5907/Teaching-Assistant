package com.app.teachingassistant.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.teachingassistant.R;
import com.app.teachingassistant.TeachingAssistantApp;
import com.app.teachingassistant.UserManage;
import com.app.teachingassistant.model.NotificationInfor;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class PushNotification extends Service {
    private long now;
    AppNotification notification;

    public PushNotification() {
    }

    // Return the communication channel to the service.
    @Override
    public IBinder onBind(Intent intent){
        // This service is unbounded
        // So this method is never called.
        return null;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        now = new Date().getTime();
        // Create MediaPlayer object, to play your song.


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // Play song
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            String UUID = intent.getStringExtra("UUID");
            FirebaseDatabase.getInstance().getReference("Notifications").child(UUID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    NotificationInfor notificationInfor = snapshot.getValue(NotificationInfor.class);
                    if(notificationInfor != null){
                        if(notificationInfor.getCreateAt() < now)
                            return;
                            pushNotification(notificationInfor.getClassName(),notificationInfor.getContent());
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return START_REDELIVER_INTENT;
    }
    private void pushNotification(String title,String message){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        Intent targetIntent = new Intent(this, UserManage.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, TeachingAssistantApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.hamburger_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Promotion
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(this.getResources().getColor(R.color.electronic_blue))
                .build();
        int notificationId =(int) new Date().getTime();

        notificationManagerCompat.notify(notificationId, notification);
    }

    // Destroy
    @Override
    public void onDestroy() {
        // Release the resources

        super.onDestroy();
    }
}
