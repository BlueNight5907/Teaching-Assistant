package com.app.teachingassistant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.teachingassistant.CreateAttendance_Auto;
import com.app.teachingassistant.CreateAttendance_Manual;
import com.app.teachingassistant.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Create_Attendance_Bottom_Sheet extends BottomSheetDialogFragment {
    View view;
    LinearLayout manual_create,auto_create,created_by_form;
    public Create_Attendance_Bottom_Sheet() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.create_attendance_bottom_sheet, container, false);
        manual_create = view.findViewById(R.id.manual_create);
        auto_create = view.findViewById(R.id.auto_create);
        created_by_form = view.findViewById(R.id.create_using_form);

        manual_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateAttendance_Manual.class);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        getActivity().startActivity(intent);
                    }
                }, 300);

            }
        });
        auto_create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateAttendance_Auto.class);

                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        getActivity().startActivity(intent);
                    }
                }, 300);

            }
        });
        created_by_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 300);

            }
        });

        return view;
    }
}