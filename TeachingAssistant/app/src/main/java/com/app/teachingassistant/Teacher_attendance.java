package com.app.teachingassistant;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.config.Student_Adapter;
import com.app.teachingassistant.model.Student_Infor;

import java.util.ArrayList;

public class Teacher_attendance extends AppCompatActivity {
    ActionBar actionBar;
    private RecyclerView recyclerView;
    private ArrayList<Student_Infor> students;
    private Student_Adapter adapter ;

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

        mockData();

        Log.d("student list", ""+students);
        adapter = new Student_Adapter(this, students);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
    }

    private void mockData() {
        for (int i = 1; i <=10; i++) {
            students.add(new Student_Infor("Nguyen Thanh Luan"+i));
        }
    }
}
