package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr,editTextPwdNew,editTextPwdConfirmNew; //watch
    private TextView textViewAuthenticated;
    private Button buttonChangePwd,buttonReAuthenticated;
    private ProgressBar progressBar;
    private String userPwdCurr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");

        editTextPwdNew=findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr =findViewById(R.id.editText_change_pwd_current);
        editTextPwdConfirmNew=findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated=findViewById(R.id.textView_change_pwd_authenticated);
        progressBar=findViewById(R.id.progressBar);
        buttonReAuthenticated=findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd =findViewById(R.id.button_change_pwd);

        //Disable edittext for new password
        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if (firebaseUser.equals("")){
            Toast.makeText(ChangePasswordActivity.this,"something went wrong user details is not available",Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }else{
            reAuthenticateUser(firebaseUser);
        }

    }

     //reauthenticate user before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr=editTextPwdCurr.getText().toString();

                if (TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePasswordActivity.this,"password is needed",Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("please enter your current password to authenticate");
                    editTextPwdCurr.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);


                    //reauthenticate user now
                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful()){
                              progressBar.setVisibility(View.GONE);

                              //DISABLE EDITEXT FOR CURRENT PASSWORD
                              editTextPwdCurr.setEnabled(false);
                              editTextPwdNew.setEnabled(true);
                              editTextPwdConfirmNew.setEnabled(true);


                              //enable change pwd button.disable authenticate button
                              buttonReAuthenticated.setEnabled(false);
                              buttonChangePwd.setEnabled(true);

                              //set textview to show user is authenticated
                              textViewAuthenticated.setText("You are authenticated/verified"+"change password now");
                              Toast.makeText(ChangePasswordActivity.this,"Password has been verified"+"change password now",Toast.LENGTH_SHORT).show();

                              //update color of change password button
                              buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,R.color.dark_green));

                              buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      changePwd(firebaseUser);
                                  }
                              });

                          }else{
                              try {
                                  throw task.getException();
                              }catch (Exception e){
                                  Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                              }
                          }
                          progressBar.setVisibility(View.GONE);
                        }
                    });
                }


            }
        });



    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew =editTextPwdConfirmNew.getText().toString();

        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePasswordActivity.this,"new password is needed",Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("please enter your new password");
            editTextPwdNew.requestFocus();
        }else if(TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(ChangePasswordActivity.this,"please confirm your new password",Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("please re-enter new password");
            editTextPwdConfirmNew.requestFocus();
        }else if(!userPwdNew.matches(userPwdConfirmNew)){
             Toast.makeText(ChangePasswordActivity.this,"password did not match",Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("please re-enter same password");
            editTextPwdConfirmNew.requestFocus();
        }else if(userPwdCurr.matches(userPwdNew)){
             Toast.makeText(ChangePasswordActivity.this,"new password cannot be same as old password",Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("please enter a new password");
            editTextPwdNew.requestFocus();
        }else{
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()){
                      Toast.makeText(ChangePasswordActivity.this,"password has  been changed",Toast.LENGTH_SHORT).show();
                      Intent intent = new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
                      startActivity(intent);
                      finish();
                  } else{
                      try {
                          throw task.getException();
                      }catch (Exception e){
                          Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                      }
                  }
                  progressBar.setVisibility(View.GONE);
                }
            });
        }
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
            Intent intent =new Intent(ChangePasswordActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_email){
            Intent intent = new Intent(ChangePasswordActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_setting){
            Toast.makeText(ChangePasswordActivity.this,"menu settings",Toast.LENGTH_SHORT).show();

        }else if(id==R.id.menu_change_password){
            Intent intent= new Intent(ChangePasswordActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_delete_profile){
            Intent intent= new Intent(ChangePasswordActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);

            //clear stack to prevent user coming back to userprofileactivity on pressing back bytton after logging
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(ChangePasswordActivity.this,"something went wrong",Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
