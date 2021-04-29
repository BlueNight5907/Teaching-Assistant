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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.config.BackgroundDrawable;
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

public class ChangeClassInfor extends AppCompatActivity {
    ActionBar actionBar;
    TextInputEditText className,classPeriod;
    Spinner themeOption;
    Button changeClassInfor;
    FirebaseUser user;
    DatabaseReference dtb;
    CardView cardView;
    ImageView imageView;
    LinearLayout linearLayout;
    final LoadingDialog loadingDialog = new LoadingDialog(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Chỉnh sửa thông tin");
        themeOption = findViewById(R.id.theme_option);
        linearLayout = findViewById(R.id.theme_option_btn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
        dtb = FirebaseDatabase.getInstance().getReference("Class");
        //Khai báo các thành phần
        ////////////////////////////////////////////////////////////////////////////////////
        className = findViewById(R.id.class_name);
        classPeriod = findViewById(R.id.period_num);
        changeClassInfor = findViewById(R.id.creater_class_btn);
        cardView = findViewById(R.id.class_background_image_Wrapper);
        imageView = findViewById(R.id.class_background_image);
        //Xét các sự kiện
        ///////////////////////////////////////////////////////////////////////////////////
        changeClassInfor.setText("Lưu");
        className.addTextChangedListener(checkInput);
        classPeriod.addTextChangedListener(checkInput);

        changeClassInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeClassInforProfile();
            }
        });
        String do_action[] = {"Hóa học","Học đường thân thiện","Sắc màu","Công nghệ","Tối giản","Toán học","Thành phố","Sống động","Ánh trăng","Chiều tối"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChangeClassInfor.this, android.R.layout.simple_spinner_item,do_action);
        adapter.setDropDownViewResource(R.layout.spinner_list_item);
        themeOption.setAdapter(adapter);
        themeOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageView.setImageResource(BackgroundDrawable.getInstance().getBackGround(position+1));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binData();
    }
    private void binData(){
        className.setText(ClassDAO.getInstance().getCurrentClass().getClassName());
        classPeriod.setText(String.valueOf(ClassDAO.getInstance().getCurrentClass().getClassPeriod()));
        themeOption.setSelection(ClassDAO.getInstance().getCurrentClass().getBackgroundtheme()-1);
    }

    private void ChangeClassInforProfile(){
        loadingDialog.startLoadingAlertDialog();
        String name = className.getText().toString();
        int period = Integer.parseInt(classPeriod.getText().toString());
        int backgroundSelected = (int)themeOption.getSelectedItemId()+1;
        String UUID = user.getUid();
        String teacherName = AccountDAO.getInstance().getCurrentUser().getName();
        String keyID = ClassDAO.getInstance().getCurrentClass().getKeyID();
        Class_Infor class_infor = new Class_Infor(name,teacherName,UUID,period,keyID,null,null,backgroundSelected,keyID);
        //Xét xem lớp học đó đã tồn tại hay chưa
        DatabaseReference classInfor = FirebaseDatabase.getInstance().getReference("Users").child(UUID).child("ClassListCreated").child(name);
        classInfor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.getValue(String.class).equals(ClassDAO.getInstance().getCurrentClass().getKeyID())){
                    loadingDialog.stopLoadingAlertDialog();
                    Toast.makeText(ChangeClassInfor.this,"Lớp học đã tồn tại",Toast.LENGTH_SHORT).show();
                }else{
                    ClassDAO.getInstance().changeClassProfile(dtb,keyID,UUID,class_infor,ChangeClassInfor.this);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.stopLoadingAlertDialog();
            }
        });
    }
    public void makeToastLong(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
    public  void closeDialog(){
        loadingDialog.stopLoadingAlertDialog();
    }
    private TextWatcher checkInput = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(className.length()>0 && classPeriod.length()>0){
                changeClassInfor.setEnabled(true);
            }
            else {
                changeClassInfor.setEnabled(false);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
