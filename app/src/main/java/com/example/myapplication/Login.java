package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText username, password;
    private Button login;
    private TextView register,reset_password;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // bỏ taskbar mặc định có sẵn
        //  getSupportActionBar().hide();


        login = (Button) findViewById(R.id.btn_signin);
        login.setOnClickListener(this);

        username = (EditText) findViewById(R.id.edittextuser);
        password = (EditText) findViewById(R.id.edittextpassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        reset_password = (TextView) findViewById(R.id.forgetpassword);
        reset_password.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_signin:
                userLogin();
                break;
            case R.id.register:
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                break;
            case R.id.forgetpassword:
                Dialog dialog = new Dialog(Login.this);
                dialog.setTitle("RESET PASSWORD PANEL");
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.reset_password);
                EditText fill_user_email = (EditText) dialog.findViewById(R.id.fill_email);
                Button btn_resetSend = (Button) dialog.findViewById(R.id.btn_reset_password_send);
                Button btn_resetCancel = (Button) dialog.findViewById(R.id.btn_reset_password_cancel);

                btn_resetSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (fill_user_email.getText().length() != 0) {
                            String fillUserEmail = fill_user_email.getText().toString().trim();

                            if (!Patterns.EMAIL_ADDRESS.matcher(fillUserEmail).matches()){
                                fill_user_email.setError("Please enter a valid email !!!");;
                                fill_user_email.requestFocus();
                                return;
                            }

                            progressBar.setVisibility(View.VISIBLE);

                            mAuth.sendPasswordResetEmail(fillUserEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Login.this,"Check your email address to reset your password",Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Login.this,"Please, try again! Something wrong happened !!!",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                            dialog.cancel();

                        } else {
                            // thông báo kéo chưa nhập thông sô tốc độ quạt
                            new AlertDialog.Builder(Login.this)
                                    .setTitle("WARNING")
                                    .setMessage("Please, Fill in your email user account !!!")
                                    .setIcon(R.drawable.ic_warning)
                                    .setCancelable(false)
                                    .setPositiveButton("UNDERSTAND", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).create().show();
                            fill_user_email.setError("Please enter your email address here !!!");;
                            fill_user_email.requestFocus();
                        }
                    }
                });

                btn_resetCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
                break;
        }

    }
    private  void userLogin(){
        String user_name = username.getText().toString().trim();
        String pass_word = password.getText().toString().trim();

        // xử lý cập nhật người dùng đăng vào hệ thống điều khiển của ngôi nhà

        final DatabaseReference User = database.getReference("Login/User");
        final DatabaseReference Password = database.getReference("Login/Password");

        if (user_name.isEmpty()){
            username.setError("Email must be filled in");
            username.requestFocus();
            return;
        }

        if (! Patterns.EMAIL_ADDRESS.matcher(user_name).matches()){
            username.setError("Please enter a valid email !!!");;
            username.requestFocus();
            return;
        }

        if(pass_word.isEmpty()){
            password.setError("Password must be filled in");
            password.requestFocus();
            return;
        }
        if(pass_word.length() < 6){
            password.setError("Password must have at least 6 characters");
            password.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(user_name,pass_word)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // cập nhật người dùng đang đăng nhập lên Firebase
                            User.setValue(user_name);
                            Password.setValue(pass_word);

                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            finish();

                        }else {
                            Toast.makeText(Login.this,"Failed to login! Please check out your accounts",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}