package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {
 private EditText editTextUpdateName,editTextUpdateDoB,editTextUpdateMobile;
 private RadioGroup radioGroupUpdateGender;
 private RadioButton radioButtonUpdateGenderSelected;
 private String textFullname,textDoB,textGender,textMobile;
 private FirebaseAuth authProfile;
 private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        getSupportActionBar().setTitle("Update Profile Details");

        progressBar =findViewById(R.id.progressBar);
        editTextUpdateName=findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB=findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile =findViewById(R.id.editText_update_profile_mobile);

        radioGroupUpdateGender=findViewById(R.id.radio_group_update_gender);

        authProfile =FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();


        //show profiledob
        showProfile(firebaseUser);

    //upload profile pic
        Button buttonUploadProfilePic = findViewById(R.id.button_upload_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this
                ,UploadProfilePictureActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //update email
          Button buttonUpdateEmail= findViewById(R.id.button_profile_update_email);
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

         //datepicker edittext
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //extracting saved dd,yy,mm
                String textSADoB[]=textDoB.split("/");



                int day =Integer.parseInt(textSADoB[0]);
                int month =Integer.parseInt(textSADoB[1])-1;
                int year =Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                //date picker dialog
                picker=new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                           editTextUpdateDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);

                picker.show();
            }
        });

//update profile
        Button buttonUpdateProfile=findViewById(R.id.button_profile_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });


    }

    //updateprofile
    private void updateProfile(FirebaseUser firebaseUser) {
   int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
   radioButtonUpdateGenderSelected =findViewById(selectedGenderID);

     // validate mobile no.
                String mobileRegex = "[0-9]{10,13}";
                Matcher mobileMatcher;
                Pattern mobilePattern =Pattern.compile(mobileRegex);
                mobileMatcher=mobilePattern.matcher(textMobile);



                if (TextUtils.isEmpty(textFullname)) {
                    Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextUpdateName.setError("Full name is required");
                    editTextUpdateName.requestFocus();
                }
                 else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(UpdateProfileActivity.this, "please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextUpdateDoB.setError("date of birth is required");
                    editTextUpdateDoB.requestFocus();
                } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
                    Toast.makeText(UpdateProfileActivity.this, "please select your gender", Toast.LENGTH_LONG).show();
                    radioButtonUpdateGenderSelected.setError("Gender is required");
                    radioButtonUpdateGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText( UpdateProfileActivity.this, "please enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("Mobile no. is required");
                    editTextUpdateMobile.requestFocus();
                } else if (textMobile.length() != 11) {
                    Toast.makeText(UpdateProfileActivity.this, "please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("mobile no. should be 11 digits");
                    editTextUpdateMobile.requestFocus();
                }else if(!mobileMatcher.find()){
                   Toast.makeText(UpdateProfileActivity.this, "please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("mobile no. is not valid");
                    editTextUpdateMobile.requestFocus();
                }
                 else{
                     //obtain data  entered by user
                    textGender= radioButtonUpdateGenderSelected.getText().toString();
                    textFullname= editTextUpdateName.getText().toString();
                    textDoB= editTextUpdateDoB.getText().toString();
                    textMobile=editTextUpdateMobile.getText().toString();

                    //enter user dadta into the firebase realtime database
                    ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textDoB,textGender,textMobile);

                     //extract user reference from database
                    DatabaseReference referenceProfile =FirebaseDatabase.getInstance().getReference("Register Users");
                    String userID=firebaseUser.getUid();
                    progressBar.setVisibility(View.VISIBLE);

                    referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){


                                   //settin new display name
                                   UserProfileChangeRequest profileUpdates= new UserProfileChangeRequest.Builder().
                                           setDisplayName(textFullname).build();
                                   firebaseUser.updateProfile(profileUpdates);
                                   Toast.makeText(UpdateProfileActivity.this,"Update suceesful",Toast.LENGTH_LONG).show();
                                    //stop user from returrning updates
                                   Intent intent = new Intent(UpdateProfileActivity.this,
                                           UserProfileActivity.class);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                           Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                   startActivity(intent);
                                   finish();
                               }else{
                                   try {
                                       throw task.getException();
                                   }catch (Exception e){
                                       Toast.makeText(UpdateProfileActivity.this,
                                               e.getMessage(),Toast.LENGTH_LONG).show();

                                   }
                               }
                               progressBar.setVisibility(View.GONE);
                        }
                    });


                }
    }


    //fetch data from datbase
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered= firebaseUser.getUid();

        //extracting user refrence from database
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register Users");  //watch it

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ReadWriteUserDetails reaUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (reaUserDetails!=null){
                    textFullname=firebaseUser.getDisplayName();
                    textDoB=reaUserDetails.doB;
                    textGender = reaUserDetails.gender;
                    textMobile =reaUserDetails.mobile;

                    editTextUpdateMobile.setText(textMobile);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateName.setText(textFullname);

                    //show gender through radiobutton
                    if(textGender.equals("male")){
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_male);
                    }else{
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }else{
                    Toast.makeText(UpdateProfileActivity.this,
                            "something went wrong",Toast.LENGTH_LONG).show();

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(UpdateProfileActivity.this,

                           "something went wrong",Toast.LENGTH_LONG).show();
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
            Intent intent =new Intent(UpdateProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_email){
            Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_setting){
            Toast.makeText(UpdateProfileActivity.this,"menu settings",Toast.LENGTH_SHORT).show();

        }else if(id==R.id.menu_change_password){
            Intent intent= new Intent(UpdateProfileActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_delete_profile){
            Intent intent= new Intent(UpdateProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateProfileActivity.this,MainActivity.class);

            //clear stack to prevent user coming back to userprofileactivity on pressing back bytton after logging
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(UpdateProfileActivity.this,"something went wrong",Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}
