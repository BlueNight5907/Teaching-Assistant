package com.app.teachingassistant.config;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.model.Student_Infor;
import com.app.teachingassistant.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Student_List_In_TeacherView_Recycler_Adapter extends RecyclerView.Adapter<Student_List_In_TeacherView_Recycler_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<User> studentList;

    public Student_List_In_TeacherView_Recycler_Adapter(Activity activity,ArrayList studentList) {
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
        holder.bind(studentList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView student_name;
        Spinner option;
        CircleImageView img;
        String do_action[] = {"Đóng","Xóa sinh viên"};
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            option = itemView.findViewById(R.id.class_people_item_more_option);
            student_name = itemView.findViewById(R.id.people_name);
            img = itemView.findViewById(R.id.people_img);
            ArrayAdapter<String>adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item,do_action);
            adapter.setDropDownViewResource(R.layout.spinner_list_item);
            option.setAdapter(adapter);
            option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 1){

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
        public void bind(User user,int position){
            if(user == null)
                return;
            student_name.setText(user.getName());
            if(user.isHasProfileUrl()){
                AccountDAO.getInstance().loadProfileImg(user.getUUID(),img);
            }
        }
    }
}
