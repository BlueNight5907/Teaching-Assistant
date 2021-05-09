package com.app.teachingassistant;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.Notification.PushNotification;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    Button loginBtn,loginWithGGBtn;
    TextView register,error;
    EditText email,password;
    protected FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog mProgressDiaglog;
    private final LoadingDialog loadingDialog = new LoadingDialog(this);
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        stopNotification();
        loginBtn = findViewById(R.id.login);
        loginWithGGBtn = findViewById(R.id.login_with_gg);
        register = findViewById(R.id.register);
        email = findViewById(R.id.email_user);
        password = findViewById(R.id.password_user);
        error = findViewById(R.id.error);
        error.setVisibility(View.GONE);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check().equals("")){
                    error.setVisibility(View.GONE);
                    login();
                }
                else {
                    error.setVisibility(View.VISIBLE);
                    error.setText(check());
                }
            }
        });
        loginWithGGBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Teacher_Home.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private String check(){
        String err="";
       if(email.getText().toString().length()==0){
            return  err="Bạn chưa nhập Email";
        }
        else if(!isValidEmail(email.getText().toString())){
            return  err="Email không hợp lệ";
        }
        else if(password.getText().toString().length() == 0){
            return  err="Bạn chưa nhập mật khẩu";
        }
        else if(password.getText().toString().length() <= 6){
            return  err="Mật khẩu quá ngắn";
        }
        return err;
    }
    private void login(){
        loadingDialog.startLoadingAlertDialog();
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        auth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            loadingDialog.stopLoadingAlertDialog();
                            Toast toast = Toast.makeText(Login.this,"Đăng nhập thất bại: "+task.getException(),Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                        else {
                            String UID = task.getResult().getUser().getUid();
                            getMainScreen(UID);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.stopLoadingAlertDialog();
                        Log.d("login failed", "onFailure: "+e);
                    }
                });

    }
    private void getMainScreen(String UID){
        Query query = FirebaseDatabase.getInstance().getReference("Users").child(UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                AccountDAO.getInstance().setCurrentUser(user);
                if(user.getRole().equals("Giáo viên")){
                    Intent intent = new Intent(Login.this,Teacher_Home.class);
                    startActivity(intent);
                    loadingDialog.stopLoadingAlertDialog();
                }
                else {
                    startNotification();
                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    loadingDialog.stopLoadingAlertDialog();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void startNotification(){
        Intent intent = new Intent(this, PushNotification.class);
        intent.putExtra("UUID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        this.startService(intent);
    }
    private void stopNotification(){
        Intent intent = new Intent(this, PushNotification.class);
        this.stopService(intent);
    }
}
