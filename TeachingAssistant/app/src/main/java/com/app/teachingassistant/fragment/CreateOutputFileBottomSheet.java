package com.app.teachingassistant.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.teachingassistant.ClassStatus;
import com.app.teachingassistant.CreateAttendance_Auto;
import com.app.teachingassistant.CreateAttendance_Manual;
import com.app.teachingassistant.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreateOutputFileBottomSheet extends BottomSheetDialogFragment {
    View view;
    LinearLayout excel,pdf;
    ClassStatus activity;
    public CreateOutputFileBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }
    public void setActivity(ClassStatus activity) {
        this.activity = activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.create_output_bottom_sheet, container, false);
        excel = view.findViewById(R.id.output_Excel);
        pdf = view.findViewById(R.id.output_PDF);

        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateAttendance_Manual.class);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        activity.createExcelFile();
                    }
                }, 300);

            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateAttendance_Auto.class);

                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        activity.createPdfFile();
                    }
                }, 300);

            }
        });


        return view;
    }
}