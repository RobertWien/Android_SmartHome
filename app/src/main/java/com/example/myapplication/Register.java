package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private EditText user, password, retype_password;
    private ProgressBar progressBar;
    private Button btn_signup;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        btn_signup = (Button) findViewById(R.id.register);
        btn_signup.setOnClickListener(this);

        user = (EditText) findViewById(R.id.user_re);
        password = (EditText) findViewById(R.id.password_re);
        retype_password = (EditText) findViewById(R.id.re_password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                RegisterUser();
                break;


        }
    }
    private void RegisterUser() {
        String user_name = user.getText().toString().trim();
        String pass_word = password.getText().toString().trim();
        String retype_pass = retype_password.getText().toString().trim();

        if (user_name.isEmpty()) {
            user.setError("Email must be filled in");
            user.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(user_name).matches()) {
            user.setError("Please enter a valid email !!!");
            ;
            user.requestFocus();
            return;
        }

        if (pass_word.isEmpty()) {
            password.setError("Password must be filled in");
            password.requestFocus();
            return;
        }
        if (pass_word.length() < 6) {
            password.setError("Password must have at least 6 characters");
            password.requestFocus();
            return;
        }

        if (retype_pass.isEmpty()) {
            retype_password.setError("Confirm Password must be filled in");
            retype_password.requestFocus();
            return;
        }
        if (retype_pass.length() < 6) {
            retype_password.setError("Confirm Password must have at least 6 characters");
            retype_password.requestFocus();
            return;
        }

        if (!retype_pass.equals(pass_word)) {
            retype_password.setError("Checkout !!!, your password and confirm password don't match !!!");
            retype_password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(user_name, pass_word)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Your account was created successfully", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(Register.this, "Failed to register, please try to do it again !!!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
    }

}