package com.example.gamecarddetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Miscellaneous extends AppCompatActivity {
    Button goToGsap, goToEmbeddedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miscellaneous);
        goToGsap = findViewById(R.id.button_gasp);
        goToEmbeddedVideo = findViewById(R.id.button_embedded_video);
        goToGsap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Miscellaneous.this, GsapActivity.class);
                startActivity(i);
            }


        });
        goToEmbeddedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Miscellaneous.this, EmbedVideoActivity.class);
                startActivity(i);

            }
        });

    }
}