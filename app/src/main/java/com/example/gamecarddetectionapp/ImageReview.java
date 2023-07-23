package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ImageReview extends AppCompatActivity {


    EditText imgname;
    ImageView imageView;
    Button pickImage, upload;
    DatabaseReference databaseReference, userRef;
    StorageReference storageReference;
    Uri imageUri;
    StorageTask uploadTask;


    private int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_review);

        imgname= (EditText)findViewById(R.id.title);

        pickImage= (Button)findViewById(R.id.selectId);
        upload = (Button)findViewById(R.id.uploadId);

        imageView = (ImageView)findViewById(R.id.imageId);

        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        storageReference = FirebaseStorage.getInstance().getReference("Upload");


        //In order to show username
        userRef = FirebaseDatabase.getInstance().getReference().child("Userprofile");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String username = snapshot.child("uname").getValue().toString();
                            saveData(username);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

          /*      if(uploadTask!=null && uploadTask.isInProgress()){
                    Toast.makeText(ImageReview.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
                }

                else{
                    saveData();
                }*/
            }
        });




        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

    }



    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == this.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);

        }
    }

    //Image Extension
    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void saveData(String username){
        String imageName = imgname.getText().toString().trim();

        if(imageName.isEmpty()){
            imgname.setError("Enter a Title");

        }

        else {
            StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ImageReview.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadUri = uriTask.getResult();

                            Upload upload = new Upload(imageName, downloadUri.toString(), username);
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(upload);
                            nextActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ImageReview.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void nextActivity(){
        imgname.setText("");
        Intent intent = new Intent(ImageReview.this,ShowImageReview.class);
        startActivity(intent);
    }
    }
