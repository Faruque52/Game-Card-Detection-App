package com.example.gamecarddetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
   // private Button goToVideo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  goToVideo=findViewById(R.id.goToVideoId);


        //set the title
        getSupportActionBar().setTitle("MyPreparation");

       //open login activity
        Button buttonLogin= findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            }
        });

        //open register activity
          Button buttonRegister= findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
               startActivity(intent);
            }
        });
       /* goToVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,GsapActivity.class);
                startActivity(i);
            }
        });*/


    }
}