package com.app.teachingassistant.config;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.R;
import com.app.teachingassistant.model.Student_Infor;

import java.util.ArrayList;

public class Student_Adapter extends RecyclerView.Adapter<Student_Adapter.MyViewHolder> {
    private Activity context;
    private ArrayList<Student_Infor> students;

    public Student_Adapter(Activity context, ArrayList<Student_Infor> students) {
        super();
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.teacher_attendeancel_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Student_Infor student_infor = students.get(position);
        holder.name.setText(student_infor.getName());
        holder.image.setImageResource(R.drawable.user_avt);


    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.teacher_attendance_student_name_item);
            image = itemView.findViewById(R.id.teacher_attendance_image_item);
        }
    }
}

