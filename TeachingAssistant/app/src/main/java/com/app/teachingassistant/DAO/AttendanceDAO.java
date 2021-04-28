package com.app.teachingassistant.DAO;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.teachingassistant.CreateAttendance_Auto;
import com.app.teachingassistant.CreateAttendance_Manual;
import com.app.teachingassistant.Teacher_attendance;
import com.app.teachingassistant.config.Student_Adapter;
import com.app.teachingassistant.config.Teacher_Attendance_List_Recycle_Adapter;
import com.app.teachingassistant.fragment.Teacher_Attendance_Fragment;
import com.app.teachingassistant.fragment.Teacher_Home_Fragment;
import com.app.teachingassistant.model.Attendance_Infor;
import com.app.teachingassistant.model.Class_Infor;
import com.app.teachingassistant.model.Result;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    public void loadAllAttendancesList(DatabaseReference ref, ArrayList<Attendance_Infor> list, Teacher_Attendance_List_Recycle_Adapter teacher_attendance_list_recycle_adapter, Teacher_Attendance_Fragment teacher_attendance_fragment){
        ref.orderByChild("createAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item:snapshot.getChildren()){
                    list.add(0,item.getValue(Attendance_Infor.class));
                    teacher_attendance_list_recycle_adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                teacher_attendance_fragment.makeToastLong("Tải lên danh sách điểm danh thất bại");
            }
        });
    }
    public void loadAllAttendancesListHome(DatabaseReference ref, ArrayList<Attendance_Infor> list, Teacher_Attendance_List_Recycle_Adapter teacher_attendance_list_recycle_adapter, Teacher_Home_Fragment teacher_attendance_fragment){
        ref.orderByChild("createAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item:snapshot.getChildren()){
                    list.add(0,item.getValue(Attendance_Infor.class));
                    teacher_attendance_list_recycle_adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                teacher_attendance_fragment.makeToastLong("Tải lên danh sách điểm danh thất bại");
            }
        });
    }
    public void loadAllStudentInAttendance(DatabaseReference ref, ArrayList<String> studentList, ArrayList<StudentAttendInfor> attendInfors, Student_Adapter adapter, Teacher_attendance activity){
        if(studentList != null){
            for(String UUID : studentList){
                ref.child("StudentStateList").child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null){

                            StudentAttendInfor attendInfor = snapshot.getValue(StudentAttendInfor.class);

                            attendInfors.add(attendInfor);


                        }
                        else {
                            StudentAttendInfor newAttendInfor = new StudentAttendInfor(UUID,-2);
                            attendInfors.add(newAttendInfor);
                        }
                        adapter.notifyDataSetChanged();
                        activity.setStatistical();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

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
