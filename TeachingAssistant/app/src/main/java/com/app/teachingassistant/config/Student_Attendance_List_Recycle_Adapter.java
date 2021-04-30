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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Student_Attendance_List_Recycle_Adapter extends RecyclerView.Adapter<Student_Attendance_List_Recycle_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<Attendance_Infor> attendance_list;

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
        TextView attendance_name,date;
        LinearLayout new_layout;
        Calendar cal = Calendar.getInstance();
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            new_layout = itemView.findViewById(R.id.attendance_new);
            attendance_name = itemView.findViewById(R.id.attendance_name);
            date = itemView.findViewById(R.id.attendace_date);


        }
        public  void bind(Attendance_Infor item,int position){
            attendance_name.setText(item.getName());
            if(position != 0){
                new_layout.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttendanceDAO.getInstance().setCurrentAttendance(item);
                    Intent intent = new Intent(mActivity, StudentAttendance.class);
                    mActivity.startActivity(intent);
                }
            });
            cal.setTimeInMillis(item.getCreateAt());
            SimpleDateFormat dft = null;
            dft = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            String dateStr = dft.format(cal.getTime());
            date.setText(dateStr);
        }

    }
}
