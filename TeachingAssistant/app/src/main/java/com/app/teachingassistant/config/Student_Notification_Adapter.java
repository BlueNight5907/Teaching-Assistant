package com.app.teachingassistant.config;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.R;
import com.app.teachingassistant.model.Student_Notification_Infor;

import java.util.ArrayList;

public class Student_Notification_Adapter extends RecyclerView.Adapter<Student_Notification_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<Student_Notification_Infor> notification_List;

    public Student_Notification_Adapter(Activity activity,ArrayList notification_List) {
        super();
        this.mActivity = activity;
        this.notification_List = notification_List;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mActivity).inflate(R.layout.notification_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Bind các thông tin ở đây
    }

    @Override
    public int getItemCount() {
        return notification_List.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView notification_name,date,tag2,content,classname;
        Button tag1;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_name = itemView.findViewById(R.id.notification_name);
            date = itemView.findViewById(R.id.date);
            tag2 = itemView.findViewById(R.id.tag2);
            tag1 = itemView.findViewById(R.id.tag1);
            content = itemView.findViewById(R.id.notification_infor);
            classname = itemView.findViewById(R.id.class_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
