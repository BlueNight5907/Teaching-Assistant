package com.app.teachingassistant;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.teachingassistant.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    RadioButton rMale,rFemale,rOther;
    EditText fullName,email,pass,confirmPass,sid;
    TextView error;
    Spinner spinner;
    Button regis;
    private ProgressDialog mProgressDiaglog;
    private FirebaseAuth auth;
    LinearLayout sidContainer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        auth = FirebaseAuth.getInstance();

        rMale = findViewById(R.id.male);
        rFemale = findViewById(R.id.female);
        rOther = findViewById(R.id.another);
        fullName = findViewById(R.id.user_fullName);
        email = findViewById(R.id.user_email);
        pass = findViewById(R.id.user_password);
        confirmPass = findViewById(R.id.user_password_confirm);
        spinner = findViewById(R.id.user_power);
        regis = findViewById(R.id.register_btn);
        error = findViewById(R.id.error);
        error.setVisibility(View.GONE);
        sidContainer = findViewById(R.id.sid_container);
        sid = findViewById(R.id.student_id);
        sidContainer.setVisibility(View.GONE);

        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check().equals("")){
                    error.setVisibility(View.GONE);
                    register();
                }
                else {
                    error.setVisibility(View.VISIBLE);
                    error.setText(check());
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    sidContainer.setVisibility(View.VISIBLE);
                }
                else{
                    sidContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private String check(){
        String err="";
        if(fullName.getText().toString().equals("")){
            return err="Tên người dùng không được để trống";
        }
        else if(!(rMale.isChecked() || rFemale.isChecked() || rOther.isChecked())){
            return err="Bạn chưa chọn giới tính";
        }
        else if(email.getText().toString().length()==0){
            return  err="Bạn chưa nhập Email";
        }
        else if(!isValidEmail(email.getText().toString())){
            return  err="Email không chính xác";
        }
        else if(pass.getText().toString().length() == 0){
            return  err="Bạn chưa nhập mật khẩu";
        }
        else if(pass.getText().toString().length() <= 6){
            return  err="Mật khẩu quá ngắn";
        }
        else if( !confirmPass.getText().toString().equals(pass.getText().toString()) ){
            return  err="Mật khẩu không khớp";
        }
        else if(spinner.getSelectedItemPosition() == 1 && sid.getText().toString().length() == 0){
            return  err="Mã số sinh viên không được để trống";
        }
        return err;
    }
    private void register(){
        showProgress();
        String mail = email.getText().toString();
        String password = pass.getText().toString();

        auth.createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgress();
                        if(!task.isSuccessful()){
                            Toast toast = Toast.makeText(Register.this,"Đăng ký thất bại: "+task.getException(),Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                        else {
                            String UID = task.getResult().getUser().getUid();
                            uploadInfor(UID);
                            showSuccessDialog();
                        }
                    }
                });
    }
    private void uploadInfor(String UID){
        String name = fullName.getText().toString();
        String mail = email.getText().toString();
        String gender = "Khác";
        if(rMale.isChecked()){
            gender = "Nam";
        }
        else if(rFemale.isChecked()){
            gender = "Nữ";
        }
        String role = spinner.getSelectedItem().toString();
        String sID = sid.getText().toString();
        User newUser = new User(UID,mail,name,false,role,gender,sID);
        FirebaseDatabase.getInstance().getReference("Users").child(UID).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }
    private void showProgress(){
        if(mProgressDiaglog == null){
            mProgressDiaglog = new ProgressDialog(this);
            mProgressDiaglog.setMessage("Đang xử lý ...");
            mProgressDiaglog.setIndeterminate(true);
            mProgressDiaglog.setCancelable(false);
        }
        mProgressDiaglog.show();
    }
    private void hideProgress(){
        if( mProgressDiaglog!=null && mProgressDiaglog.isShowing()){
            mProgressDiaglog.dismiss();
        }
    }
    private void showSuccessDialog(){
        //Tạo đối tượng
        AlertDialog.Builder b = new AlertDialog.Builder(Register.this);
        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.normal_dialog, null);
        Button OK = customLayout.findViewById(R.id.ok_button);
        Button Cancel = customLayout.findViewById(R.id.not_ok_button);
        TextView mess = customLayout.findViewById(R.id.dialog_message);
        b.setView(customLayout);
        mess.setText("Đăng ký thành công, nhấn Ok để quay trở lại màn hình đăng nhập");

        //Tạo dialog
        AlertDialog al = b.create();
        //Hiển thị
        al.show();
        //Nút Cancel
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.dismiss();
            }
        });
        // Nút Ok
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgress();
    }
}
