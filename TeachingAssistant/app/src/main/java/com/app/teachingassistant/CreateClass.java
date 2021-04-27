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

public class CreateClass extends AppCompatActivity {
    ActionBar actionBar;
    TextInputEditText className,classPeriod;
    Spinner themeOption;
    Button createClass;
    FirebaseUser user;
    DatabaseReference dtb;
    CardView cardView;
    ImageView imageView;
    final LoadingDialog loadingDialog = new LoadingDialog(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_class);
        Spinner spinner;
        LinearLayout linearLayout;
        TextView theme_name;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Tạo lớp học");
        spinner = findViewById(R.id.theme_option);
        linearLayout = findViewById(R.id.theme_option_btn);
        String do_action[] = {"Hóa học","Công nghệ","Giáo dục","Toán học"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateClass.this, android.R.layout.simple_spinner_item,do_action);
        adapter.setDropDownViewResource(R.layout.spinner_list_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
         dtb = FirebaseDatabase.getInstance().getReference("Class");
        //Khai báo các thành phần
        ////////////////////////////////////////////////////////////////////////////////////
        themeOption = findViewById(R.id.theme_option);
        className = findViewById(R.id.class_name);
        classPeriod = findViewById(R.id.period_num);
        createClass = findViewById(R.id.creater_class_btn);
        cardView = findViewById(R.id.class_background_image_Wrapper);
        imageView = findViewById(R.id.class_background_image);
        //Xét các sự kiện
        ///////////////////////////////////////////////////////////////////////////////////
        className.addTextChangedListener(checkInput);
        classPeriod.addTextChangedListener(checkInput);
        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClassProfile();
            }
        });
    }
    private void clearAll(){
        themeOption.setSelection(0);
        className.getText().clear();
        classPeriod.getText().clear();
        createClass.setEnabled(false);
    }
    private void createClassProfile(){
        loadingDialog.startLoadingAlertDialog();
        String name = className.getText().toString();
        int period = Integer.parseInt(classPeriod.getText().toString());
        int backgroundSelected = (int)themeOption.getSelectedItemId();
        String UUID = user.getUid();
        String teacherName = AccountDAO.getInstance().getCurrentUser().getName();
        String keyID = dtb.push().getKey();
        Class_Infor class_infor = new Class_Infor(name,teacherName,UUID,period,keyID,null,null,null,keyID);
        //Xét xem lớp học đó đã tồn tại hay chưa
        DatabaseReference classInfor = FirebaseDatabase.getInstance().getReference("Users").child(UUID).child("ClassListCreated").child(name);
        classInfor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    loadingDialog.stopLoadingAlertDialog();
                    Toast.makeText(CreateClass.this,"Lớp học đã tồn tại",Toast.LENGTH_SHORT).show();
                }else{
                    Result result = ClassDAO.getInstance().createClassProfile(dtb,keyID,class_infor);
                    if(result.isError()){
                        Log.d("create class", "createClassProfile: "+result.getMessage());
                    }
                    FirebaseDatabase.getInstance().getReference("Users").child(UUID).child("ClassListCreated").child(name).setValue(keyID);
                    loadingDialog.stopLoadingAlertDialog();
                    Toast.makeText(CreateClass.this,"Tạo lớp học thành công",Toast.LENGTH_SHORT).show();
                    clearAll();
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
            if(className.length()>0 && classPeriod.length()>0){
                createClass.setEnabled(true);
            }
            else {
                createClass.setEnabled(false);
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
