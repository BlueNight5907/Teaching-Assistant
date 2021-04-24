package com.app.teachingassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.config.Student_Attendance_List_Recycle_Adapter;
import com.app.teachingassistant.config.Student_Home_Classlist_Recycle_Adapter;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.fragment.Student_Home_Fragment;
import com.app.teachingassistant.fragment.Student_People_Fragment;
import com.app.teachingassistant.model.Attendance_Infor;
import com.app.teachingassistant.model.Class_Infor;
import com.app.teachingassistant.model.Result;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Student_Attend_Class extends AppCompatActivity {
    ActionBar actionBar;
    Button attendClass;
    TextInputEditText classCode;
    FirebaseUser user;
    DatabaseReference dtb;
    final LoadingDialog loadingDialog = new LoadingDialog(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_attend_class);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
        dtb = FirebaseDatabase.getInstance().getReference("Class");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Tham gia lớp học");

        //////////////////////////////////////////////////////////////////////
        attendClass = findViewById(R.id.attend_class_btn);
        classCode = findViewById(R.id.class_code);





        //////////////////////////////////////////////////////////////////
        classCode.addTextChangedListener(checkInput);
        attendClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendClassWithCode();
            }
        });




    }
    private void attendClassWithCode(){
        loadingDialog.startLoadingAlertDialog();
        String classcode = classCode.getText().toString();
        String UUID = user.getUid();
        DatabaseReference classAttend = FirebaseDatabase.getInstance().getReference("Users").child(UUID).child("ClassListAttended").child(classcode);
        classAttend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    loadingDialog.stopLoadingAlertDialog();
                    Toast.makeText(Student_Attend_Class.this,"Bạn đã đăng ký lớp học này rồi",Toast.LENGTH_SHORT).show();
                }else{
                    Result result = ClassDAO.getInstance().attendClass(FirebaseDatabase.getInstance(),UUID,classcode);
                    if(result.isError()){
                        Log.d("create class", "createClassProfile: "+result.getMessage());
                    }
                    loadingDialog.stopLoadingAlertDialog();
                    Toast.makeText(Student_Attend_Class.this,result.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.stopLoadingAlertDialog();
            }
        });
    }
    private TextWatcher checkInput = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(classCode.length()>4){
                attendClass.setEnabled(true);
            }
            else {
                attendClass.setEnabled(false);
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

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
