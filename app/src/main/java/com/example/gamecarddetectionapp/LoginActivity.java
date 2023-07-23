package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail,editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    FirebaseAuth mAuth;

    TextView goToRegister;

    private static final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    getSupportActionBar().setTitle("Login");

    goToRegister = findViewById(R.id.gotoSignupId);

    editTextLoginEmail=findViewById(R.id.editText_login_email);
    editTextLoginPwd=findViewById(R.id.editText_login_pwd);
    progressBar=findViewById(R.id.login_progressbarid);

    authProfile =FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //reset password
        Button buttonForgotPassword = findViewById(R.id.button_forgot_password);

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

            }
        });
    buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(LoginActivity.this,"you can reset your password now!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
        }
    });


    //show hide password using eye icon
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
       imageViewShowHidePwd.setImageResource(R.drawable.ic_show_hide);
       imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                   //if password is visible then hide it
                   editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                   //change icon
                imageViewShowHidePwd.setImageResource(R.drawable.ic_show_hide);
               }else{
                   editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                   imageViewShowHidePwd.setImageResource(R.drawable.ic_hide);
               }
           }
       });

    //login user
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail= editTextLoginEmail.getText().toString();
                String textPwd= editTextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this,"please enter your e-mail",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                     Toast.makeText(LoginActivity.this,"please re-enter your e-mail",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email is required");
                    editTextLoginEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                     Toast.makeText(LoginActivity.this,"please enter your password",Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("password is required");
                    editTextLoginEmail.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });



    }

    private void loginUser(String email, String pwd) {
    authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
       if (task.isSuccessful()){

       //get instance of the current user
           FirebaseUser firebaseUser=authProfile.getCurrentUser();


           //check if email is verified before user can access there profile
           if(firebaseUser.isEmailVerified()){
               Toast.makeText(LoginActivity.this,"you are logged in now",Toast.LENGTH_SHORT).show();

           //'open user profile
             //start user profile activity
          startActivity(new Intent(LoginActivity.this,HomeActivity.class));
         finish(); //close loginActivity



           }else{
               firebaseUser.sendEmailVerification();
               authProfile.signOut(); //sign out user
               showAlertDialog();
           }

       }   else{
           try{
               throw task.getException();
           }catch (FirebaseAuthInvalidUserException e){
               editTextLoginEmail.setError("user does not exists or is no longer valid.please register again");
               editTextLoginEmail.requestFocus();
           }catch (FirebaseAuthInvalidCredentialsException e){
                editTextLoginEmail.setError("invalid credentials.kindly,check and re-enter");
               editTextLoginEmail.requestFocus();
           } catch (Exception e){
               Log.e(TAG,e.getMessage());
               Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
           }
       }
       progressBar.setVisibility(View.GONE);

        }
    });

    }

    private void showAlertDialog() {
        //set up alert builder
        AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("email is not verified");
        builder.setMessage("please verify your email now.you cant login without email verification");

        //open email apps if the user clicked/taps continue button
        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent =new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to email app in new window and not withhin this app
                startActivity(intent);

            }
        });
// create the alterdialog
        AlertDialog alertDialog= builder.create();

        //show the aleterdialog
        alertDialog.show();
    }

    //check if user is already logged in.in such case straightway take the user to the users profile
    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }

    }

}