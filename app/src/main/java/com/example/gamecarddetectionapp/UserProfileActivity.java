package com.example.gamecarddetectionapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome,textViewFullName,textViewEmail,textViewDoB,textViewGender,textViewMobile;
    private ProgressBar progressBar;
    private String fullName,email,doB,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);



        getSupportActionBar().setTitle("Home");
        // goToMap = findViewById(R.id.mapId);
        /*goToGSAP=findViewById(R.id.goToGsapId);
        goToVideo=findViewById(R.id.goToVideoId);*/

        // goToUpload = findViewById(R.id.goToUpload);
        //  goToDetection = findViewById(R.id.goToDetectionId);

        // goToUpload = findViewById(R.id.goToUpload);
        // goToReview = findViewById(R.id.goToReview);

        /*goToReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, ShowImageReview.class);
                startActivity(i);
            }
        });*/


        /*goToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, ImageReview.class);
                startActivity(i);
            }
        });*/



        textViewWelcome=findViewById(R.id.textView_show_welcome);
        textViewFullName =findViewById(R.id.textView_show_full_name);
        textViewEmail =findViewById(R.id.textView_show_email);
        textViewDoB=findViewById(R.id.textView_show_dob);
        textViewGender=findViewById(R.id.textView_show_gender);
        textViewMobile=findViewById(R.id.textView_show_mobile);
        progressBar= findViewById(R.id.progressBar);

        //set onclicklistener on imageview to open uploadprofileactivity
        imageView = findViewById(R.id.imageView_profile_dp);

        //show gsap animation
       /* goToGSAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this,GsapActivity.class);
                startActivity(i);
            }
        });*/

        //show embeded map
     /*   goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this,ActivityMap.class);
                startActivity(i);
            }
        });*/
        //show embeded video
       /* goToVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this,EmbedVideoActivity.class);
                startActivity(i);
            }
        });*/
       /* goToDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this,Detection.class);
                startActivity(i);
            }
        });*/


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this,UploadProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser==null){
            Toast.makeText(UserProfileActivity.this,"something went wrong user details is not available at the moment",
                    Toast.LENGTH_SHORT).show();
        }else{
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //set up alert builder
        AlertDialog.Builder builder= new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("email is not verified");
        builder.setMessage("please verify your email now.you cant login without email verification next time");

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

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID= firebaseUser.getUid();


        //extracting user reference referenceprofile for "registerd user"
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Register Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails= snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null){
                    fullName =firebaseUser.getDisplayName();
                    email =firebaseUser.getEmail();
                    doB=readUserDetails.doB;
                    gender=readUserDetails.gender;
                    mobile=readUserDetails.mobile;

                    textViewWelcome.setText("welcome, "+fullName+ "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);


                    //set user dp(After user has uploaded
                    Uri uri = firebaseUser.getPhotoUrl();

                    // imageviewer set imageuri should not be used with regular uris.so using picasso
                    Picasso.get().load(uri).into(imageView);
                    //  Picasso.get().load(uri).into(imageView);

                }else{
                    Toast.makeText(UserProfileActivity.this,"something went wrong",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this,"something went wrong",Toast.LENGTH_LONG).show();
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
            Intent intent =new Intent(UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_update_email){
            Intent intent = new Intent(UserProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }else if(id == R.id.menu_update_setting){
            Toast.makeText(UserProfileActivity.this,"menu settings",Toast.LENGTH_SHORT).show();

        }else if(id==R.id.menu_change_password){
            Intent intent= new Intent(UserProfileActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_delete_profile){
            Intent intent= new Intent(UserProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);



            //clear stack to prevent user coming back to userprofileactivity on pressing back bytton after logging
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(UserProfileActivity.this,"something went wrong",Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}