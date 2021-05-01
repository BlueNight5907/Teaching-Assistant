package com.app.teachingassistant.config;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.fragment.Teacher_People_Fragment;
import com.app.teachingassistant.model.Attendance_Infor;
import com.app.teachingassistant.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Student_List_In_TeacherView_Recycler_Adapter extends RecyclerView.Adapter<Student_List_In_TeacherView_Recycler_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<User> studentList;


    public Student_List_In_TeacherView_Recycler_Adapter(Activity activity, ArrayList studentList) {
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
        LoadingDialog loadingDialog = new LoadingDialog(mActivity);
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            option = itemView.findViewById(R.id.class_people_item_more_option);
            student_name = itemView.findViewById(R.id.people_name);
            img = itemView.findViewById(R.id.people_img);
            ArrayAdapter<String>adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item,do_action);
            adapter.setDropDownViewResource(R.layout.spinner_list_item);
            option.setAdapter(adapter);
            

        }
        public void bind(User user,int pos){
            if(user == null)
                return;
            student_name.setText(user.getName());
            if(user.isHasProfileUrl()){
                AccountDAO.getInstance().loadProfileImg(user.getUUID(),img);
            }
            option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 1){
                        showDialog(user, pos);
                        option.setSelection(0);
                    }

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        private int changeDptoPx(int dp){
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, mActivity.getResources().getDisplayMetrics());
            return (int)pixels;
        }
        private void showDialog(User user, int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View customview = inflater.inflate(R.layout.normal_dialog,null);
            builder.setView(customview);
            Button OK = customview.findViewById(R.id.ok_button);
            Button Cancel = customview.findViewById(R.id.not_ok_button);;
            TextView textView = customview.findViewById(R.id.dialog_message);
            textView.setText("Bạn có muốn xóa sinh viên này không");
            AlertDialog alert = builder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.animation;
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alert.show();
            alert.getWindow().setLayout(changeDptoPx(280), changeDptoPx(140));
            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            // Nút Ok
            OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    loadingDialog.startLoadingAlertDialog();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ArrayList<String> students = ClassDAO.getInstance().getCurrentClass().getStudentList();
                    students.remove(position);
                    Map<String,Object> map = new HashMap<>();
                    map.put("Class/"+ClassDAO.getInstance().getCurrentClass().getKeyID()+"/studentList",students);
                    map.put("Users/"+user.getUUID()+"/ClassListAttended/"+ClassDAO.getInstance().getCurrentClass().getKeyID(),null);
                    ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingDialog.stopLoadingAlertDialog();
                            if(task.isSuccessful()){
                                Toast.makeText(mActivity,"Xóa học sinh thành công",Toast.LENGTH_SHORT).show();
                                studentList.remove(position);
                                notifyDataSetChanged();
                            }
                            else {
                                Toast.makeText(mActivity,"Xóa học sinh thất bại",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.startLoadingAlertDialog();
                            Toast.makeText(mActivity,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}
