package com.app.teachingassistant.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.config.Student_List_Recycle_Adapter;
import com.app.teachingassistant.model.Student_Infor;
import com.app.teachingassistant.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Student_People_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Student_People_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = Student_People_Fragment.class.getSimpleName();;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    CircleImageView teacherIMG;
    TextView teacherName;
    Spinner peopleOption;
    RecyclerView recyclerView;
    ArrayList<User> userArrayList = new ArrayList<User>();
    Button acceptAttend,addStudent;
    FirebaseUser user;
    DatabaseReference classRef,userRef;


    public Student_People_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Student_People_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Student_People_Fragment newInstance(String param1, String param2) {
        Student_People_Fragment fragment = new Student_People_Fragment();
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
        classRef = FirebaseDatabase.getInstance().getReference("Class");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.student_class_people_fragment, container, false);
        //Ẩn đi thanh option do dùng chung layout
        peopleOption = view.findViewById(R.id.class_people_item_more_option);
        peopleOption.setVisibility(View.GONE);
        //Thiết lập recycler view
        teacherName = view.findViewById(R.id.people_name);
        teacherIMG = view.findViewById(R.id.people_img);


        recyclerView = view.findViewById(R.id.student_class_people_list);
        Student_List_Recycle_Adapter student_list_recycle_adapter = new Student_List_Recycle_Adapter(getActivity(),userArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(student_list_recycle_adapter);
        /////////////////////////////////////////////////////////////
        ArrayList<String> studentList = new ArrayList<>();
        studentList = ClassDAO.getInstance().getCurrentClass().getStudentList();
        AccountDAO.getInstance().loadStudentList(userRef,studentList,userArrayList,student_list_recycle_adapter);
        // Inflate the layout for this fragment


        loadAll();
        return view;
    }
    private void loadAll(){
        teacherName.setText(ClassDAO.getInstance().getCurrentClass().getTeacherName());
        AccountDAO.getInstance().loadProfileImg(ClassDAO.getInstance().getCurrentClass().getTeacherUUID(),teacherIMG);
    }
    public void finish(){
        finish();
    }
}