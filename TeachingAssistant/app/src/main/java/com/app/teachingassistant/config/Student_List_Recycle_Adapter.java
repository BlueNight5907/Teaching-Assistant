package com.app.teachingassistant.config;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.teachingassistant.R;
import com.app.teachingassistant.model.Student_Infor;
import com.app.teachingassistant.model.User;

import java.util.ArrayList;

public class Student_List_Recycle_Adapter extends RecyclerView.Adapter<Student_List_Recycle_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<User> studentList;

    public Student_List_Recycle_Adapter(Activity activity,ArrayList studentList) {
        super();
        this.mActivity = activity;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mActivity).inflate(R.layout.list_person_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.student_name.setText(String.valueOf(studentList.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView student_name;
        Spinner option;
        String do_action[] = {"Hủy đăng ký"};
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            option = itemView.findViewById(R.id.class_people_item_more_option);
            student_name = itemView.findViewById(R.id.people_name);
            option.setVisibility(View.GONE);
        }
    }
}
