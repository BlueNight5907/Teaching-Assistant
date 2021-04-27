package com.app.teachingassistant.DAO;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.app.teachingassistant.CreateAttendance_Auto;
import com.app.teachingassistant.CreateAttendance_Manual;
import com.app.teachingassistant.Teacher_attendance;
import com.app.teachingassistant.model.Attendance_Infor;
import com.app.teachingassistant.model.Class_Infor;
import com.app.teachingassistant.model.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AttendanceDAO {
    //Mẫu thiết kế singleton
    private static AttendanceDAO instance;
    private Attendance_Infor currentAttendance;
    private DatabaseReference ref,classRef,attendRef;
    private AttendanceDAO() {
        ref = FirebaseDatabase.getInstance().getReference("Users");
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        attendRef = FirebaseDatabase.getInstance().getReference("Attendances");
    }
    public static AttendanceDAO getInstance() {
        if(instance == null) {
            synchronized(AttendanceDAO.class) {
                if(null == instance) {
                    instance  = new AttendanceDAO();
                }
            }
        }
        return instance;
    }
    public void createAttendanceManual(DatabaseReference dtb, String keyID , Attendance_Infor attendanceInfor, CreateAttendance_Manual activity){

        dtb.child(keyID).setValue(attendanceInfor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                activity.closeDialog();
                if(!task.isSuccessful()){
                    activity.makeToastLong("Tạo điểm danh mới thất bại");
                }
                else {
                    activity.makeToastLong("Tạo điểm danh mới thành công");
                    activity.openAttendance(attendanceInfor);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activity.makeToastLong("Tạo điểm danh mới thất bại");
            }
        });
    }
    public void createAttendanceAuto(DatabaseReference dtb, String keyID , Attendance_Infor attendanceInfor, CreateAttendance_Auto activity){

        dtb.child(keyID).setValue(attendanceInfor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                activity.closeDialog();
                if(!task.isSuccessful()){
                    activity.makeToastLong("Tạo điểm danh mới thất bại");
                }
                else {
                    activity.makeToastLong("Tạo điểm danh mới thành công");
                    activity.openAttendance(attendanceInfor);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activity.makeToastLong("Tạo điểm danh mới thất bại");
            }
        });
    }
    public void setCurrentAttendance(Attendance_Infor attendance){
        this.currentAttendance = attendance;
    }
    public Attendance_Infor getCurrentAttendance(){
        return currentAttendance;
    }
}
