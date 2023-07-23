package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    CardView showReview, goUserProfileActivity, goRating, goGoogleMap, getGoDetection, goToPost, goToProfile, goToMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        showReview = findViewById(R.id.idReview);
        goUserProfileActivity = findViewById(R.id.goUserProfileId2);
        goRating = findViewById(R.id.ratingId);
        goGoogleMap = findViewById(R.id.map_Id);
        getGoDetection = findViewById(R.id.detection_Id);
        goToPost = findViewById(R.id.post_Id);
        goToProfile = findViewById(R.id.goUserProfileId);
        goToMore = findViewById(R.id.goToMoreId);


        goRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iii = new Intent(HomeActivity.this,Rating.class);
                startActivity(iii);
            }
        });
        goUserProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(HomeActivity.this,UserProfileActivity.class);
                startActivity(ii);
            }
        });
        ///google map

        goGoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(HomeActivity.this,GoogleMapActivity.class);
                startActivity(ii);
            }
        });


       //detection

        getGoDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(HomeActivity.this,Detection.class);
                startActivity(ii);
            }
        });
        //go to post
        goToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(HomeActivity.this,ImageReview.class);
                startActivity(ii);
            }
        });
        goToMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(HomeActivity.this,Miscellaneous.class);
                startActivity(ii);
            }
        });



        showReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,ShowImageReview.class);
                startActivity(i);
            }
        });


        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logoutMenuId:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}