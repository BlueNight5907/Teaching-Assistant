package com.app.teachingassistant.DAO;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.teachingassistant.ChangeClassInfor;
import com.app.teachingassistant.Teacher_attendance;
import com.app.teachingassistant.config.ClassStatusAdapter;
import com.app.teachingassistant.config.Student_Adapter;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.Class_Infor;
import com.app.teachingassistant.model.Result;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.app.teachingassistant.model.StudentBannedInfor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassDAO {
    //Mẫu thiết kế singleton
    private static ClassDAO instance;
    private Class_Infor currentClass;
    private DatabaseReference ref,classRef;
    private ClassDAO() {
        ref = FirebaseDatabase.getInstance().getReference("Users");
        classRef = FirebaseDatabase.getInstance().getReference("Class");
    }
    public static ClassDAO getInstance() {
        if(instance == null) {
            synchronized(ClassDAO.class) {
                if(null == instance) {
                    instance  = new ClassDAO();
                }
            }
        }
        return instance;
    }
    public Result createClassProfile(DatabaseReference dtb,String keyID ,Class_Infor class_infor){
        final Result[] result = {new Result(false, "Tạo lớp học thành công")};
        dtb.child(keyID).setValue(class_infor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    result[0] = new Result(true, task.getException().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result[0] = new Result(true, e.toString());
            }
        });
        return result[0];
    }
    public void changeClassProfile(DatabaseReference dtb, String keyID,String UUID , Class_Infor class_infor, ChangeClassInfor activity){
        Map<String,Object> map = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        map.put("Users/"+UUID+"/ClassListCreated/"+currentClass.getClassName(),null);
        map.put("Users/"+UUID+"/ClassListCreated/"+class_infor.getClassName(),keyID);
        map.put("Class/"+keyID+"/className",class_infor.getClassName());
        map.put("Class/"+keyID+"/classPeriod",class_infor.getClassPeriod());
        map.put("Class/"+keyID+"/backgroundtheme",class_infor.getBackgroundtheme());

       ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                activity.closeDialog();
                if(!task.isSuccessful()){
                    activity.makeToastLong("Cập nhật thông tin thất bại");
                    return;
                }
                activity.makeToastLong("Cập nhật thông tin thành công");
                currentClass.setClassName(class_infor.getClassName());
                currentClass.setBackgroundtheme(class_infor.getBackgroundtheme());
                currentClass.setClassPeriod(class_infor.getClassPeriod());
                activity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activity.closeDialog();
                activity.makeToastLong("Cập nhật thông tin thất bại");
            }
        });
    }
    public Result attendClass(FirebaseDatabase dtb, String UUID, String classCode){
        final Result[] result = {new Result(false, "Gửi yêu cầu tham gia thành công")};
        dtb.getReference("Users").child(UUID).child("ClassListAttended").child(classCode).setValue(false);
        dtb.getReference("Class").child(classCode).child("studentToAttend").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> arrayList = new ArrayList<>();
                for(DataSnapshot item :snapshot.getChildren()){
                    arrayList.add(item.getValue().toString());
                }
                arrayList.add(UUID);
                Log.d("push", "onDataChange: "+arrayList);
                dtb.getReference("Class").child(classCode).child("studentToAttend").setValue(arrayList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result[0] = new Result(true, error.getMessage());
            }
        });
        return result[0];
    }
    public Result removeStudentAttend(FirebaseDatabase dtb, String UUID, String classCode){
        final Result[] result = {new Result(false, "Xóa học sinh thành công")};
        dtb.getReference("Users").child(UUID).child("ClassListAttended").child(classCode).setValue(null);
        dtb.getReference("Class").child(classCode).child("studentToAttend").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> arrayList = new ArrayList<>();
                for(DataSnapshot item :snapshot.getChildren()){
                    arrayList.add(item.getValue().toString());
                }
                arrayList.remove(UUID);
                Log.d("push", "onDataChange: "+arrayList);
                dtb.getReference("Class").child(classCode).child("studentToAttend").setValue(arrayList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                result[0] = new Result(true, error.getMessage());
            }
        });
        return result[0];
    }
    public Map<String,Object> acceptStudentAttend(FirebaseDatabase dtb, String UUID, String classCode){
        Map<String,Object> map = new HashMap<>();
        ArrayList<String> attendList = new ArrayList<>();
        ArrayList<String> studentList = new ArrayList<>();
        dtb.getReference("Users").child(UUID).child("ClassListAttended").child(classCode).setValue(true);
        map.put("Users/"+UUID+"/ClassListAttend",true);
        dtb.getReference("Class").child(classCode).child("studentToAttend").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item :snapshot.getChildren()){
                    attendList.add(item.getValue().toString());
                }
                attendList.remove(UUID);
                map.put("Class/"+classCode+"/studentToAttend",attendList);
                dtb.getReference("Class").child(classCode).child("studentToAttend").setValue(attendList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        dtb.getReference("Class").child(classCode).child("studentList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item :snapshot.getChildren()){
                    studentList.add(item.getValue().toString());
                }
                studentList.add(UUID);
                map.put("Class/"+classCode+"/studentList",studentList);
                Log.d("push", "onDataChange: "+studentList);
                dtb.getReference("Class").child(classCode).child("studentList").setValue(studentList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return map;
    }

    public void loadAllStudentInAttendance(DatabaseReference ref, ArrayList<String> studentList, ArrayList<StudentBannedInfor> bannedInfors, ClassStatusAdapter adapter){
        if(studentList != null){
            for(String UUID : studentList){
                ref.child("StudentBannedList").child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null){
                            StudentBannedInfor bannedInfor = snapshot.getValue(StudentBannedInfor.class);
                            bannedInfors.add(bannedInfor);


                        }
                        else {
                            StudentBannedInfor newAttendInfor = new StudentBannedInfor(UUID,0);
                            bannedInfors.add(newAttendInfor);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

    }

    public Class_Infor getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(Class_Infor currentClass) {
        this.currentClass = currentClass;
    }
}
