package com.app.teachingassistant.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.teachingassistant.Chat_Activity;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.TeacherClass;
import com.app.teachingassistant.config.Student_Attendance_List_Recycle_Adapter;
import com.app.teachingassistant.config.Teacher_Attendance_List_Recycle_Adapter;
import com.app.teachingassistant.model.Attendance_Infor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Teacher_Home_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Teacher_Home_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = Teacher_Home_Fragment.class.getSimpleName();;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    View view;
    private String mParam1;
    private String mParam2;
    ArrayList<Attendance_Infor> attendance_list = new ArrayList<Attendance_Infor>();
    LinearLayout classEventLayout;
    RelativeLayout classInforLayout;
    Spinner classitemOption;
    Button openMessBtn;
    RecyclerView attendance_list_recycler_view;
    FirebaseUser user;
    DatabaseReference classRef,userRef;
    Teacher_Attendance_List_Recycle_Adapter teacher_attendance_list_recycle_adapter;
    TextView className,classMembers;

    public Teacher_Home_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Studdent_Home_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Teacher_Home_Fragment newInstance(String param1, String param2) {
        Teacher_Home_Fragment fragment = new Teacher_Home_Fragment();
        Bundle args = new Bundle();
        args.putString(TAG, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(TAG);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.teacher_class_home_fragment, container, false);
        //Khai báo các thành phần trong layout fragment
        classEventLayout = view.findViewById(R.id.event_available_layout);
        classInforLayout = view.findViewById(R.id.student_main_class_item_relative);
        classitemOption = view.findViewById(R.id.class_item_more_option);
        attendance_list_recycler_view = view.findViewById(R.id.attendance_list);
        className = view.findViewById(R.id.student_home_classname);
        classMembers = view.findViewById(R.id.student_home_teacher_name);


        //Thay đổi các thông tin của layout thông tin lớp học, ẩn đi phần event do xài lại layout
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,150, this.getResources().getDisplayMetrics());
        float margin_pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10, this.getResources().getDisplayMetrics());
        classEventLayout.setVisibility(View.GONE);
        classitemOption.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new  RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(int)pixels);
        params.setMargins((int)margin_pixels,(int)margin_pixels,(int)margin_pixels,(int)margin_pixels);
        classInforLayout.setLayoutParams(params);

        openMessBtn = view.findViewById(R.id.open_message);
        openMessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Chat_Activity.class);
                startActivity(intent);
            }
        });
        //Load danh sách điểm danh
        teacher_attendance_list_recycle_adapter = new Teacher_Attendance_List_Recycle_Adapter(getActivity(),attendance_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        attendance_list_recycler_view.setItemAnimator(new DefaultItemAnimator());
        attendance_list_recycler_view.setLayoutManager(layoutManager);
        attendance_list_recycler_view.setAdapter(teacher_attendance_list_recycle_adapter);
        loadAll();
        return view;
    }
    public void finish(){
        finish();
    }
    private void loadAll(){
        className.setText(ClassDAO.getInstance().getCurrentClass().getClassName());
        if(ClassDAO.getInstance().getCurrentClass().getStudentList() != null){
            classMembers.setText(""+ClassDAO.getInstance().getCurrentClass().getStudentList().size());
        }
        else {
            classMembers.setText("O học viên");
        }
    }
}