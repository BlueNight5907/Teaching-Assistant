package com.app.teachingassistant.config;

import android.app.Activity;
import android.content.Intent;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.MainActivity;
import com.app.teachingassistant.R;
import com.app.teachingassistant.StudentClass;
import com.app.teachingassistant.TeacherClass;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.Class_Infor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.cert.PKIXRevocationChecker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Teacher_Home_Classlist_Adapter extends RecyclerView.Adapter<Teacher_Home_Classlist_Adapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<Class_Infor> Class_Infors;

    public Teacher_Home_Classlist_Adapter(Activity activity,ArrayList Class_Infors) {
        super();
        this.mActivity = activity;
        this.Class_Infors = Class_Infors;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mActivity).inflate(R.layout.student_main_class_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        hideEvent(holder);
        //Xử lý thông tin ở đây
        holder.bind(Class_Infors.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassDAO.getInstance().setCurrentClass(Class_Infors.get(position));
                Intent intent = new Intent(mActivity, TeacherClass.class);
                mActivity.startActivity(intent);
            }
        });
    }
    private void hideEvent(MyViewHolder holder){
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,150, mActivity.getResources().getDisplayMetrics());
        float margin_pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10, mActivity.getResources().getDisplayMetrics());
        holder.layout.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new  RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(int)pixels);
        params.setMargins((int)margin_pixels,(int)margin_pixels,(int)margin_pixels,(int)margin_pixels);
        holder.relativeLayout.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return Class_Infors.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        boolean initial = true;
        TextView classname,classMembers,event_infor;
        Spinner option;
        LinearLayout layout;
        RelativeLayout relativeLayout;
        LoadingDialog loadingDialog = new LoadingDialog(mActivity);
        ImageView background;
        String do_action[] = {"Đóng","Xóa lớp học"};

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.event_available_layout);
            relativeLayout = itemView.findViewById(R.id.student_main_class_item_relative);
            classname = itemView.findViewById(R.id.student_home_classname);
            classMembers = itemView.findViewById(R.id.student_home_teacher_name);
            option = itemView.findViewById(R.id.class_item_more_option);
            event_infor = itemView.findViewById(R.id.event_infor);
            background = itemView.findViewById(R.id.class_background_image);
            ArrayAdapter<String>adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item,do_action);
            adapter.setDropDownViewResource(R.layout.spinner_list_item);
            option.setAdapter(adapter);
        }
        public void bind(Class_Infor class_infor){
            if(class_infor == null) return;
            classname.setText(class_infor.getClassName());
            if(class_infor.getStudentList()==null){
                classMembers.setText("0 học viên");
            }
            else {
                classMembers.setText(String.valueOf(class_infor.getStudentList().size())+" học viên");
            }
            int backgroundThemeId = class_infor.getBackgroundtheme();
            background.setImageResource(BackgroundDrawable.getInstance().getBackGround(backgroundThemeId));
            option.setTag(class_infor.getKeyID());
            option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("delete", ""+initial+" "+position);
                    if(initial){
                        initial = false;
                    }else{
                        if(position == 1){
                            loadingDialog.startLoadingAlertDialog();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseDatabase dtb = FirebaseDatabase.getInstance();
                            Map<String, Object> infos = new HashMap<>();
                            infos.put("Users/"+user.getUid()+"/ClassListCreated/"+class_infor.getClassName(),null);
                            infos.put("Class/"+class_infor.getKeyID(),null);
                            dtb.getReference().updateChildren(infos);
                            ClassDAO.getInstance().setCurrentClass(null);
                            loadingDialog.stopLoadingAlertDialog();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
    }
}
