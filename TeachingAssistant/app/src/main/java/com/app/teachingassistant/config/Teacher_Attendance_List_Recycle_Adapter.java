package com.app.teachingassistant.config;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AttendanceDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.EditAttendance;
import com.app.teachingassistant.R;
import com.app.teachingassistant.StudentAttendance;
import com.app.teachingassistant.Teacher_attendance;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.Attendance_Infor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
        holder.bind(attendance_list.get(position),position);
    }

    @Override
    public int getItemCount() {
        return attendance_list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView attendance_name,date,status;
        Spinner option;
        String do_action[] = {"Đóng","Chỉnh sửa","Xóa"};
        Calendar cal= Calendar.getInstance();
        final LoadingDialog loadingDialog = new LoadingDialog(mActivity);
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            attendance_name = itemView.findViewById(R.id.attendance_name);
            date = itemView.findViewById(R.id.attendace_date);
            status = itemView.findViewById(R.id.attendance_status);
            option = itemView.findViewById(R.id.more_option);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item,do_action);
            adapter.setDropDownViewResource(R.layout.spinner_list_item);
            option.setAdapter(adapter);


        }
        private int changeDptoPx(int dp){
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, mActivity.getResources().getDisplayMetrics());
            return (int)pixels;
        }
        public void bind(Attendance_Infor attendanceInfor, int positon){
            attendance_name.setText(attendanceInfor.getName());
            int totalStudent = 0;
            if(ClassDAO.getInstance().getCurrentClass().getStudentList() != null){
                totalStudent =  ClassDAO.getInstance().getCurrentClass().getStudentList().size();
            }
            cal.setTimeInMillis(attendanceInfor.getCreateAt());
            SimpleDateFormat dft = null;
            //Định dạng ngày / tháng /năm
            dft =new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String strDate = dft.format(cal.getTime());
            date.setText(strDate);
            status.setText("0/"+totalStudent);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttendanceDAO.getInstance().setCurrentAttendance(attendanceInfor);
                    Intent intent = new Intent(mActivity, Teacher_attendance.class);
                    mActivity.startActivity(intent);
                }
            });
            option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 1){
                        AttendanceDAO.getInstance().setCurrentAttendance(attendanceInfor);
                        Intent intent = new Intent(mActivity, EditAttendance.class);
                        mActivity.startActivity(intent);
                    }
                    else if(position == 2){
                        showDialog(attendanceInfor,positon);
                        option.setSelection(0);

                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
                private void showDialog(Attendance_Infor attendanceInfor, int position){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    View customview = inflater.inflate(R.layout.normal_dialog,null);
                    builder.setView(customview);
                    Button OK = customview.findViewById(R.id.ok_button);
                    Button Cancel = customview.findViewById(R.id.not_ok_button);;
                    TextView textView = customview.findViewById(R.id.dialog_message);
                    textView.setText("Bạn có muốn xóa buổi điểm danh này không");
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
                            DatabaseReference attendRef = FirebaseDatabase.getInstance().getReference("Attendances").child(ClassDAO.getInstance().getCurrentClass().getKeyID());
                            attendRef.child(attendanceInfor.getKeyID()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingDialog.stopLoadingAlertDialog();
                                    if(task.isSuccessful()){
                                        Toast.makeText(mActivity,"Xóa điểm danh thành công",Toast.LENGTH_SHORT).show();
                                        attendance_list.remove(position);
                                        notifyDataSetChanged();
                                    }
                                    else {
                                        Toast.makeText(mActivity,"Xóa điểm danh thất bại",Toast.LENGTH_SHORT).show();
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
            });
        }
    }
}
