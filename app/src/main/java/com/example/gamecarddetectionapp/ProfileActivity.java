package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {
    private ImageView userImage;
    private EditText username;
    private Button updateButton;

    DatabaseReference profileDatabaseReference;
    StorageReference profileStorageReference;
    Uri imageUri;
    Bitmap bitmap;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.userImmage);
        username = findViewById(R.id.userProfileName);
        updateButton = findViewById(R.id.userProfileUpdateBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        profileDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Userprofile");
        profileStorageReference = FirebaseStorage.getInstance().getReference();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Please select file"), 101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == this.RESULT_OK) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                userImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateProfile() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File Uploader");
        progressDialog.show();

        final StorageReference uploader = profileStorageReference.child("profileimages/" + "img" + System.currentTimeMillis());
        uploader.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final Map<String, Object> map = new HashMap<>();
                                map.put("uimage", uri.toString());
                                map.put("uname", username.getText().toString());

                                profileDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            profileDatabaseReference.child(userId).updateChildren(map);
                                        } else {
                                            profileDatabaseReference.child(userId).setValue(map);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        profileDatabaseReference.child((userId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username.setText(snapshot.child("uname").getValue().toString());
                    Glide.with(getApplicationContext()).load(snapshot.child("uimage").getValue().toString()).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}