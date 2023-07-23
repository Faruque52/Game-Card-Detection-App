package com.example.gamecarddetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class GsapActivity extends AppCompatActivity {
WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsap);

        WebView webView=(WebView) findViewById(R.id.GsapId);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");

    }
}