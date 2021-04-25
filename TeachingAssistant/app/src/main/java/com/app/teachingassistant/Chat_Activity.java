package com.app.teachingassistant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.config.Message_List_Adapter;
import com.app.teachingassistant.model.Message;
import com.app.teachingassistant.model.Result;
import com.app.teachingassistant.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_Activity extends AppCompatActivity {
    CircleImageView userAvt;
    ActionBar actionBar;
    private ArrayList<Message> messageList = new ArrayList<Message>();
    Message_List_Adapter message_list_adapter;
    RecyclerView message_list_view;
    EditText input_message;
    FirebaseUser user;
    DatabaseReference chatRef,classRef,userRef;
    String chatKey;
    EditText chatContent;
    Button sendMessageBtn;
    RecyclerView.LayoutManager layoutManager;
    private boolean loading = true;
    long endAt = new Date().getTime();
    long startAt = new Date().getTime();
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
        chatKey = ClassDAO.getInstance().getCurrentClass().getChatKey();
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatKey);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());


        message_list_view = findViewById(R.id.recycler_gchat);
        message_list_adapter = new Message_List_Adapter(this,messageList);
        layoutManager = new LinearLayoutManager(this);
        message_list_view.setItemAnimator(new DefaultItemAnimator());
        message_list_view.setLayoutManager(layoutManager);
        message_list_view.setAdapter(message_list_adapter);
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
        chatContent = findViewById(R.id.edit_gchat_message);
        sendMessageBtn = findViewById(R.id.button_gchat_send);
        sendMessageBtn.setOnClickListener(sendMess);
        message_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(IsRecyclerViewAtTop())
                {
                    Log.d("ffffff", "onScrollStateChanged: nani cô re");
                    load20Message();
                }
            }
        });
        loadStart();

    }
    private boolean IsRecyclerViewAtTop()   {
        if(message_list_view.getChildCount() == 0)
            return true;
        return message_list_view.getChildAt(0).getTop() == 0;
    }
    private void load20Message(){
        chatRef.limitToLast(20).orderByChild("createdAt").endAt(endAt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Message> temp = new ArrayList<>();
                for(DataSnapshot item: snapshot.getChildren()){
                    temp.add(0,item.getValue(Message.class));
                }
                for(Message item:temp){
                    messageList.add(0,item);
                    message_list_adapter.notifyDataSetChanged();
                }
                endAt = messageList.get(0).getCreatedAt();
                scrollMessageViewToBottom();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadMessage(){
        chatRef.limitToLast(20).orderByChild("createdAt").endAt(endAt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Message> temp = new ArrayList<>();
                for(DataSnapshot item: snapshot.getChildren()){
                    temp.add(0,item.getValue(Message.class));

                }
                for(Message item:temp){
                    messageList.add(0,item);
                    message_list_adapter.notifyDataSetChanged();
                }
                if(messageList.size() > 0){
                    endAt = messageList.get(0).getCreatedAt();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadStart(){
        loadMessage();
    }
    private void scrollMessageViewToBottom(){
        message_list_view.smoothScrollToPosition(messageList.size());
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private View.OnClickListener sendMess = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String content = chatContent.getText().toString();
            if(content.length()==0) return;
            String userUid = user.getUid();
            long dateCreated = (new Date().getTime());
            Message message = new Message(content, AccountDAO.getInstance().getCurrentUser().getName(),userUid,dateCreated);
            clearText();
            message_list_adapter.notifyDataSetChanged();
            scrollMessageViewToBottom();
            Result result = AccountDAO.getInstance().sendMessage(chatRef,message);

            if(result.isError()){
                Toast.makeText(Chat_Activity.this,result.getMessage(),Toast.LENGTH_SHORT).show();
            }
            else {

            }
            Log.d("Time", "onClick: "+dateCreated);
        }
    };
    private void clearText(){
        chatContent.getText().clear();
    }


    @Override
    protected void onStart() {
        super.onStart();
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if(message.getCreatedAt()>startAt){
                    messageList.add(message);
                    message_list_adapter.notifyDataSetChanged();
                    startAt = message.getCreatedAt();
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}