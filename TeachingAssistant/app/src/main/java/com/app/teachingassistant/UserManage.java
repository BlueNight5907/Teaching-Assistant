package com.app.teachingassistant;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.PathUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.teachingassistant.DAO.AccountDAO;
import com.app.teachingassistant.config.Student_Attendance_List_Recycle_Adapter;
import com.app.teachingassistant.config.Student_Home_Classlist_Recycle_Adapter;
import com.app.teachingassistant.dialog.LoadingDialog;
import com.app.teachingassistant.fragment.Student_Home_Fragment;
import com.app.teachingassistant.fragment.Student_People_Fragment;
import com.app.teachingassistant.model.Attendance_Infor;
import com.app.teachingassistant.model.Class_Infor;
import com.app.teachingassistant.model.Result;
import com.app.teachingassistant.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserManage extends AppCompatActivity {
    ActionBar actionBar;
    TextView txtLgName,txtLgRole,txtName,txtGender,txtMail,txtSID,txtPermission;
    Button logout,btnChangeName,btnChangePass,btnChangeGender,btnChangeSID,saveChanges,unSaveChanges;
    CircleImageView changeImage,userAvt;
    FirebaseUser user;
    Uri selectedImageUri;
    String name;
    String gender;
    String permission;
    String sID;
    String email;
    LinearLayout sidContainer;
    final LoadingDialog loadingDialog = new LoadingDialog(this);
    private static final int SELECT_PICTURE = 1;
    private static final int OPEN_REQUEST_CODE = 2;
    private String selectedImagePath;
    private boolean imageChange = false;
    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_infor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Người dùng");
        logout = findViewById(R.id.attend_class_btn);
        logout.setBackground(getResources().getDrawable(R.drawable.border_btn_5dp_red));
        logout.setText("Đăng xuất");

        //Khai báo các view control
        txtLgName = findViewById(R.id.lg_user_name);
        txtLgRole = findViewById(R.id.lg_user_role);
        txtName = findViewById(R.id.txtName);
        txtGender = findViewById(R.id.txtGender);
        txtMail = findViewById(R.id.txt_mail);
        txtSID = findViewById(R.id.txt_sid);
        txtPermission = findViewById(R.id.txtPermission);
        btnChangeName = findViewById(R.id.changeUserName);
        btnChangeGender = findViewById(R.id.changeGender);
        btnChangePass = findViewById(R.id.changePass);
        btnChangeSID = findViewById(R.id.changeSID);
        changeImage = findViewById(R.id.changeImage);
        userAvt = findViewById(R.id.larg_user_avt);
        sidContainer = findViewById(R.id.sid_container);
        saveChanges = findViewById(R.id.saveChange);
        unSaveChanges = findViewById(R.id.unSave);

        saveChanges.setVisibility(View.INVISIBLE);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        }
        loadInfor();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserManage.this,Login.class);
                FirebaseAuth.getInstance().signOut();
                finishAffinity();
                startActivity(intent);

            }
        });
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            loadImage();
                        } else {
                            requestPermission(); // Code for permission
                        }
                    } else {
                        loadImage();
                    }
                }
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfile();
            }
        });
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserFullName();
            }
        });
        btnChangeSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSID();
            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        btnChangeGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserGender();
            }
        });

    }
    //Đổi giới tính
    private void changeUserGender(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customview = inflater.inflate(R.layout.change_gender_dialog,null);
        builder.setView(customview);
        TextView title = customview.findViewById(R.id.dialog_title);
        title.setText("Chọn giới tính");
        Button OK = customview.findViewById(R.id.ok_button);
        Button Cancel = customview.findViewById(R.id.cancel_button);
        RadioButton male = customview.findViewById(R.id.male);
        RadioButton female = customview.findViewById(R.id.female);
        RadioButton other = customview.findViewById(R.id.another);
        other.setChecked(true);
        if(gender.equals("Nam")){
            male.setChecked(true);
        }
        else if(gender.equals("Nữ")){
            female.setChecked(true);
        }
        // Create AlertDialog:
        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.animation;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        // Nút Ok
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Khác";
                if(male.isChecked()){
                    gender = "Nam";
                }
                else if(female.isChecked()){
                    gender = "Nữ";
                }
                txtGender.setText(gender);
                saveChanges.setVisibility(View.VISIBLE);
                alert.dismiss();
            }
        });
    }
    //Đổi mật khẩu
    private void changePassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customview = inflater.inflate(R.layout.change_password,null);
        builder.setView(customview);
        Button OK = customview.findViewById(R.id.ok_button);
        Button Cancel = customview.findViewById(R.id.cancel_button);
        //Edit Text nhập mật khẩu
        final TextInputEditText password = customview.findViewById(R.id.password);
        final TextInputLayout passwordWrapper = customview.findViewById(R.id.passwordWrapper);
        TextView textView = customview.findViewById(R.id.error);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    passwordWrapper.setError("Bạn chưa nhập mật khẩu");
                } else {
                    passwordWrapper.setError(null);
                }
                textView.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //Edit Text nhập mật khẩu mới
        final TextInputEditText passwordNew = customview.findViewById(R.id.passwordNew);
        final TextInputLayout passwordNewWrapper = customview.findViewById(R.id.passwordNewWrapper);
        passwordNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    passwordNewWrapper.setError("Bạn chưa nhập mật khẩu mới");
                } else {
                    passwordNewWrapper.setError(null);
                }
                textView.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



        //Edit Text nhập mật khẩu mới
        final TextInputEditText repasswordNew = customview.findViewById(R.id.repasswordNew);
        final TextInputLayout repasswordNewWrapper = customview.findViewById(R.id.repasswordNewWrapper);
        repasswordNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    repasswordNewWrapper.setError("Bạn chưa nhập lại mật khẩu mới");
                } else {
                    repasswordNewWrapper.setError(null);
                }
                textView.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        // Create AlertDialog:
        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.animation;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        // Nút Ok
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                String newPass = passwordNew.getText().toString();
                String reNewPass = repasswordNew.getText().toString();
                String err = checkNewPass(pass,newPass,reNewPass);
                if(err.length() == 0){
                    loadingDialog.startLoadingAlertDialog();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, pass);
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingDialog.stopLoadingAlertDialog();
                                    if(task.isSuccessful()){
                                        alert.dismiss();
                                        Result changePassResult =  AccountDAO.getInstance().changePassword(user,newPass);
                                        if(!changePassResult.isError()){
                                            Toast.makeText(UserManage.this,"Đổi mật khẩu thành công",Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            Toast.makeText(UserManage.this,"Đổi mật khẩu thất bại",Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(UserManage.this,"Mật khẩu không khớp",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                else {
                    textView.setText(err);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private String checkNewPass(String pass,String newPass,String confirmPass){
        String err="";
        if(pass.length()==0){
            return  err="Bạn chưa nhập mật khẩu";
        }
        else if(newPass.length() == 0){
            return  err="Bạn chưa nhập mật khẩu mới";
        }
        else if(newPass.length() <= 6){
            return  err="Mật khẩu mới";
        }
        else if( !confirmPass.equals(newPass) ){
            return  err="Mật khẩu không khớp";
        }
        return err;
    }
    //Đổi mã số sinh viên
    private void changeSID(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customview = inflater.inflate(R.layout.normal_change_infor_dialog,null);
        builder.setView(customview);
        TextView title = customview.findViewById(R.id.dialog_title);
        title.setText("Đổi tên");
        Button OK = customview.findViewById(R.id.ok_button);
        Button Cancel = customview.findViewById(R.id.cancel_button);
        final TextInputEditText editText = customview.findViewById(R.id.username);
        final TextInputLayout usernameWrapper = customview.findViewById(R.id.usernameWrapper);
        usernameWrapper.setHint("Nhập mã số sinh viên");
        editText.setCompoundDrawables(getDrawable(R.drawable.ic_baseline_call_to_action_24),null,null,null);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    usernameWrapper.setError("Bạn chưa nhập mã số sinh viên");
                } else {
                    usernameWrapper.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        // Create AlertDialog:
        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.animation;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        // Nút Ok
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sID = editText.getText().toString();
                txtSID.setText(sID);
                saveChanges.setVisibility(View.VISIBLE);
                alert.dismiss();
            }
        });
    }
    //Đổi tên
    private void changeUserFullName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thay đổi thông tin");
        LayoutInflater inflater = getLayoutInflater();
        View customview = inflater.inflate(R.layout.normal_change_infor_dialog,null);
        builder.setView(customview);
        Button OK = customview.findViewById(R.id.ok_button);
        Button Cancel = customview.findViewById(R.id.cancel_button);
        final TextInputEditText editText = customview.findViewById(R.id.username);
        final TextInputLayout usernameWrapper = customview.findViewById(R.id.usernameWrapper);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    usernameWrapper.setError("Bạn chưa nhập tên người dùng");
                } else {
                    usernameWrapper.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        // Create AlertDialog:
        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.animation;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        // Nút Ok
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editText.getText().toString();
                txtLgName.setText(name);
                txtName.setText(name);
                saveChanges.setVisibility(View.VISIBLE);
                alert.dismiss();
            }
        });
    }
    //Upload thông tin
    private void uploadProfile(){
        loadingDialog.startLoadingAlertDialog();
        AccountDAO.getInstance().getCurrentUser().setName(name);
        AccountDAO.getInstance().getCurrentUser().setGender(gender);
        AccountDAO.getInstance().getCurrentUser().setRole(permission);
        AccountDAO.getInstance().getCurrentUser().setSID(sID);
        //update user profile
        if(imageChange){
            //update user image
            Result res1 = AccountDAO.getInstance().uploadImageToFireBase(user.getUid(),selectedImageUri);
            if(res1.isError()){
                Toast.makeText(UserManage.this,"Upload ảnh đại diện thất bại",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(UserManage.this,"Upload ảnh đại diện thành công",Toast.LENGTH_SHORT).show();
            }
            AccountDAO.getInstance().getCurrentUser().setHasProfileUrl(true);
        }
        Result res2 = AccountDAO.getInstance().updateProfiles(user.getUid(),AccountDAO.getInstance().getCurrentUser());
        loadingDialog.stopLoadingAlertDialog();
        if(res2.isError()){
            Toast.makeText(UserManage.this,"Upload thông tin người dùng thất bại",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(UserManage.this,"Upload thông tin người dùng thành công",Toast.LENGTH_SHORT).show();
        }
    }
    //Load ảnh từ storage
    private void loadImage(){
        saveChanges.setVisibility(View.VISIBLE);
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);
    }
    //kiểm tra xem đã xin quyền hay chưa
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(UserManage.this,android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result<= PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(UserManage.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(UserManage.this, "Quyền Ghi Bộ nhớ ngoài cho phép chúng tôi đọc các tệp. Vui lòng cho phép quyền này trong Cài đặt Ứng dụng.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(UserManage.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0]<= PackageManager.PERMISSION_GRANTED)  {
            } else {
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadInfor(){
        name = AccountDAO.getInstance().getCurrentUser().getName();
        gender = AccountDAO.getInstance().getCurrentUser().getGender();
        permission = AccountDAO.getInstance().getCurrentUser().getRole();
        sID = AccountDAO.getInstance().getCurrentUser().getSID();
        email = user.getEmail();
        if(permission.equals("Giáo viên")){
            sidContainer.setVisibility(View.GONE);
        }
        if(AccountDAO.getInstance().getCurrentUser().isHasProfileUrl()){
            AccountDAO.getInstance().loadProfileImg(AccountDAO.getInstance().getCurrentUser().getUUID(),userAvt);
        }
        txtLgName.setText(name);
        txtLgRole.setText(permission);
        txtName.setText(name);
        txtGender.setText(gender);
        txtMail.setText(email);
        txtSID.setText(sID);
        txtPermission.setText(permission);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                selectedImagePath = selectedImageUri.getPath();
                userAvt.setImageURI(selectedImageUri);
                imageChange = true;
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        String result;
        // just some safety built in
        if( uri == null ){
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor == null ){
            result = uri.getPath();
        }
        else {
            cursor.moveToFirst();
            int indx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(indx);
            cursor.close();
        }
        // this is our fallback here
        return result;
    }
}
