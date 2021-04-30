package com.app.teachingassistant.config;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.AttendanceDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.StudentAttendInfor;
import com.app.teachingassistant.model.StudentBannedInfor;
import com.app.teachingassistant.model.StudentBannedList;
import com.app.teachingassistant.model.User;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ClassStatusAdapter extends RecyclerView.Adapter<ClassStatusAdapter.MyViewHolder> {
    private Activity context;
    private ArrayList<String> students;
    private ArrayList<StudentBannedInfor> bannedInfors;
    private ArrayList<StudentBannedList> studentBannedLists;

    DatabaseReference classRef;

    float count;

    public ClassStatusAdapter(Activity context, ArrayList<String> students, ArrayList<StudentBannedInfor> bannedInfors, ArrayList<StudentBannedList> studentBannedLists) {
        super();
        this.context = context;
        this.students = students;
        this.bannedInfors = bannedInfors;
        this.studentBannedLists = studentBannedLists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.class_status_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.bind(bannedInfors.get(position), position);
    }

    @Override
    public int getItemCount() {
        return bannedInfors.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        Button ban_btn;
        TextView name, stt, absent_count;
        int status = 0;

        LoadingDialog loadingDialog = new LoadingDialog(context);

        private  void changeData(StudentBannedInfor data,int position){
            data.setState(status);
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stt = itemView.findViewById(R.id.stt);
            name = itemView.findViewById(R.id.student_status_name_item);
            image = itemView.findViewById(R.id.student_status_image_item);
            ban_btn = itemView.findViewById(R.id.ban_btn);
            absent_count = itemView.findViewById(R.id.absent_date);

        }

        public void bind(StudentBannedInfor data , int position) {
            count = 0;
            String UUID = data.getUUID();
            if(data == null) return;
            stt.setText(String.valueOf(position));
            classRef = FirebaseDatabase.getInstance().getReference("Class");

            FirebaseDatabase.getInstance().getReference("Attendances")
                    .child(ClassDAO.getInstance().getCurrentClass().getKeyID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    Log.d("snapshotNew", "onDataChange: "+snapshot);
                                    for (DataSnapshot item: snapshot.getChildren()) {
                                        DataSnapshot temp = item.child("studentStateList");
                                        Log.d("snapshotNew", "onDataChange: "+temp);
                                        if (temp.getValue() != null) {
                                            for (DataSnapshot data: temp.getChildren()) {
                                                if (data != null) {
                                                    StudentAttendInfor std = data.getValue(StudentAttendInfor.class);
                                                    if (std.getUUID().equals(UUID)) {
                                                        if (std.getState() == 0) {
                                                            count += 0.5;
                                                        }
                                                        else if (std.getState() < 0) {
                                                            count += 1;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    absent_count.setText(String.valueOf(count));
                                    studentBannedLists.get(position).setAbsentDates(count);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            FirebaseDatabase.getInstance().getReference("Users").child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        User user = snapshot.getValue(User.class);
                        name.setText(user.getName());
                        if(user.isHasProfileUrl()){
                            AccountDAO.getInstance().loadProfileImg(UUID,image);
                        }
                        studentBannedLists.get(position).setName(user.getName());
                        studentBannedLists.get(position).setUUID(user.getUUID());
                        studentBannedLists.get(position).setState(data.getState());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            int presentState = data.getState();

            if (presentState == 0) {
                status = -1;
                ban_btn.setText("Cấm thi");
                changeData(data, position);
                String text = "Bạn chắc chắn muốn cấm thi học viên này không";
                ban_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(data, position, text, presentState);
                    }
                });
            }
            if (presentState == -1){
                status = 0;
                ban_btn.setText("Hủy cấm thi");
                changeData(data, position);
                String text = "Bạn chắc chắn muốn hủy cấm thi học viên này không";
                ban_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(data, position, text, presentState);
                    }
                });
            }




        }
        private int changeDptoPx(int dp){
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, context.getResources().getDisplayMetrics());
            return (int)pixels;
        }

        private void showDialog(StudentBannedInfor user, int position, String text, int preStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = context.getLayoutInflater();
            View customview = inflater.inflate(R.layout.normal_dialog, null);
            builder.setView(customview);
            Button OK = customview.findViewById(R.id.ok_button);
            Button Cancel = customview.findViewById(R.id.not_ok_button);
            TextView textView = customview.findViewById(R.id.dialog_message);
            textView.setText(text);
            AlertDialog alert = builder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.animation;
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alert.show();
            alert.getWindow().setLayout(changeDptoPx(280), changeDptoPx(140));
            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            // Nút Ok
            OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    loadingDialog.startLoadingAlertDialog();
                    if (preStatus == 0) {
                        ban_btn.setText("Hủy cấm thi");
                    }
                    else {
                        ban_btn.setText("Cấm thi");
                    }
                    Map<String,Object> map = new HashMap<>();
                    map.put(user.getUUID(),user);

                    classRef.child(ClassDAO.getInstance().getCurrentClass().getKeyID()).child("studentBannedList").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingDialog.stopLoadingAlertDialog();
                            if(task.isSuccessful()){
                                bannedInfors.get(position).setState(user.getState());
                                Toast.makeText(context,"Đã lưu",Toast.LENGTH_LONG).show();
                                notifyItemChanged(position);
                            }
                            else {
                                Toast.makeText(context,"Lưu thất bại",Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.stopLoadingAlertDialog();
                            Toast.makeText(context,"Lỗi kết nối! Lưu thất bại.",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        }

    }
}

