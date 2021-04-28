package com.app.teachingassistant.config;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AttendanceDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.StudentAttendance;
import com.app.teachingassistant.model.Attendance_Infor;

import java.util.ArrayList;

public class Student_Attendance_List_Recycle_Adapter extends RecyclerView.Adapter<Student_Attendance_List_Recycle_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<Attendance_Infor> attendance_list;
    private String a = "5";

    public Student_Attendance_List_Recycle_Adapter(Activity activity,ArrayList attendance_list) {
        super();
        this.mActivity = activity;
        this.attendance_list = attendance_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mActivity).inflate(R.layout.student_attendance_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(attendance_list.get(position),position);
    }

    @Override
    public int getItemCount() {
        return attendance_list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView attendance_name,date,status;
        LinearLayout new_layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            new_layout = itemView.findViewById(R.id.attendance_new);
            attendance_name = itemView.findViewById(R.id.attendance_name);
            date = itemView.findViewById(R.id.attendace_date);
            status = itemView.findViewById(R.id.attendance_status);

        }
        public  void bind(Attendance_Infor item,int position){
            attendance_name.setText(item.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttendanceDAO.getInstance().setCurrentAttendance(item);
                    Intent intent = new Intent(mActivity, StudentAttendance.class);
                    mActivity.startActivity(intent);
                }
            });
        }

    }
}
