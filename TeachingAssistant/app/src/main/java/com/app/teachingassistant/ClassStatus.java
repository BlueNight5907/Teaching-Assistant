package com.app.teachingassistant;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.config.ClassStatusAdapter;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.app.teachingassistant.model.StudentBannedInfor;
import com.app.teachingassistant.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClassStatus extends AppCompatActivity {
    ActionBar actionBar;
    private RecyclerView recyclerView;
    private ArrayList<String> studentList = new ArrayList<>();
    private ArrayList<StudentBannedInfor> bannedList = new ArrayList<>();
    private ClassStatusAdapter classStatusAdapter;

    FirebaseUser user;
    DatabaseReference classRef,userRef,attendRef;

    Button btnOutputFile;
    Date dateOj;
    DateFormat dateFormat;

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

        btnOutputFile = (Button)findViewById(R.id.output_file);

        btnOutputFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdfFile();
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.student_status_list_item);

        user = FirebaseAuth.getInstance().getCurrentUser();
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        if (user == null) {
            finish();
        }

        classStatusAdapter = new ClassStatusAdapter(this, studentList, bannedList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(classStatusAdapter);
        binData();
    }

    private void createPdfFile() {
        PdfDocument myPdf = new PdfDocument();
        Paint myPaint = new Paint();

        dateOj = new Date();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1200,2010, 1).create();
        PdfDocument.Page myPage = myPdf.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();



    }

    private void binData() {

        if (ClassDAO.getInstance().getCurrentClass() == null) {
            return;
        }

        studentList = ClassDAO.getInstance().getCurrentClass().getStudentList();
        String keyId = ClassDAO.getInstance().getCurrentClass().getKeyID();
        ClassDAO.getInstance().loadAllStudentInAttendance(classRef.child(keyId), studentList, bannedList, classStatusAdapter);
    }


}
