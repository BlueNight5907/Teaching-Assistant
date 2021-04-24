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

public class ClassStatusAdapter extends RecyclerView.Adapter<ClassStatusAdapter.MyViewHolder> {
    private Activity context;
    private ArrayList<Student_Infor> students;

    public ClassStatusAdapter(Activity context, ArrayList<Student_Infor> students) {
        super();
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.class_status_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Student_Infor student_infor = students.get(position);
        holder.stt.setText(String.valueOf(position));
        holder.name.setText(student_infor.getName());
        holder.image.setImageResource(R.drawable.user_avt);


    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, stt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stt = itemView.findViewById(R.id.stt);
            name = itemView.findViewById(R.id.student_status_name_item);
            image = itemView.findViewById(R.id.student_status_image_item);
        }
    }
}

