package com.app.teachingassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.config.ClassStatusAdapter;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.app.teachingassistant.model.StudentBannedInfor;
import com.app.teachingassistant.model.StudentBannedList;
import com.app.teachingassistant.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClassStatus extends AppCompatActivity {
    ActionBar actionBar;
    private RecyclerView recyclerView;
    private ArrayList<String> studentList = new ArrayList<>();
    private ArrayList<StudentBannedInfor> bannedList = new ArrayList<>();
    private ArrayList<StudentBannedList> studentBannedLists = new ArrayList<>();
    private ClassStatusAdapter classStatusAdapter;

    FirebaseUser user;
    DatabaseReference classRef,userRef,attendRef;

    Button btnOutputFile;
    Date dateOj;
    DateFormat dateFormat;

    int pageWidth = 1500;
    private static final int PERMISSION_REQUEST_CODE = 100;

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

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        btnOutputFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            createPdfFile();
                        } else {
                            requestPermission(); // Code for permission
                        }
                    } else {
                        createPdfFile();
                    }
                }
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.student_status_list_item);

        user = FirebaseAuth.getInstance().getCurrentUser();
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        if (user == null) {
            finish();
        }

        classStatusAdapter = new ClassStatusAdapter(this, studentList, bannedList, studentBannedLists);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(classStatusAdapter);
        binData();
    }

    private void createPdfFile() {
        int count = 1, height;
        PdfDocument myPdf = new PdfDocument();
        Paint myPaint = new Paint();

        dateOj = new Date();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, 2500, 1).create();
        PdfDocument.Page myPage = myPdf.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        myPaint.setTextSize(70);
        canvas.drawText("DANH SÁCH ĐIỂM DANH", pageWidth/2, 50,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Teacher name:"+ClassDAO.getInstance().getCurrentClass().getTeacherName(), 20,100,myPaint );
        canvas.drawText("Class name: "+ClassDAO.getInstance().getCurrentClass().getClassName(), 20, 150, myPaint);

        myPaint.setTextAlign(Paint.Align.RIGHT);
        myPaint.setTextSize(35f);
        myPaint.setColor(Color.BLACK);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        canvas.drawText("Date create:"+ dateFormat.format(dateOj), pageWidth-20, 100, myPaint);
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        canvas.drawText("Create at:"+ dateFormat.format(dateOj), pageWidth-20, 150, myPaint);

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20, 180,  pageWidth-20, 260, myPaint);
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("Stt", 40, 240, myPaint);
        canvas.drawText("MSSV", 100, 240, myPaint);
        canvas.drawText("Họ và tên", 600, 240, myPaint);
        canvas.drawText("Vắng", 1200, 240, myPaint);
        canvas.drawText("Cấm thi", 1350, 240, myPaint);
        canvas.drawLine(80, 190, 80, 250, myPaint);
        canvas.drawLine(580, 190, 580, 250, myPaint);
        canvas.drawLine(1180, 190, 1180, 250, myPaint);
        canvas.drawLine(1330, 190, 1330, 250, myPaint);

        for(StudentBannedList item: studentBannedLists) {
            height = (count-1)*150;
            canvas.drawText(String.valueOf(count), 40, 300+height,myPaint);
            canvas.drawText(item.getUUID(), 100, 300+height, myPaint);
            canvas.drawText(item.getName(), 600, 300+height, myPaint);
            canvas.drawText(String.valueOf(item.getAbsentDates()), 1020, 300+height, myPaint);
            if (item.getState() == 0) {
                canvas.drawText("Không", 1350, 300+height, myPaint);
            }
            else {
                canvas.drawText("Cấm thi", 1350, 300+height, myPaint);
            }
            count += 1;
        }

        myPdf.finishPage(myPage);

        File file = new File(this.getExternalFilesDir("/"), "Danh sach cam thi.pdf");

        try {
            myPdf.writeTo(new FileOutputStream(file));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        myPdf.close();

        Toast.makeText(this, "Đã lưu tập tin", Toast.LENGTH_SHORT).show();
    }

    private void binData() {

        if (ClassDAO.getInstance().getCurrentClass() == null) {
            return;
        }

        studentList = ClassDAO.getInstance().getCurrentClass().getStudentList();
        String keyId = ClassDAO.getInstance().getCurrentClass().getKeyID();
        ClassDAO.getInstance().loadAllStudentInAttendance(classRef.child(keyId), studentList, bannedList, classStatusAdapter, studentBannedLists);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ClassStatus.this,android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result<= PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ClassStatus.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(ClassStatus.this, "Quyền Ghi Bộ nhớ ngoài cho phép chúng tôi đọc các tệp. Vui lòng cho phép quyền này trong Cài đặt Ứng dụng.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ClassStatus.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0]<= PackageManager.PERMISSION_GRANTED)  {
                } else {
                }
                break;
        }
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
