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

import com.app.teachingassistant.config.ClassStatusAdapter;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.app.teachingassistant.model.User;


import java.util.ArrayList;

public class ClassStatus extends AppCompatActivity {
    ActionBar actionBar;
    private RecyclerView recyclerView;
    private ArrayList<StudentAttendInfor> students;
    private ClassStatusAdapter adapter ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relative_output_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Tình trạng lớp học");

        recyclerView = (RecyclerView)findViewById(R.id.student_status_list_item);

        students = new ArrayList<>();

        mockData();

        Log.d("student list", ""+students);

        adapter = new ClassStatusAdapter(this, students);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
    }

    private void mockData() {
        for (int i = 1; i <=20; i++) {
            students.add(new StudentAttendInfor());
        }
    }
}
