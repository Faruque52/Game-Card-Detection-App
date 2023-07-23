package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthincated;
    private String userOldEmail,userNewEmail,userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail,editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setTitle("Update Email");

        progressBar= findViewById(R.id.progressBar);
        editTextPwd =findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail =findViewById(R.id.editText_update_email_new);
        textViewAuthincated=findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail =findViewById(R.id.button_update_email);

    buttonUpdateEmail.setEnabled(false); //make button disabled
    editTextNewEmail.setEnabled(false);

    authProfile=FirebaseAuth.getInstance();
    firebaseUser=authProfile.getCurrentUser();

    //set old email id on textview
        userOldEmail=firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this,"something went wrong.user details is not availabale",Toast.LENGTH_LONG).show();
        }else{
            reAuthenticate(firebaseUser);
        }
    }

    //reauthenticate user
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser =findViewById(R.id.button_authentic_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //obtain password for authentication
            userPwd= editTextPwd.getText().toString();

            if(TextUtils.isEmpty(userPwd)){
                Toast.makeText(UpdateEmailActivity.this,"password is needed to continue",Toast.LENGTH_SHORT).show();
                editTextPwd.setError("Please enter your password for authentication");
             editTextPwd.requestFocus();
            }else{
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential= EmailAuthProvider.getCredential(userOldEmail,userPwd);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                          progressBar.setVisibility(View.GONE);

                      Toast.makeText(UpdateEmailActivity.this,"password has been verifiede"+"you can update email now",Toast.LENGTH_LONG).show();

                      //set textview to show that user authentication
                        textViewAuthincated.setText("you are authenticated.you can update your email now");

                        //disable edittext for password,button and enable edittext for new email and update email
                        editTextNewEmail.setEnabled(true);
                        editTextPwd.setEnabled(false);
                        buttonVerifyUser.setEnabled(false);
                        buttonUpdateEmail.setEnabled(true);

                        //change color of update email button
                        buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,
                                R.color.dark_green));

                        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userNewEmail =editTextNewEmail.getText().toString();
                                if (TextUtils.isEmpty(userNewEmail)){
                                    Toast.makeText(UpdateEmailActivity.this,"new email is required",Toast.LENGTH_SHORT).show();
                                    editTextNewEmail.setError("please enter new email");
                                    editTextNewEmail.requestFocus();
                                }else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                      Toast.makeText(UpdateEmailActivity.this,"please enter valid email",Toast.LENGTH_SHORT).show();
                                    editTextNewEmail.setError("please provide valid email");
                                    editTextNewEmail.requestFocus();
                                }else if (userOldEmail.matches(userNewEmail)){
                                      Toast.makeText(UpdateEmailActivity.this,"new email cant be same as old email",Toast.LENGTH_SHORT).show();
                                    editTextNewEmail.setError("please enter new email");
                                    editTextNewEmail.requestFocus();
                                }else{
                                    progressBar.setVisibility(View.VISIBLE);
                                    updateEmail(firebaseUser);
                                }
                            }
                        });
                    }else{
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    }
                });
            }
            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){

                    //verify email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this,"Email has been updated.please verify your new email",Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(UpdateEmailActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    //creating Actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate menu items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

//when any menu is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int  id = item.getItemId();

        if (id == R.id.menu_refresh){
            //refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }else if (id==R.id.menu_update_profile){
            Intent intent =new Intent(UpdateEmailActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_email){
            Intent intent = new Intent(UpdateEmailActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_setting){
            Toast.makeText(UpdateEmailActivity.this,"menu settings",Toast.LENGTH_SHORT).show();

        }else if(id==R.id.menu_change_password){
            Intent intent= new Intent(UpdateEmailActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_delete_profile){
            Intent intent= new Intent(UpdateEmailActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateEmailActivity.this,MainActivity.class);

            //clear stack to prevent user coming back to userprofileactivity on pressing back bytton after logging
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(UpdateEmailActivity.this,"something went wrong",Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}