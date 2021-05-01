package com.app.teachingassistant.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.config.Accept_Student_Adapter;
import com.app.teachingassistant.fragment.Teacher_People_Fragment;
import com.app.teachingassistant.model.Result;

import com.app.teachingassistant.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AcceptAttendDialog extends DialogFragment {
    Button addAll,close;
    RecyclerView listStudentView;
    AlertDialog alertDialog;
    FirebaseUser user;
    DatabaseReference classRef,userRef;
    ArrayList<String> studentAttend = new ArrayList<>();
    ArrayList<User> studentList = new ArrayList<>();
    Accept_Student_Adapter accept_student_adapter;
    Teacher_People_Fragment fragment;
    public AcceptAttendDialog(Teacher_People_Fragment fragment){
        this.fragment = fragment;
        user = FirebaseAuth.getInstance().getCurrentUser();
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        studentAttend = ClassDAO.getInstance().getCurrentClass().getStudentToAttend();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.accept_attend, null);
        builder.setView(customLayout);

        addAll = customLayout.findViewById(R.id.add_all);
        addAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptAll();
            }
        });
        close = customLayout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        listStudentView = customLayout.findViewById(R.id.list_student);
        accept_student_adapter = new Accept_Student_Adapter(getActivity(),fragment,studentList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listStudentView.setItemAnimator(new DefaultItemAnimator());
        listStudentView.setLayoutManager(layoutManager);
        listStudentView.setAdapter(accept_student_adapter);
        AccountDAO.getInstance().loadStudentList(userRef,studentAttend,studentList,accept_student_adapter);

        alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }
    public void acceptAll(){
        String classCode = ClassDAO.getInstance().getCurrentClass().getKeyID();
        if(studentAttend!=null){
            for(int position = studentAttend.size() - 1;position >= 0;position--){
                String UUID = studentList.get(position).getUUID();
                Map<String,Object> map = ClassDAO.getInstance().acceptStudentAttend(FirebaseDatabase.getInstance(),UUID,classCode);
                Log.d("map", "onClick: "+map);
                studentAttend.remove(position);
                if(position < studentList.size()){
                    fragment.addStdtoList(studentList.get(position));
                    studentList.remove(position);
                }
                accept_student_adapter.notifyDataSetChanged();
            }
        }
    }
}