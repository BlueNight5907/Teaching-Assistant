package com.app.teachingassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.AttendanceDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.config.Student_Attendance_List_Recycle_Adapter;
import com.app.teachingassistant.config.Student_Home_Classlist_Recycle_Adapter;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.fragment.Student_Home_Fragment;
import com.app.teachingassistant.fragment.Student_People_Fragment;
import com.app.teachingassistant.model.Attendance_Infor;
import com.app.teachingassistant.model.Class_Infor;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.app.teachingassistant.model.StudentBannedInfor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAttendance extends AppCompatActivity {
    CircleImageView userAvt;
    ActionBar actionBar;
    TextView subjectTxt,attendName,timeCreateAt,timeEnd,stateTxt;
    Calendar cal = Calendar.getInstance();
    Button attendanceBtn;
    StudentAttendInfor studentAttendInfor;
    DatabaseReference attendRef;
    LoadingDialog loadingDialog = new LoadingDialog(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_attendance);
        userAvt = findViewById(R.id.user_logo_toolbar);
        userAvt.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Điểm danh");

        subjectTxt = findViewById(R.id.subject);
        attendName = findViewById(R.id.attendance_name);
        timeCreateAt = findViewById(R.id.createAt);
        timeEnd = findViewById(R.id.endAt);
        stateTxt = findViewById(R.id.attendance_state);
        attendanceBtn = findViewById(R.id.attendance_btn);
        attendRef = FirebaseDatabase.getInstance().getReference("Attendances").child(ClassDAO.getInstance().getCurrentClass().getKeyID())
                .child(AttendanceDAO.getInstance().getCurrentAttendance().getKeyID()).child("studentStateList");



        bind();
        attendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState();
            }
        });








    }
    public void makeToastLong(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    private void changeState(){
        if(studentAttendInfor.getState()!=1){
            loadingDialog.startLoadingAlertDialog();

            attendRef.child(studentAttendInfor.getUUID()).child("state").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    loadingDialog.stopLoadingAlertDialog();
                    if(task.isSuccessful()){
                        makeToastLong("Điểm danh thành công");
                        studentAttendInfor.setState(1);
                        acceptChangeState(studentAttendInfor);

                    }else {
                        makeToastLong("Điểm danh thất bại");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingDialog.startLoadingAlertDialog();
                    makeToastLong("Điểm danh thất bại");
                }
            });

            return;
        }
        showDialog();


    }
    private void bind(){
        subjectTxt.setText(ClassDAO.getInstance().getCurrentClass().getClassName());
        attendName.setText(AttendanceDAO.getInstance().getCurrentAttendance().getName());
        long now = new Date().getTime();
        long timeCreate = AttendanceDAO.getInstance().getCurrentAttendance().getCreateAt();
        long timeEndAt = AttendanceDAO.getInstance().getCurrentAttendance().getEndAt();
        Map<String,StudentAttendInfor> studentAttendInforList = (Map)AttendanceDAO.getInstance().getCurrentAttendance().getStudentStateList();
        studentAttendInfor = studentAttendInforList.get(AccountDAO.getInstance().getCurrentUser().getUUID());
        if(studentAttendInfor == null){
            studentAttendInfor = new StudentAttendInfor(AccountDAO.getInstance().getCurrentUser().getUUID(),-2);
        }
        bindAttendaneState(studentAttendInfor.getState());

        if(now - timeCreate < 1* 1000 * 60 * 60 * 3){
            cal.setTimeInMillis(now-timeCreate);
            SimpleDateFormat dft = null;
            dft =new SimpleDateFormat("hh:mm", Locale.getDefault());
            String strTime = dft.format(cal.getTime());
            timeCreateAt.setText(strTime+" phút trước");
            cal.setTimeInMillis(timeCreate);
        }else {
            cal.setTimeInMillis(timeCreate);
            SimpleDateFormat dft = null;
            //Định dạng ngày / tháng /năm
            dft =new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String strDate = dft.format(cal.getTime());

            dft =new SimpleDateFormat("HH:mm", Locale.getDefault());
            String strTime = dft.format(cal.getTime());
            timeCreateAt.setText("Được tạo vào:  "+strTime+" phút, ngày "+strDate);
        }
        if(AttendanceDAO.getInstance().getCurrentAttendance().getType().equals("manual")){
            Log.d("hello", "bind: hello");
            timeEnd.setVisibility(View.GONE);
            attendanceBtn.setEnabled(false);
            attendanceBtn.setText("Bạn không có quyền thay đổi");
            attendanceBtn.setBackground(getDrawable(R.drawable.un_attendance_btn));
        }else {
            cal.setTimeInMillis(timeEndAt);
            SimpleDateFormat dft = null;
            //Định dạng ngày / tháng /năm
            dft =new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String strDate = dft.format(cal.getTime());

            dft =new SimpleDateFormat("HH:mm", Locale.getDefault());
            String strTime = dft.format(cal.getTime());
            timeEnd.setText("Đến hạn vào:  "+strTime+" phút, ngày "+strDate);
            if(timeEndAt<now){
                unAcceptChangeState();
            }else {
                acceptChangeState(studentAttendInfor);
            }

        }


    }
    private int changeDptoPx(int dp){
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, getResources().getDisplayMetrics());
        return (int)pixels;
    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customview = inflater.inflate(R.layout.normal_dialog, null);
        builder.setView(customview);
        Button OK = customview.findViewById(R.id.ok_button);
        Button Cancel = customview.findViewById(R.id.not_ok_button);
        TextView textView = customview.findViewById(R.id.dialog_message);
        textView.setText("Bạn có chắc muốn hủy điểm danh");
        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.animation;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
        alert.getWindow().setLayout(changeDptoPx(320), changeDptoPx(150));
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
                attendRef.child(studentAttendInfor.getUUID()).child("state").setValue(-2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.stopLoadingAlertDialog();
                        if(task.isSuccessful()){
                            makeToastLong("Hủy điểm danh thành công");
                            studentAttendInfor.setState(-2);
                            acceptChangeState(studentAttendInfor);

                        }else {
                            makeToastLong("Hủy điểm danh thất bại");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.startLoadingAlertDialog();
                        makeToastLong("Hủy điểm danh thất bại");
                    }
                });
            }
        });

    }
    private void acceptChangeState(StudentAttendInfor studentAttendInfor){
        attendanceBtn.setEnabled(true);

        if(studentAttendInfor.getState() < 1){
            attendanceBtn.setText("Điểm danh");

            attendanceBtn.setBackground(getDrawable(R.drawable.attendance_btn));
        }
        else {
            attendanceBtn.setText("Hủy điểm danh");
            attendanceBtn.setBackground(getDrawable(R.drawable.un_attendance_btn));
        }


    }
    private void unAcceptChangeState(){
        attendanceBtn.setEnabled(false);
        attendanceBtn.setText("Bạn không có quyền thay đổi");
        attendanceBtn.setBackground(getDrawable(R.drawable.un_attendance_btn));
    }
    private void bindAttendaneState(int state){
        switch (state){
            case -2:
                stateTxt.setText("Chưa điểm danh");
                stateTxt.setTextColor(getResources().getColor(R.color.soothing_breeze));
                return;
            case -1:
                stateTxt.setText("Vắng học");
                stateTxt.setTextColor(getResources().getColor(R.color.chi_gong));
                return;
            case 0:
                stateTxt.setText("Đi trễ");
                stateTxt.setTextColor(getResources().getColor(R.color.bright_yarrow));
                return;
            case 1:
                stateTxt.setText("Đã điểm danh");
                return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
