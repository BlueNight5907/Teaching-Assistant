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
            return err="T??n ng?????i d??ng kh??ng ???????c ????? tr???ng";
        }
        else if(!(rMale.isChecked() || rFemale.isChecked() || rOther.isChecked())){
            return err="B???n ch??a ch???n gi???i t??nh";
        }
        else if(email.getText().toString().length()==0){
            return  err="B???n ch??a nh???p Email";
        }
        else if(!isValidEmail(email.getText().toString())){
            return  err="Email kh??ng ch??nh x??c";
        }
        else if(pass.getText().toString().length() == 0){
            return  err="B???n ch??a nh???p m???t kh???u";
        }
        else if(pass.getText().toString().length() <= 6){
            return  err="M???t kh???u qu?? ng???n";
        }
        else if( !confirmPass.getText().toString().equals(pass.getText().toString()) ){
            return  err="M???t kh???u kh??ng kh???p";
        }
        else if(spinner.getSelectedItemPosition() == 1 && sid.getText().toString().length() == 0){
            return  err="M?? s??? sinh vi??n kh??ng ???????c ????? tr???ng";
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
                            Toast toast = Toast.makeText(Register.this,"????ng k?? th???t b???i: "+task.getException(),Toast.LENGTH_LONG);
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
        String gender = "Kh??c";
        if(rMale.isChecked()){
            gender = "Nam";
        }
        else if(rFemale.isChecked()){
            gender = "N???";
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
            mProgressDiaglog.setMessage("??ang x??? l?? ...");
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
        //T???o ?????i t?????ng
        AlertDialog.Builder b = new AlertDialog.Builder(Register.this);
        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.normal_dialog, null);
        Button OK = customLayout.findViewById(R.id.ok_button);
        Button Cancel = customLayout.findViewById(R.id.not_ok_button);
        TextView mess = customLayout.findViewById(R.id.dialog_message);
        b.setView(customLayout);
        mess.setText("????ng k?? th??nh c??ng, nh???n Ok ????? quay tr??? l???i m??n h??nh ????ng nh???p");

        //T???o dialog
        AlertDialog al = b.create();
        //Hi???n th???
        al.show();
        //N??t Cancel
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.dismiss();
            }
        });
        // N??t Ok
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
