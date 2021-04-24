package com.app.teachingassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.app.teachingassistant.config.Message_List_Adapter;
import com.app.teachingassistant.model.Message;
import com.app.teachingassistant.model.User;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_Activity extends AppCompatActivity {
    CircleImageView userAvt;
    ActionBar actionBar;
    private ArrayList<Message> messageList = new ArrayList<Message>();
    Message_List_Adapter message_list_adapter;
    RecyclerView message_list_view;
    EditText input_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);


        userAvt = findViewById(R.id.user_logo_toolbar);
        userAvt.setVisibility(View.GONE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Nhắn tin");


        message_list_view = findViewById(R.id.recycler_gchat);
        genMock();
        message_list_adapter = new Message_List_Adapter(this,messageList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        message_list_view.setItemAnimator(new DefaultItemAnimator());
        message_list_view.setLayoutManager(layoutManager);
        message_list_view.setAdapter(message_list_adapter);
        scrollMessageViewToBottom();

        input_message = findViewById(R.id.edit_gchat_message);
        input_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message_list_view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollMessageViewToBottom();
                    }
                }, 300);

            }
        });


    }
    private void scrollMessageViewToBottom(){
        message_list_view.smoothScrollToPosition(messageList.size());
    }
    private void genMock(){
        for(int i=0;i<10;i++){
            Date date = new Date();
            long timeMilli = date.getTime();
            User newUser = new User();
            Message message = new Message("can you hear at "+i,newUser,timeMilli);
            messageList.add(message);

        }
        Date date = new Date();
        long timeMilli = date.getTime();
        User currentUser = new User();
        Message message = new Message("My message ",currentUser,timeMilli);
        messageList.add(message);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}