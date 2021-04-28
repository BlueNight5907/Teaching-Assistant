package com.app.teachingassistant.config;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.Teacher_attendance;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.app.teachingassistant.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Student_Adapter extends RecyclerView.Adapter<Student_Adapter.MyViewHolder> {
    private Activity context;
    private ArrayList<String> students;
    private ArrayList<StudentAttendInfor> attendInfors;

    public Student_Adapter(Activity context, ArrayList<String> students,ArrayList<StudentAttendInfor> attendInfors) {
        super();
        this.context = context;
        this.students = students;
        this.attendInfors = attendInfors;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.teacher_attendeancel_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(attendInfors.get(position),position);


    }

    @Override
    public int getItemCount() {
        return attendInfors.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name,No;
        RadioButton present,lated,absent;
        int status = -1;

        private  void changeData(StudentAttendInfor data,int positon){
            data.setState(status);
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.teacher_attendance_student_name_item);
            image = itemView.findViewById(R.id.teacher_attendance_image_item);
            No = itemView.findViewById(R.id.number);
            present =  itemView.findViewById(R.id.checkbox_present);
            lated = itemView.findViewById(R.id.checkbox_late);
            absent = itemView.findViewById(R.id.checkbox_absent);
        }
        public  void bind(StudentAttendInfor data, int positon){
            String UUID = data.getUUID();
            No.setText(String.valueOf(positon));
            FirebaseDatabase.getInstance().getReference("Users").child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null){
                        User user = snapshot.getValue(User.class);
                        name.setText(user.getName());
                        if(user.isHasProfileUrl()){
                            AccountDAO.getInstance().loadProfileImg(UUID,image);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(present.isChecked()){
                        status = 1;
                        changeData(data,positon);
                    }
                    else if(absent.isChecked()){
                        status = -1;
                        changeData(data,positon);
                    }
                    else {
                        status = 0;
                        changeData(data,positon);
                    }
                    ((Teacher_attendance)context).setStatistical();
                }
            };
            int presentState = data.getState();
            if(presentState == 0){
                lated.setChecked(true);
            }
            else if(presentState == 1){
                present.setChecked(true);
            }
            else if(presentState == -1){
                absent.setChecked(true);
            }
            present.setOnCheckedChangeListener(check);
            absent.setOnCheckedChangeListener(check);
            lated.setOnCheckedChangeListener(check);
        }
    }
}

