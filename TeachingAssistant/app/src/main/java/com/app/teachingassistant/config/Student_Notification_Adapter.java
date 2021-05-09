package com.app.teachingassistant.config;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AttendanceDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.model.NotificationInfor;
import com.app.teachingassistant.model.Student_Notification_Infor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Student_Notification_Adapter extends RecyclerView.Adapter<Student_Notification_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<NotificationInfor> notification_List;

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
        holder.bind(notification_List.get(position),position);
    }

    @Override
    public int getItemCount() {
        return notification_List.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView notification_name,date,tag2,content,classname;
        Button tag1;
        Calendar cal = Calendar.getInstance();


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
        public void bind(NotificationInfor notificationInfor, int position){
            cal.setTimeInMillis(notificationInfor.getCreateAt());
            SimpleDateFormat dft = null;
            dft = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            String strDate = dft.format(cal.getTime());
            content.setText(notificationInfor.getContent());
            date.setText(strDate);
            if(position == 0){
                tag2.setVisibility(View.VISIBLE);
            }
            classname.setText(notificationInfor.getClassName());
            if(notificationInfor.getType() == 1){
                notification_name.setVisibility(View.INVISIBLE);
                tag1.setText("Cấm thi");
            }else {
                tag1.setText("Vắng học");
                notification_name.setText(notificationInfor.getContent().replace("Bạn đã vắng học vào ",""));
            }
        }
    }
}
