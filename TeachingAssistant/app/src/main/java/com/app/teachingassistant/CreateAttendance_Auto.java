package com.app.teachingassistant;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateAttendance_Auto extends AppCompatActivity {
    ActionBar actionBar;
    LinearLayout linearLayout;
    TextView theme_name;
    FirebaseUser user;
    Button createAttend,changeDate,changeTime;
    Calendar cal;
    Date dateFinish;
    Date hourFinish;
    EditText header,desc;
    TextInputEditText txtDate,txtTime;
    final LoadingDialog loadingDialog = new LoadingDialog(this);
    DatabaseReference attendanceRef,classRef,userRef;
    String classKey;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_auto_attendance);
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
        createAttend.setText("Tạo");
        header = findViewById(R.id.header);
        desc = findViewById(R.id.description);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        changeDate = findViewById(R.id.changeDate);
        changeTime = findViewById(R.id.changeHours);
        getDefaultInfor();
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        classKey = ClassDAO.getInstance().getCurrentClass().getKeyID();
        classRef = FirebaseDatabase.getInstance().getReference("Class").child(classKey);
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendances").child(classKey);
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        header.addTextChangedListener(checkInput);
        desc.addTextChangedListener(checkInput);
        createAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAttendance();
            }
        });
    }
    private TextWatcher checkInput = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(header.length()>0 && desc.length()>0){
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
    private void addAttendance(){
        loadingDialog.startLoadingAlertDialog();
        String headerText = header.getText().toString();
        String descText = desc.getText().toString();

        String s=txtDate.getText()+"";
        String strArrtmp[]=s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);

        s=txtTime.getTag()+"";
        String strArr[]=s.split(":");
        int gio=Integer.parseInt(strArr[0]);
        int phut=Integer.parseInt(strArr[1]);

        try {
            cal = Calendar.getInstance();
            cal.set(nam,thang,ngay,gio,phut);
        }catch (Exception e){
            loadingDialog.stopLoadingAlertDialog();
            Toast.makeText(this,"Nhập thời gian không đúng định dạng",Toast.LENGTH_SHORT).show();
        }
        long endAt = cal.getTimeInMillis();
        if(endAt < new Date().getTime()){
            loadingDialog.stopLoadingAlertDialog();
            makeToastLong("Thời gian tới hạn bé hơn so với thời gian khỏi tạo");
            return;
        }
        attendanceRef.child(headerText).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!= null){
                    loadingDialog.stopLoadingAlertDialog();
                    Toast.makeText(CreateAttendance_Auto.this,"Điểm danh với tên vừa nhập đã tồn tại, vui lòng đổi tên khác",Toast.LENGTH_SHORT).show();
                }
                else {
                    long createAt = new Date().getTime();
                    Attendance_Infor attendanceInfor = new Attendance_Infor(headerText,descText,createAt,endAt,"auto");
                    AttendanceDAO.getInstance().createAttendanceAuto(attendanceRef,headerText,attendanceInfor,CreateAttendance_Auto.this);
                }
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
    /**
     * Hàm lấy các thông số mặc định khi lần đầu tiền chạy ứng dụng
     */
    public void getDefaultInfor()
    {
        //lấy ngày hiện tại của hệ thống
        cal=Calendar.getInstance();
        SimpleDateFormat dft = null;
        //Định dạng ngày / tháng /năm
        dft =new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDate = dft.format(cal.getTime());
        //hiển thị lên giao diện
        txtDate.setText(strDate);
        dateFinish=cal.getTime();
        //Định dạng giờ phút am/pm
        dft=new SimpleDateFormat("hh:mm a",Locale.getDefault());
        String strTime=dft.format(cal.getTime());
        //đưa lên giao diện
        txtTime.setText(strTime);
        //lấy giờ theo 24 để lập trình theo Tag
        dft=new SimpleDateFormat("HH:mm",Locale.getDefault());
        txtTime.setTag(dft.format(cal.getTime()));
        //gán cal.getTime() cho ngày hoàn thành và giờ hoàn thành

        hourFinish=cal.getTime();
    }
    /**
     * Hàm hiển thị DatePicker dialog
     */
    public void showDatePickerDialog()
    {
        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                //Mỗi lần thay đổi ngày tháng năm thì cập nhật lại TextView Date
                txtDate.setText(
                        (dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
                //Lưu vết lại biến ngày hoàn thành
                cal.set(year, monthOfYear, dayOfMonth);
                dateFinish = cal.getTime();
            }
        };
        //các lệnh dưới này xử lý ngày giờ trong DatePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s=txtDate.getText()+"";
        String strArrtmp[]=s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(
                CreateAttendance_Auto.this,
                callback, nam, thang, ngay);
        pic.setTitle("Chọn ngày tới hạn");
        pic.show();
    }
    /**
     * Hàm hiển thị TimePickerDialog
     */
    public void showTimePickerDialog()
    {
        TimePickerDialog.OnTimeSetListener callback=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view,
                                  int hourOfDay, int minute) {
                //Xử lý lưu giờ và AM,PM
                String s=hourOfDay +":"+minute;
                int hourTam=hourOfDay;
                if(hourTam>12)
                    hourTam=hourTam-12;
                txtTime.setText
                        (hourTam +":"+minute +(hourOfDay>12?" PM":" AM"));
                //lưu giờ thực vào tag
                txtTime.setTag(s);
                //lưu vết lại giờ vào hourFinish
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                hourFinish=cal.getTime();
            }
        };
        //các lệnh dưới này xử lý ngày giờ trong TimePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s=txtTime.getTag()+"";
        String strArr[]=s.split(":");
        int gio=Integer.parseInt(strArr[0]);
        int phut=Integer.parseInt(strArr[1]);
        TimePickerDialog time=new TimePickerDialog(
                CreateAttendance_Auto.this,
                callback, gio, phut, true);
        time.setTitle("Chọn giờ");
        time.show();
    }

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
