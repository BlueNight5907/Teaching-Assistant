package com.app.teachingassistant;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.teachingassistant.fragment.Student_Home_Fragment;
import com.app.teachingassistant.fragment.Student_People_Fragment;
import com.app.teachingassistant.fragment.Teacher_Attendance_Fragment;
import com.app.teachingassistant.fragment.Teacher_Home_Fragment;
import com.app.teachingassistant.fragment.Teacher_People_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherClass extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView sidebar;
    CircleImageView userAvt;
    ActionBar actionBar;
    Fragment fragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_class);


        userAvt = findViewById(R.id.user_logo_toolbar);
        userAvt.setVisibility(View.GONE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        Drawable hamburger_button = getResources().getDrawable(R.drawable.hamburger_icon);
        actionBar.setHomeAsUpIndicator(hamburger_button);
        /////////////////////////////////////////////////////////////////
        //Khai báo botttombar
        BottomNavigationView bottomBar_navigation = (BottomNavigationView) findViewById(R.id.bottombar_navigation);
        bottomBar_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Xử lý sự kiện tại navigation sidebar
        drawerLayout = findViewById(R.id.drawer_layout);
        sidebar = findViewById(R.id.sidebar);

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
                    case R.id.side_nav_home:
                        finish();
                        return true;
                    default:
                    case R.id.side_nav_notificaitons:
                        Intent intent = new Intent(TeacherClass.this,StudentNotification.class);
                        startActivity(intent);

                }

                return true;
            }
        });
        drawerLayout.addDrawerListener(myDrawlayoutListener);
        actionBar.setTitle("Lớp học");
        fragment = new Teacher_Home_Fragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_menu_has_avt, menu);
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
    //Xử lý sự kiện click trên draw layout
    private DrawerLayout.DrawerListener myDrawlayoutListener = new DrawerLayout.DrawerListener() {
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
    };
    //Xử lý sự kiện click vào bottom bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    actionBar.setTitle("Lớp học");

                    fragment = new Teacher_Home_Fragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_people:
                    actionBar.setTitle("Mọi người");
                    fragment = new Teacher_People_Fragment();

                    loadFragment(fragment);
                    return true;
                case R.id.navigation_attendance:
                    actionBar.setTitle("Điểm danh");
                    fragment = new Teacher_Attendance_Fragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };
    //Load Fragment
    private void loadFragment(Fragment fragment) {
        Log.d("check fragment active", "loadFragment: hello");

        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}
