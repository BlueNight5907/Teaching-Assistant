package com.app.teachingassistant.config;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.R;
import com.app.teachingassistant.StudentAttendance;
import com.app.teachingassistant.Teacher_attendance;
import com.app.teachingassistant.model.Attendance_Infor;

import java.util.ArrayList;

public class Teacher_Attendance_List_Recycle_Adapter extends RecyclerView.Adapter<Teacher_Attendance_List_Recycle_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<Attendance_Infor> attendance_list;

    public Teacher_Attendance_List_Recycle_Adapter(Activity activity,ArrayList attendance_list) {
        super();
        this.mActivity = activity;
        this.attendance_list = attendance_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mActivity).inflate(R.layout.teacher_attendance_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return attendance_list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView attendance_name,date,status;
        LinearLayout new_layout;
        Spinner option;
        String do_action[] = {"Chỉnh sửa","Xóa"};
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            new_layout = itemView.findViewById(R.id.attendance_new);
            attendance_name = itemView.findViewById(R.id.attendance_name);
            date = itemView.findViewById(R.id.attendace_date);
            status = itemView.findViewById(R.id.attendance_status);
            option = itemView.findViewById(R.id.more_option);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item,do_action);
            adapter.setDropDownViewResource(R.layout.spinner_list_item);
            option.setAdapter(adapter);
            option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, Teacher_attendance.class);
                    mActivity.startActivity(intent);
                }
            });
        }
    }
}
