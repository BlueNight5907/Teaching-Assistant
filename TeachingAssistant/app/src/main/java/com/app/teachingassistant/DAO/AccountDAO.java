package com.app.teachingassistant.DAO;

import android.net.Uri;
import android.util.Log;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.UserManage;
import com.app.teachingassistant.config.Accept_Student_Adapter;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.Message;
import com.app.teachingassistant.model.Result;
import com.app.teachingassistant.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountDAO {
    private User currentUser;
    private DatabaseReference ref,classRef;
    private StorageReference storageReference;
    public Uri profileImage = null;
    //Mẫu thiết kế singleton
    private static AccountDAO instance;
    private AccountDAO() {}
    public static AccountDAO getInstance() {
        if(instance == null) {
            synchronized(AccountDAO.class) {
                if(null == instance) {
                    instance  = new AccountDAO();
                }
            }
        }
        return instance;
    }
    public Result changePassword(FirebaseUser user,String newPassword){
        final Result[] result = {new Result(false, "Cập nhật thành công")};
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                result[0] = new Result(true, task.getException().toString());
                            }
                        }
                    });
        }
        return result[0];
    }
    public void updateProfiles(String UUID, User user, LoadingDialog loadingDialog, UserManage activity){
        Map<String,Object> map = new HashMap<>();
        map.put("gender",user.getGender());
        map.put("hasProfileUrl",user.isHasProfileUrl());
        map.put("sid",user.getSID());


        ref.child(UUID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.stopLoadingAlertDialog();
                if(!task.isSuccessful()){
                    activity.makeToastLong("Cập nhật thông tin thất bại");
                }
                else {
                    activity.makeToastLong("Cập nhật thông tin thành công");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.stopLoadingAlertDialog();
                activity.makeToastLong("Cập nhật thông tin thất bại");
            }
        });

    }
    public Result sendMessage(DatabaseReference chatRef, Message message){
        final Result[] result = {new Result(false, "Cập nhật thành công")};
        String key = chatRef.push().getKey();
        chatRef.child(key).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    result[0] = new Result(true,task.getException().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result[0] = new Result(true,"Lỗi kết nối mạng");
            }
        });
        return result[0];
    }
    public Result uploadImageToFireBase(String UUID,Uri imageUri){
        final Result[] result = {new Result(false, "Cập nhật thành công")};
        StorageReference userProfileImgRef = storageReference.child(UUID).child("profile.jpg");
        userProfileImgRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result[0] = new Result(true,e.toString());
            }
        });
        return result[0];
    }
    public void loadProfileImg(String UUID,CircleImageView imageView){
        try{
            StorageReference userProfileImgRef = storageReference.child(UUID).child("profile.jpg");
            userProfileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(uri == null)
                        return;
                    Picasso.get().load(uri).into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //////////////////////////////////////////////////////////////////

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        storageReference = FirebaseStorage.getInstance().getReference("Users");
        ref = FirebaseDatabase.getInstance().getReference("Users");
        classRef = FirebaseDatabase.getInstance().getReference("Class");
    }
    public Result loadStudentList(DatabaseReference userRef, ArrayList<String> studentAttend, ArrayList<User> listUser, RecyclerView.Adapter accept_student_adapter){
        final Result[] result = {new Result(false, "Cập nhật thành công")};
        if(studentAttend != null){
            for(String item:studentAttend){
                try{
                    userRef.child(item).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listUser.add(snapshot.getValue(User.class));
                            accept_student_adapter.notifyDataSetChanged();
                            Log.d("size", "onCreateDialog: "+listUser.size());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            result[0] = new Result(true,error.toString());
                        }
                    });
                }catch (Exception e){

                }
            }
        }
        return result[0];
    }
}
