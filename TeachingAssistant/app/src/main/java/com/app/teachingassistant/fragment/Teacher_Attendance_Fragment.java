package com.app.teachingassistant.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.teachingassistant.ClassStatus;
import com.app.teachingassistant.DAO.AttendanceDAO;
import com.app.teachingassistant.DAO.ClassDAO;
import com.app.teachingassistant.R;
import com.app.teachingassistant.Teacher_attendance;
import com.app.teachingassistant.config.Teacher_Attendance_List_Recycle_Adapter;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.Attendance_Infor;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Teacher_Attendance_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Teacher_Attendance_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = Teacher_Attendance_Fragment.class.getSimpleName();
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    RecyclerView recyclerView;
    Button createAttendance;
    ConstraintLayout classState;
    ArrayList<Attendance_Infor> attendance_list = new ArrayList<Attendance_Infor>();
    FirebaseUser user;
    DatabaseReference classRef,userRef,attendRef;
    Teacher_Attendance_List_Recycle_Adapter teacher_attendance_list_recycle_adapter;
    final LoadingDialog loadingDialog = new LoadingDialog(getActivity());


    public Teacher_Attendance_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Teacher_Attendance_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Teacher_Attendance_Fragment newInstance(String param1, String param2) {
        Teacher_Attendance_Fragment fragment = new Teacher_Attendance_Fragment();
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
        classRef = FirebaseDatabase.getInstance().getReference("Class").child(ClassDAO.getInstance().getCurrentClass().getKeyID());
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        attendRef = FirebaseDatabase.getInstance().getReference("Attendances").child(ClassDAO.getInstance().getCurrentClass().getKeyID());
    }
    public void closeDialog(){
        loadingDialog.stopLoadingAlertDialog();
    }
    public void openAttendance(Attendance_Infor attendanceInfor){
        AttendanceDAO.getInstance().setCurrentAttendance(attendanceInfor);
        Intent intent = new Intent(getActivity(), Teacher_attendance.class);
        startActivity(intent);
        finish();
    }
    public void makeToastLong(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.teacher_class_attendance_fragment, container, false);
        //Thiết lập recycler view
        recyclerView = view.findViewById(R.id.attendance_list);
        teacher_attendance_list_recycle_adapter = new Teacher_Attendance_List_Recycle_Adapter(getActivity(),attendance_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(teacher_attendance_list_recycle_adapter);

        createAttendance = view.findViewById(R.id.btn_bottom_sheet_dialog);
        createAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogFragment();
            }
        });

        classState = view.findViewById(R.id.class_state);
        classState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ClassStatus.class);
                getActivity().startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    public void finish(){
        finish();
    }
    private void loadAllAttendancesList(){
        AttendanceDAO.getInstance().loadAllAttendancesList(attendRef,attendance_list,teacher_attendance_list_recycle_adapter,Teacher_Attendance_Fragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        attendance_list.clear();
        loadAllAttendancesList();
    }

    /**
     * showing bottom sheet dialog fragment
     * same layout is used in both dialog and dialog fragment
     */
    public void showBottomSheetDialogFragment() {
        Create_Attendance_Bottom_Sheet bottomSheetFragment = new Create_Attendance_Bottom_Sheet();
        bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
    }
}