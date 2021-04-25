package com.app.teachingassistant.config;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.model.Message;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Message_List_Adapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Activity mActivity;
    private ArrayList<Message> messageList;
    Calendar cal = Calendar.getInstance();
    public Message_List_Adapter(Activity activity,ArrayList<Message> messageList) {
        super();
        this.mActivity = activity;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messageList.get(position);

        if (message.getUserUID().equals(AccountDAO.getInstance().getCurrentUser().getUUID())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_message, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.other_message, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message,position);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message,position);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText,dateText;
        ProgressBar progressBar;
        CardView cardView;

        SentMessageHolder(View itemView) {
            super(itemView);
            dateText = (TextView)itemView.findViewById(R.id.text_gchat_date_me);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            progressBar = itemView.findViewById(R.id.progressBar);
            cardView = itemView.findViewById(R.id.card_gchat_message_me);

        }

        void bind(Message message,int positon) {
            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using methods
            //lấy ngày dc tạo của tin nhắn
            cal.setTimeInMillis(message.getCreatedAt());
            SimpleDateFormat dft = null;
            dft = new SimpleDateFormat("hh:mm", Locale.getDefault());
            String time = dft.format(cal.getTime());
            //Định dạng ngày / tháng /năm
            if(positon == 0){
                dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String strDate = dft.format(cal.getTime());
                //hiển thị lên giao diện
                dateText.setText(strDate);
            }
            else if(messageList.get(positon).getCreatedAt() - messageList.get(positon-1).getCreatedAt() >= 1 * 1000 * 60 * 60 * 24){
                dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String strDate = dft.format(cal.getTime());
                //hiển thị lên giao diện
                dateText.setText(strDate);
            }
            else {
                dateText.setVisibility(View.GONE);
            }
            timeText.setText(time);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText,dateText;
        CircleImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
            dateText = (TextView)itemView.findViewById(R.id.text_gchat_date_other);
        }

        void bind(Message message,int positon) {
            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using methods
            //lấy ngày dc tạo của tin nhắn
            cal.setTimeInMillis(message.getCreatedAt());
            SimpleDateFormat dft = null;
            dft = new SimpleDateFormat("hh:mm", Locale.getDefault());
            String time = dft.format(cal.getTime());
            //Định dạng ngày / tháng /năm
            if(positon == 0){
                dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String strDate = dft.format(cal.getTime());
                //hiển thị lên giao diện
                dateText.setText(strDate);
            }
            else if(messageList.get(positon).getCreatedAt() - messageList.get(positon-1).getCreatedAt() >= 1 * 1000 * 60 * 60 * 24){
                dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String strDate = dft.format(cal.getTime());
                //hiển thị lên giao diện
                dateText.setText(strDate);
            }
            else {
                dateText.setVisibility(View.GONE);
            }
            timeText.setText(time);
            nameText.setText(message.getSender());

            // Insert the profile image from the URL into the ImageView.
            //Thêm ảnh avt
            AccountDAO.getInstance().loadProfileImg(message.getUserUID(),profileImage);
        }
    }
}

