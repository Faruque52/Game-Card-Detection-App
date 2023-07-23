package com.example.gamecarddetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class EmbedVideoActivity extends AppCompatActivity {
     private YouTubePlayerView youTubePlayerView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embed_video);

        youTubePlayerView=findViewById(R.id.youtubeplayerID);
      //  button=findViewById(R.id.youtubeButtonId);

    }
}