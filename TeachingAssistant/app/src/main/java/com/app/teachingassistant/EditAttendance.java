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
        actionBar.setTitle("Ch???nh s???a ??i???m danh");




        classKey = ClassDAO.getInstance().getCurrentClass().getKeyID();
        classRef = FirebaseDatabase.getInstance().getReference("Class").child(classKey);
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendances").child(classKey);
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        createAttend = findViewById(R.id.creater_class_btn);
        createAttend.setText("L??u");
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
                Toast.makeText(this,"Nh???p th???i gian kh??ng ????ng ?????nh d???ng",Toast.LENGTH_SHORT).show();
            }
            endAt = cal.getTimeInMillis();
            if(endAt < new Date().getTime()){
                loadingDialog.stopLoadingAlertDialog();
                makeToastLong("Th???i gian t???i h???n b?? h??n so v???i th???i gian kh???i t???o");
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
                        if(!temp.getKeyID().equals(AttendanceDAO.getInstance().getCurrentAttendance().getKeyID())){
                            loadingDialog.stopLoadingAlertDialog();
                            Toast.makeText(EditAttendance.this,"??i???m danh v???i t??n v???a nh???p ???? t???n t???i, vui l??ng ?????i t??n kh??c",Toast.LENGTH_SHORT).show();
                            return;
                        }
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
                            makeToastLong("C???p nh???t th??ng tin th??nh c??ng");
                        }
                        else {
                            makeToastLong("C???p nh???t th??ng tin th???t b???i");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.stopLoadingAlertDialog();
                        makeToastLong("???? c?? l???i x???y ra");
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
     * H??m l???y c??c th??ng s??? m???c ?????nh khi l???n ?????u ti???n ch???y ???ng d???ng
     */
    public void setTime(long time){
        //l???y ng??y hi???n t???i c???a h??? th???ng
        cal=Calendar.getInstance();
        cal.setTimeInMillis(time);
        SimpleDateFormat dft = null;
        //?????nh d???ng ng??y / th??ng /n??m
        dft =new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDate = dft.format(cal.getTime());
        //hi???n th??? l??n giao di???n
        txtDate.setText(strDate);
        dateFinish=cal.getTime();
        //?????nh d???ng gi??? ph??t am/pm
        dft=new SimpleDateFormat("hh:mm a",Locale.getDefault());
        String strTime=dft.format(cal.getTime());
        //????a l??n giao di???n
        txtTime.setText(strTime);
        //l???y gi??? theo 24 ????? l???p tr??nh theo Tag
        dft=new SimpleDateFormat("HH:mm",Locale.getDefault());
        txtTime.setTag(dft.format(cal.getTime()));
        //g??n cal.getTime() cho ng??y ho??n th??nh v?? gi??? ho??n th??nh
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
     * H??m hi???n th??? DatePicker dialog
     */
    public void showDatePickerDialog()
    {
        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                //M???i l???n thay ?????i ng??y th??ng n??m th?? c???p nh???t l???i TextView Date
                txtDate.setText(
                        (dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
                //L??u v???t l???i bi???n ng??y ho??n th??nh
                cal.set(year, monthOfYear, dayOfMonth);
                dateFinish = cal.getTime();
            }
        };
        //c??c l???nh d?????i n??y x??? l?? ng??y gi??? trong DatePickerDialog
        //s??? gi???ng v???i tr??n TextView khi m??? n?? l??n
        String s=txtDate.getText()+"";
        String strArrtmp[]=s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(
                EditAttendance.this,
                callback, nam, thang, ngay);
        pic.setTitle("Ch???n ng??y t???i h???n");
        pic.show();
    }
    /**
     * H??m hi???n th??? TimePickerDialog
     */
    public void showTimePickerDialog()
    {
        TimePickerDialog.OnTimeSetListener callback=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view,
                                  int hourOfDay, int minute) {
                //X??? l?? l??u gi??? v?? AM,PM
                String s=hourOfDay +":"+minute;
                int hourTam=hourOfDay;
                if(hourTam>12)
                    hourTam=hourTam-12;
                txtTime.setText
                        (hourTam +":"+minute +(hourOfDay>12?" PM":" AM"));
                //l??u gi??? th???c v??o tag
                txtTime.setTag(s);
                //l??u v???t l???i gi??? v??o hourFinish
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                hourFinish=cal.getTime();
            }
        };
        //c??c l???nh d?????i n??y x??? l?? ng??y gi??? trong TimePickerDialog
        //s??? gi???ng v???i tr??n TextView khi m??? n?? l??n
        String s=txtTime.getTag()+"";
        String strArr[]=s.split(":");
        int gio=Integer.parseInt(strArr[0]);
        int phut=Integer.parseInt(strArr[1]);
        TimePickerDialog time=new TimePickerDialog(
                EditAttendance.this,
                callback, gio, phut, true);
        time.setTitle("Ch???n gi???");
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
