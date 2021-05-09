package com.app.teachingassistant;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AttendanceDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.config.Student_Adapter;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.NotificationInfor;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.app.teachingassistant.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Teacher_attendance extends AppCompatActivity {
    ActionBar actionBar;
    private RecyclerView recyclerView;
    private ArrayList<String> studentList = new ArrayList<>();
    private ArrayList<StudentAttendInfor> attendInforsList = new ArrayList<>();
    private Student_Adapter adapter ;
    Calendar cal = Calendar.getInstance();
    LinearLayout setEndHour;
    TextView className,header,describe,createAt,endAt;
    TextView totalStdTxt,attendedTxt,latedTxt,absentTxt,type;
    FirebaseUser user;
    Button savebtn;
    DatabaseReference classRef,userRef,attendRef;
    final LoadingDialog loadingDialog = new LoadingDialog(this);

    int attend = 0;
    int lated = 0;
    int absent = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_attendance);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
        classRef = FirebaseDatabase.getInstance().getReference("Class").child(ClassDAO.getInstance().getCurrentClass().getKeyID());
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        attendRef = FirebaseDatabase.getInstance().getReference("Attendances").child(ClassDAO.getInstance().getCurrentClass().getKeyID());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(AttendanceDAO.getInstance().getCurrentAttendance().getName());

        recyclerView = (RecyclerView)findViewById(R.id.teacher_attendance_recyclerview);
        totalStdTxt = findViewById(R.id.totalStd);
        attendedTxt = findViewById(R.id.attended);
        latedTxt = findViewById(R.id.lated);
        absentTxt = findViewById(R.id.absent_number);
        type = findViewById(R.id.type);
        savebtn = findViewById(R.id.save_attend_btn);

        className = findViewById(R.id.subject);
        header = findViewById(R.id.header);
        describe = findViewById(R.id.describle);
        createAt = findViewById(R.id.createAt);
        endAt = findViewById(R.id.endAt);
        setEndHour = findViewById(R.id.setEndHours);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChange();
            }
        });



        adapter = new Student_Adapter(this, studentList,attendInforsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        binData();
    }
    private void changeRecyclerHeight(){
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,120, getResources().getDisplayMetrics());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels + getNavigationBarHeight();
        recyclerView.getLayoutParams().height =(int)(height - pixels);


    }
    public void makeToastLong(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    private void saveChange(){
        long createAt = new Date().getTime();
        String className = ClassDAO.getInstance().getCurrentClass().getClassName();
        loadingDialog.startLoadingAlertDialog();
        Map<String,Object> map = new HashMap<>();
        for(StudentAttendInfor item:attendInforsList){
            map.put("Attendances/"+ClassDAO.getInstance().getCurrentClass().getKeyID()+"/"+AttendanceDAO.getInstance().getCurrentAttendance().getKeyID()+"/studentStateList/"+item.getUUID(),item);
            if(item.getState() < 0){
                NotificationInfor notificationInfor = new NotificationInfor(createAt,className,0,"Bạn đã vắng học vào "+AttendanceDAO.getInstance().getCurrentAttendance().getName());
                map.put("Notifications/"+item.getUUID()+"/"+AttendanceDAO.getInstance().getCurrentAttendance().getKeyID(),notificationInfor);
            }
            else{
                map.put("Notifications/"+item.getUUID()+"/"+AttendanceDAO.getInstance().getCurrentAttendance().getKeyID(),null);
            }
        }
        FirebaseDatabase.getInstance().getReference().updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.stopLoadingAlertDialog();
                if(task.isSuccessful()){
                    makeToastLong("Lưu điểm danh thành công");
                    finish();
                }
                else {
                    makeToastLong("Lưu điểm danh thất bại");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.stopLoadingAlertDialog();
                makeToastLong("Lỗi kết nối, Lưu điểm danh thất bại");
            }
        });

    }

    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
    private void binData(){
        if(AttendanceDAO.getInstance().getCurrentAttendance() == null){
            return;
        }
        className.setText(ClassDAO.getInstance().getCurrentClass().getClassName());
        header.setText(AttendanceDAO.getInstance().getCurrentAttendance().getName());
        describe.setText(AttendanceDAO.getInstance().getCurrentAttendance().getDescribe());
        cal.setTimeInMillis(AttendanceDAO.getInstance().getCurrentAttendance().getCreateAt());
        SimpleDateFormat dft = null;
        dft = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String time = dft.format(cal.getTime());
        dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String date = dft.format(cal.getTime());
        createAt.setText(time+" phút,  ngày "+date);
        if(AttendanceDAO.getInstance().getCurrentAttendance().getType().equals("manual")){
            setEndHour.setVisibility(View.GONE);
            type.setText("Thủ công/Giáo viên điểm danh");
        }
        else {
            cal.setTimeInMillis(AttendanceDAO.getInstance().getCurrentAttendance().getEndAt());
            dft = new SimpleDateFormat("hh:mm", Locale.getDefault());
            time = dft.format(cal.getTime());
            dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            date = dft.format(cal.getTime());
            endAt.setText(time+" phút,  ngày "+date);
        }

        studentList = ClassDAO.getInstance().getCurrentClass().getStudentList();
        if(studentList != null){
            totalStdTxt.setText(String.valueOf(studentList.size()));
        }else {
            totalStdTxt.setText("0");
        }
        String keyID = AttendanceDAO.getInstance().getCurrentAttendance().getKeyID();
        AttendanceDAO.getInstance().loadAllStudentInAttendance(attendRef.child(keyID),studentList,attendInforsList,adapter,Teacher_attendance.this);



    }
    public  void setStatistical(){
        this.attend = 0;
        this.lated = 0;
        this.absent = 0;
        for(StudentAttendInfor attendInfor:attendInforsList){

            if(attendInfor.getState() == 0){

                lated = lated + 1;
            }
            else if(attendInfor.getState() == 1){

                attend = attend + 1;
            }
            else {
                absent = absent+1;
            }
        }
        Log.d("thong ke", "loadAllStudentInAttendance: "+attendInforsList.size()+" "+attend+" "+absent+" "+lated);
        attendedTxt.setText(String.valueOf(attend));
        latedTxt.setText(String.valueOf(lated));
        absentTxt.setText(String.valueOf(absent));
    }
    public  void setStatistical(int attend,int lated, int absent){
        this.attend = attend;
        this.lated = lated;
        this.absent = absent;
        Log.d("thong ke", "loadAllStudentInAttendance: "+attendInforsList.size()+" "+attend+" "+absent+" "+lated);
        attendedTxt.setText(String.valueOf(attend));
        latedTxt.setText(String.valueOf(lated));
        absentTxt.setText(String.valueOf(absent));
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
