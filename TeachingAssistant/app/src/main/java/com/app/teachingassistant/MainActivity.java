package com.app.teachingassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.config.Student_Home_Classlist_Recycle_Adapter;
import com.app.teachingassistant.model.Class_Infor;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView userAvt,lgUserAvt;
    private DrawerLayout drawerLayout;
    NavigationView sidebar;
    RecyclerView mainClassList;
    ArrayList<Class_Infor> class_list = new ArrayList<Class_Infor>();
    Button attendClassBtn;
    TextView txtName,txtRole;
    LinearLayout manageUser;
    FirebaseUser user;
    DatabaseReference classRef,userRef;
    Student_Home_Classlist_Recycle_Adapter student_home_classlist_recycle_adapter;
    private ArrayList<String> classKeyIDList = new ArrayList<String>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Drawable hamburger_button = getResources().getDrawable(R.drawable.hamburger_icon);
        actionBar.setHomeAsUpIndicator(hamburger_button);
        /////////////////////////////////////////////////////////////////

        userAvt = findViewById(R.id.user_logo_toolbar);
        userAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserManage.class);
                startActivity(intent);
            }
        });
        /////////////////////////////////////////////////////////////////////////////////
        /*Khai báo Firebase
         */
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        ///////////////////////////////////////////////////////////////////////////////


        userAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserManage.class);
                startActivity(intent);
            }
        });
        //Xử lý sự kiện tại navigation sidebar
        drawerLayout = findViewById(R.id.drawer_layout);
        sidebar = findViewById(R.id.sidebar);
        MenuItem menuItem = sidebar.getMenu().findItem(R.id.side_nav_home).setChecked(true);
        menuItem.setChecked(true);
        sidebar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // set item as selected to persist highlight
                item.setChecked(true);
                // close drawer when item is tapped
                drawerLayout.closeDrawers();
                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                switch (item.getItemId()){
                    case R.id.side_nav_notificaitons:
                        Intent intent = new Intent(MainActivity.this,StudentNotification.class);
                        startActivity(intent);
                }

                return true;
            }
        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        /////////////////////////////////////////////////////////////////
        View header = sidebar.getHeaderView(0);
        txtName = (TextView)header.findViewById(R.id.user_name);
        txtRole = (TextView)header.findViewById(R.id.user_role);
        lgUserAvt = (CircleImageView)header.findViewById(R.id.user_logo_toolbar);
        manageUser = (LinearLayout)header.findViewById(R.id.go_to_manageUser);
        manageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserManage.class);
                startActivity(intent);
            }
        });
        txtName.setText(AccountDAO.getInstance().getCurrentUser().getName());
        txtRole.setText(AccountDAO.getInstance().getCurrentUser().getRole());
        if(AccountDAO.getInstance().getCurrentUser().isHasProfileUrl()){
            AccountDAO.getInstance().loadProfileImg(user.getUid(),lgUserAvt);
        }


        //Load danh sách lớp
        mainClassList = findViewById(R.id.main_class_list);
        student_home_classlist_recycle_adapter = new Student_Home_Classlist_Recycle_Adapter(this,class_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mainClassList.setItemAnimator(new DefaultItemAnimator());
        mainClassList.setLayoutManager(layoutManager);
        mainClassList.setAdapter(student_home_classlist_recycle_adapter);


        attendClassBtn = findViewById(R.id.attend_class_btn);
        attendClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Student_Attend_Class.class);
                startActivity(intent);
            }
        });
        loadAllInfor();
    }


    //Tải lên các thôn tin lên màn hình chính
    private void loadAllInfor(){
        if(AccountDAO.getInstance().getCurrentUser().isHasProfileUrl()){
            AccountDAO.getInstance().loadProfileImg(AccountDAO.getInstance().getCurrentUser().getUUID(),userAvt);
            AccountDAO.getInstance().loadProfileImg(AccountDAO.getInstance().getCurrentUser().getUUID(),lgUserAvt);
        }
        txtName.setText(AccountDAO.getInstance().getCurrentUser().getName());
        txtRole.setText(AccountDAO.getInstance().getCurrentUser().getRole());



    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*
                GenericTypeIndicator<HashMap<String,String>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String,String>>(){};
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap = snapshot.getValue(genericTypeIndicator);
                */
            update(snapshot);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        userRef.child("ClassListAttended").addValueEventListener(valueEventListener);

    }


    @Override
    protected void onPause() {
        super.onPause();
        userRef.child("ClassListAttended").addValueEventListener(valueEventListener);
    }
    private void update(DataSnapshot snapshot){
        class_list.clear();
        for(DataSnapshot item:snapshot.getChildren()){
            if(item.getValue(Boolean.class)){
                String keyID = item.getKey().toString();
                classRef.child(keyID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Class_Infor class_infor = snapshot.getValue(Class_Infor.class);
                        if(class_infor == null) return;
                        class_list.add(class_infor);
                        student_home_classlist_recycle_adapter.notifyDataSetChanged();
                        Log.d("num class", "onDataChange: "+class_list.size());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {

            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}