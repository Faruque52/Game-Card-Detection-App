package com.example.gamecarddetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class SassImplementation extends AppCompatActivity {
    WebView webview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sass_implementation);
        webview1=(WebView) findViewById(R.id.sassId);
        webview1.getSettings().setJavaScriptEnabled(true);
        webview1.loadUrl("file:///android_asset/Sass/index.html");
    }
}