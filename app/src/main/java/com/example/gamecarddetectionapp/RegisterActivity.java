package com.example.gamecarddetectionapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamecarddetectionapp.R.id;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName,editTextRegisterEmail,editTextRegisterDOB,editTextRegisterMobile,
            editTextRegisterPWD,editTextRegisterConfirmPWD;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;

    TextView invalidMsg;
    //

    private static  final  String TAG="RegisterAcitivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        Toast.makeText(RegisterActivity.this,"You can register now",Toast.LENGTH_LONG).show();

        progressBar=findViewById(R.id.progressBar);
        editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=findViewById(R.id.editText_register_email);
        editTextRegisterDOB=findViewById(R.id.editText_register_dob);
        editTextRegisterMobile=findViewById(R.id.editText_register_mobile);
        editTextRegisterPWD=findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPWD=findViewById(R.id.editText_register_confirm_password);

        // radiobutton for gender

        radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        invalidMsg = findViewById(R.id.invalidId);


        editTextRegisterEmail.addTextChangedListener(new TextWatcher() {

            private final long DELAY = 10; // Delay time in milliseconds
            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                handler.removeCallbacks(runnable); // Remove the previous runnable
                runnable = new Runnable() {
                    @Override
                    public void run() {

                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                            if (!charSequence.toString().isEmpty()) { // Check if the email is not empty
                                // Check if email is already registered
                                FirebaseAuth.getInstance().fetchSignInMethodsForEmail(charSequence.toString())
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                List<String> signInMethods = task.getResult().getSignInMethods();
                                                if (signInMethods != null && signInMethods.size() > 0) {
                                                    invalidMsg.setText("This email is already registered.");
                                                } else {
                                                    invalidMsg.setText("");
                                                }
                                            } else {
                                                // Handle errors
                                                invalidMsg.setText(task.getException().getMessage());
                                            }
                                        });
                            } else {
                                invalidMsg.setText("Invalid email address");
                            }
                        } else {
                            invalidMsg.setText("Invalid email address");
                        }
                    }
                };
                handler.postDelayed(runnable, DELAY); // Schedule the new runnable


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //datepicker edittext
        editTextRegisterDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                int day =calendar.get(Calendar.DAY_OF_MONTH);
                int month =calendar.get(Calendar.MONTH);
                int year =calendar.get(Calendar.YEAR);

                //date picker dialog
                picker=new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDOB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);

                picker.show();
            }
        });


        Button buttonRegister= findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDOB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPWD.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPWD.getText().toString();
                String textGender; // cant obtain value

                // validate mobile no.
                String mobileRegex = "[0-9]{10,13}";
                Matcher mobileMatcher;
                Pattern mobilePattern =Pattern.compile(mobileRegex);
                mobileMatcher=mobilePattern.matcher(textMobile);



                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextRegisterDOB.setError("date of birth is required");
                    editTextRegisterDOB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "please select your gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "please enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile no. is required");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("mobile no. should be 11 digits");
                    editTextRegisterMobile.requestFocus();
                }else if(!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this, "please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("mobile no. is not valid");
                    editTextRegisterMobile.requestFocus();
                }
                else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "please enter your password", Toast.LENGTH_LONG).show();
                    editTextRegisterPWD.setError("password is required");
                    editTextRegisterPWD.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "password  should be at least 6 digit", Toast.LENGTH_LONG).show();
                    editTextRegisterPWD.setError("password too weak");
                    editTextRegisterPWD.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "please confirm your password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPWD.setError("password confirmed is required");
                    editTextRegisterConfirmPWD.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "please password is same", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPWD.setError("password confirmation is required");
                    editTextRegisterConfirmPWD.requestFocus();
                    // clear the entered password
                    editTextRegisterPWD.clearComposingText();
                    editTextRegisterConfirmPWD.clearComposingText();
                } else{
                    textGender= radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDoB,textGender,textMobile,textPwd);
                }

            }
        });
    }

    //register user using crdential given
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //create user profile

        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //upDAte display name of user
                            UserProfileChangeRequest profileChangeRequest =new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);


                            //enter user data into the firebase realtime database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB,textGender,textMobile);

                            //extracting user reference from database
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //send verdification email
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this,"user registered succesfully.Please verify your email",
                                                Toast.LENGTH_LONG).show();

                                        //open user profile after succesfull registration
                                        Intent intent= new Intent(RegisterActivity.this,UserProfileActivity.class);
                                        //to prevent user from returning back after succesfull registration
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish(); // to  close register activity
                                    }else{
                                        Toast.makeText(RegisterActivity.this,"user registration has  failed.Please try again",
                                                Toast.LENGTH_LONG).show();

                                    }
                                    //hide progressbar until user registerd
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                        }else{
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                editTextRegisterPWD.setError("your password is too weak.kindly use a mix of alphabets");
                                editTextRegisterPWD.requestFocus();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                editTextRegisterPWD.setError("your email is already in use or already in use.kindly re-enter.");
                                editTextRegisterPWD.requestFocus();
                            }catch (FirebaseAuthUserCollisionException e){
                                editTextRegisterPWD.setError("user is already register with this email.please use another email");
                                editTextRegisterPWD.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                            }
                            //hide progressbar until user registerd
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}



