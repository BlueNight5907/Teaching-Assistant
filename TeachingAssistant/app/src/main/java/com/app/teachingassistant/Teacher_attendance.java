package com.app.teachingassistant;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.app.teachingassistant.config.Student_Home_Classlist_Recycle_Adapter;
import com.app.teachingassistant.model.Student_Infor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Teacher_attendance extends AppCompatActivity {
    ActionBar actionBar;
    private RecyclerView recyclerView;
    private ArrayList<Student_Infor> students;
    private Student_Adapter adapter ;
    Calendar cal = Calendar.getInstance();
    LinearLayout setEndHour;
    TextView className,header,describe,createAt,endAt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_attendance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Điểm danh buổi 10");

        recyclerView = (RecyclerView)findViewById(R.id.teacher_attendance_recyclerview);


        students = new ArrayList<>();
        className = findViewById(R.id.subject);
        header = findViewById(R.id.header);
        describe = findViewById(R.id.describle);
        createAt = findViewById(R.id.createAt);
        endAt = findViewById(R.id.endAt);
        setEndHour = findViewById(R.id.setEndHours);
        binData();
        genMock();
        Log.d("student list", ""+students);
        adapter = new Student_Adapter(this, students);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
    private void changeRecyclerHeight(){
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,120, getResources().getDisplayMetrics());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels + getNavigationBarHeight();
        recyclerView.getLayoutParams().height =(int)(height - pixels);


    }
    private void genMock(){
        students = new ArrayList<>();
        for(int i =0 ; i<20;i++){
            students.add(new Student_Infor());
        }
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
        describe.setText(AttendanceDAO.getInstance().getCurrentAttendance().getName());
        cal.setTimeInMillis(AttendanceDAO.getInstance().getCurrentAttendance().getCreateAt());
        SimpleDateFormat dft = null;
        dft = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String time = dft.format(cal.getTime());
        dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String date = dft.format(cal.getTime());
        createAt.setText(time+" phút,  ngày "+date);
        if(AttendanceDAO.getInstance().getCurrentAttendance().getType().equals("manual")){
            setEndHour.setVisibility(View.GONE);
        }
        else {
            cal.setTimeInMillis(AttendanceDAO.getInstance().getCurrentAttendance().getEndAt());
            dft = new SimpleDateFormat("hh:mm", Locale.getDefault());
            time = dft.format(cal.getTime());
            dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            date = dft.format(cal.getTime());
            endAt.setText(time+" phút,  ngày "+date);
        }

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
