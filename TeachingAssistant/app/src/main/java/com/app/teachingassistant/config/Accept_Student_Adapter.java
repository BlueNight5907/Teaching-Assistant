package com.app.teachingassistant.config;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.fragment.Teacher_People_Fragment;
import com.app.teachingassistant.model.Result;
import com.app.teachingassistant.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Accept_Student_Adapter extends RecyclerView.Adapter<Accept_Student_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<User> student_list;
    private Teacher_People_Fragment fragment;

    public Accept_Student_Adapter(Activity activity,Teacher_People_Fragment fragment,ArrayList student_list) {
        super();
        this.mActivity = activity;
        this.student_list = student_list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mActivity).inflate(R.layout.list_student_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(student_list.get(position),position);
    }

    @Override
    public int getItemCount() {
        return student_list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userAvt;
        TextView userName;
        LinearLayout ignore,accept;
        ProgressBar progressBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ignore = itemView.findViewById(R.id.ignore);
            accept = itemView.findViewById(R.id.accept);
            userName = itemView.findViewById(R.id.user_name);
            userAvt = itemView.findViewById(R.id.user_logo);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
        public void bind(User user,int position){
            if(user == null){
                return;
            }
            userName.setText(user.getName());
            String UUID = user.getUUID();
            String classCode = ClassDAO.getInstance().getCurrentClass().getKeyID();
            if(user.isHasProfileUrl()) {
                AccountDAO.getInstance().loadProfileImg(UUID,userAvt);
            }

            ignore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ignore.setVisibility(View.GONE);
                    accept.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    Result result = ClassDAO.getInstance().removeStudentAttend(FirebaseDatabase.getInstance(),UUID,classCode);
                    if(!result.isError()){
                        ClassDAO.getInstance().getCurrentClass().getStudentToAttend().remove(position);
                        student_list.remove(position);
                        notifyDataSetChanged();
                    }
                    else{
                        ignore.setVisibility(View.VISIBLE);
                        accept.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ignore.setVisibility(View.GONE);
                    accept.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    Map<String,Object> map = ClassDAO.getInstance().acceptStudentAttend(FirebaseDatabase.getInstance(),UUID,classCode);
                    Log.d("map", "onClick: "+map);
                    ClassDAO.getInstance().getCurrentClass().getStudentToAttend().remove(position);
                    student_list.remove(position);
                    fragment.addStdtoList(user);
                    notifyDataSetChanged();
                }
            });
        }
    }

}
