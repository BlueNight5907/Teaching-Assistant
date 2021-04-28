package com.app.teachingassistant;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.teachingassistant.DAO.AttendanceDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.Attendance_Infor;
import com.app.teachingassistant.model.Result;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateAttendance_Manual extends AppCompatActivity {
    ActionBar actionBar;
    Button createAttend;
    EditText header,description;
    FirebaseUser user;
    String classKey;
    final LoadingDialog loadingDialog = new LoadingDialog(this);
    DatabaseReference attendanceRef,classRef,userRef;
    private static CreateAttendance_Manual instance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_manual_attendance);
        instance = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Tạo điểm danh");

        createAttend = findViewById(R.id.creater_class_btn);
        createAttend.setText("Tiếp tục");
        header = findViewById(R.id.header);
        description = findViewById(R.id.description);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
        classKey = ClassDAO.getInstance().getCurrentClass().getKeyID();
        classRef = FirebaseDatabase.getInstance().getReference("Class").child(classKey);
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendances").child(classKey);
        userRef = FirebaseDatabase.getInstance().getReference("Users");


        header.addTextChangedListener(checkInput);
        description.addTextChangedListener(checkInput);

        createAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAttendance();
            }
        });


    }
    /**
     * Hàm lấy các thông số mặc định khi lần đầu tiền chạy ứng dụng
     */
    private void addAttendance(){
        loadingDialog.startLoadingAlertDialog();
        String headerText = header.getText().toString();
        String descText = description.getText().toString();
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item: snapshot.getChildren()){
                    Attendance_Infor temp = item.getValue(Attendance_Infor.class);
                    if(temp.getName().equals(headerText)){
                        loadingDialog.stopLoadingAlertDialog();
                        Toast.makeText(CreateAttendance_Manual.this,"Điểm danh với tên vừa nhập đã tồn tại, vui lòng đổi tên khác",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                long createAt = new Date().getTime();
                String keyID = attendanceRef.push().getKey();
                Attendance_Infor attendanceInfor = new Attendance_Infor(headerText,descText,createAt, createAt,"auto",keyID);
                AttendanceDAO.getInstance().createAttendanceManual(attendanceRef,keyID,attendanceInfor,CreateAttendance_Manual.this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void closeDialog(){
        loadingDialog.stopLoadingAlertDialog();
    }
    public void openAttendance(Attendance_Infor attendanceInfor){
        AttendanceDAO.getInstance().setCurrentAttendance(attendanceInfor);
        Intent intent = new Intent(this, Teacher_attendance.class);
        startActivity(intent);
        finish();
    }
    public void makeToastLong(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    public static CreateAttendance_Manual getInstance(){
        return instance;
    }
    private TextWatcher checkInput = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(header.length()>0 && description.length()>0){
                createAttend.setEnabled(true);
            }
            else {
                createAttend.setEnabled(false);
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
