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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditAttendance extends AppCompatActivity {
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
    Spinner attendanceType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Chỉnh sửa điểm danh");




        classKey = ClassDAO.getInstance().getCurrentClass().getKeyID();
        classRef = FirebaseDatabase.getInstance().getReference("Class").child(classKey);
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendances").child(classKey);
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        createAttend = findViewById(R.id.creater_class_btn);
        createAttend.setText("Lưu");
        header = findViewById(R.id.header);
        desc = findViewById(R.id.description);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        changeDate = findViewById(R.id.changeDate);
        changeTime = findViewById(R.id.changeHours);
        attendanceType = findViewById(R.id.attendaceType);

        linearLayout = findViewById(R.id.setTime);
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

        header.addTextChangedListener(checkInput);
        desc.addTextChangedListener(checkInput);
        createAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAttendance();
            }
        });
        attendanceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    linearLayout.setVisibility(View.GONE);
                }
                else{
                    long time = AttendanceDAO.getInstance().getCurrentAttendance().getEndAt();
                    setTime(time);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getDefaultInfor();
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


        String type = "auto";
        if(attendanceType.getSelectedItemPosition() == 0){
            type = "manual";
        }
        long endAt = AttendanceDAO.getInstance().getCurrentAttendance().getCreateAt();
        if(type.equals("auto")){
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
            endAt = cal.getTimeInMillis();
            if(endAt < new Date().getTime()){
                loadingDialog.stopLoadingAlertDialog();
                makeToastLong("Thời gian tới hạn bé hơn so với thời gian khỏi tạo");
                return;
            }
        }
        Map<String,Object> map = new HashMap<>();

        long finalEndAt = endAt;
        String finalType = type;
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item: snapshot.getChildren()){
                    Attendance_Infor temp = item.getValue(Attendance_Infor.class);
                    if(temp.getName().equals(headerText)){
                        loadingDialog.stopLoadingAlertDialog();
                        Toast.makeText(EditAttendance.this,"Điểm danh với tên vừa nhập đã tồn tại, vui lòng đổi tên khác",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                long createAt = AttendanceDAO.getInstance().getCurrentAttendance().getCreateAt();
                String keyID = AttendanceDAO.getInstance().getCurrentAttendance().getKeyID();
                Attendance_Infor attendanceInfor = new Attendance_Infor(headerText,descText,createAt, finalEndAt, finalType,keyID,AttendanceDAO.getInstance().getCurrentAttendance().getStudentStateList());
                map.put(keyID,attendanceInfor);
                attendanceRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.stopLoadingAlertDialog();
                        if(task.isSuccessful()){
                            makeToastLong("Cập nhật thông tin thành công");
                        }
                        else {
                            makeToastLong("Cập nhật thông tin thất bại");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.stopLoadingAlertDialog();
                        makeToastLong("Đã có lỗi xảy ra");
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.stopLoadingAlertDialog();
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
    public void setTime(long time){
        //lấy ngày hiện tại của hệ thống
        cal=Calendar.getInstance();
        cal.setTimeInMillis(time);
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
    public void getDefaultInfor()
    {
        /////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        header.setText(AttendanceDAO.getInstance().getCurrentAttendance().getName());
        desc.setText(AttendanceDAO.getInstance().getCurrentAttendance().getDescribe());
        String type = AttendanceDAO.getInstance().getCurrentAttendance().getType();
        if(type.equals("manual")){
            linearLayout.setVisibility(View.GONE);
            attendanceType.setSelection(0);
        }else {
            attendanceType.setSelection(0);
            long time = AttendanceDAO.getInstance().getCurrentAttendance().getEndAt();
            setTime(time);
        }
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
                EditAttendance.this,
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
                EditAttendance.this,
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
