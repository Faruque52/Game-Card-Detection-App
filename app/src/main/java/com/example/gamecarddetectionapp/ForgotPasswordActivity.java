package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button buttonPwdReset;
    private EditText editTextPwdResetEmail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private final static  String TAG = "ForgotPasswordActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextPwdResetEmail=findViewById(R.id.editText_password_reset_email);
        buttonPwdReset=findViewById(R.id.button_password_reset);
        progressBar=findViewById(R.id.forgot_progressBar);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =editTextPwdResetEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this,"please enter your registered email",Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Email is required");
                    editTextPwdResetEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                   Toast.makeText(ForgotPasswordActivity.this,"please valid email",Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Valid Email is required");
                    editTextPwdResetEmail.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });


    }

    private void resetPassword(String email) {
        authProfile =FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,"please check your inbox for password reset link",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this,MainActivity.class);



            //clear stack to prevent user coming back to forgotpasswordactivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();  //close userprofileactivity
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextPwdResetEmail.setError("user does not exists or is no longer valid.please register again");
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    }
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}