package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowImageReview extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Upload> uploadList;
    DatabaseReference databaseReference, likeref;
    private ProgressBar progressBar;
    Boolean testclick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_review);

        recyclerView = findViewById(R.id.reView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.recycleProgressId);


        uploadList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        likeref =  FirebaseDatabase.getInstance().getReference("Likes");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot datasnap : snapshot.getChildren()){
                    Upload upload = datasnap.getValue(Upload.class);
                    uploadList.add(upload);
                }

                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ShowImageReview.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });



        FirebaseRecyclerOptions<Upload> options =
                new FirebaseRecyclerOptions.Builder<Upload>()
                        .setQuery(databaseReference, Upload.class)
                        .build();


        FirebaseRecyclerAdapter<Upload, MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Upload, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Upload model) {
                Upload upload = uploadList.get(position);
                holder.textView.setText(upload.getImageName());
                holder.userTextView.setText(upload.getUsername());
                Picasso.get().load(upload.getImageUri())
                        .fit()
                        .centerCrop()
                        .into(holder.imageView);

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = firebaseUser.getUid();
                String postkey = getRef(position).getKey();

                holder.getLikeStatus(postkey,userId);


                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        testclick = true;

                        likeref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(testclick == true){
                                    if(snapshot.child(postkey).hasChild(userId)){
                                        likeref.child(postkey).child(userId).removeValue();
                                        testclick = false;
                                    }
                                    else{
                                        likeref.child(postkey).child(userId).setValue(true);
                                        testclick = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                holder.commentsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),CommentPanel.class);
                        intent.putExtra("postkey",postkey);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample,parent,false);
                return new MyViewHolder(view);

            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    }
