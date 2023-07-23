package com.example.gamecarddetectionapp;




import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.gamecarddetectionapp.ml.Model1;
import com.example.gamecarddetectionapp.ml.Model1;
import com.example.gamecarddetectionapp.ml.Model1;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;

public class Detection extends AppCompatActivity {
    Button selectBtn, predictBtn, captureBtn, buttonMap;
    TextView result;
    ImageView imageView;
    Bitmap bitmap;
    int imageSize = 224;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        getPermission();
        String[] labels = new String[0];
        int count = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line = bufferedReader.readLine();
            while (line!= null){
                labels[count]=line;
                count++;
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        selectBtn = findViewById(R.id.selectBtn);
        predictBtn = findViewById(R.id.predictBtn);
        captureBtn = findViewById(R.id.captureBtn);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);


        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);

            }
        });
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });




        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Model1 model = Model1.newInstance(Detection.this);

                    // Creates inputs for reference.
                    TensorImage image = TensorImage.fromBitmap(bitmap);

                    // Runs model inference and gets result.
                    Model1.Outputs outputs = model.process(image);
                    List<Category> probability = outputs.getProbabilityAsCategoryList();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        probability.sort(Comparator.comparing(Category::getScore, Comparator.reverseOrder()));
                    }
                    result.setText(probability.get(0).getLabel()+" "+probability.get(0).getScore());

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }


            }





            int getMax(float[] arr){
                int max=0;
                for(int i = 0; i<arr.length;i++){
                    if(arr[i] > arr[max]) max = i;
                }
                return max;
            }
        });


    }


    void getPermission(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Detection.this, new String[]{Manifest.permission.CAMERA}, 11);

            } else {
                //Request camera permission if we don't have it.
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 12);
            }
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==11){
            if(grantResults.length>0){
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==10){
            if(data!=null){
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode==12){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);



    }
}