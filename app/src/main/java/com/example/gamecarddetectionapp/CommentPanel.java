package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentPanel extends AppCompatActivity {

    DatabaseReference commentRef,userRef;

    private List<ModelClass> modelClassList;
    private RecyclerView recyclerView;

    private EditText edtComment;
    private Button submitComment;
    String postkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_panel);

        edtComment = findViewById(R.id.commentsssssId);
        submitComment = findViewById(R.id.commentBtnId);
        recyclerView = findViewById(R.id.recycleVwComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postkey = getIntent().getStringExtra("postkey");

        commentRef = FirebaseDatabase.getInstance().getReference().child("Upload").child(postkey).child("Comments");
//      userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef = FirebaseDatabase.getInstance().getReference().child("Userprofile");



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String userimage = snapshot.child("uimage").getValue().toString();
                            String username = snapshot.child("uname").getValue().toString();
                            commenstDetails(username,userimage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

    private void commenstDetails(String username,String userimage){
        String mComment = edtComment.getText().toString();


        if (mComment.isEmpty()){
            edtComment.setError("Fill it up");
            edtComment.requestFocus();
            return;

        }
        else  {
            Calendar dateValue = Calendar.getInstance();
            /*SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yy");
            String cdate = dateFormat.format(dateValue.getTime());*/

            String cdate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(new Date());

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String ctime = timeFormat.format(dateValue.getTime());


            ModelClass modelClass = new ModelClass(username, userimage, mComment, cdate, ctime);

            String key = commentRef.push().getKey();
            commentRef.child(key).setValue(modelClass);
            edtComment.setText("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ModelClass> options =
                new FirebaseRecyclerOptions.Builder<ModelClass>()
                        .setQuery(commentRef, ModelClass.class)
                        .build();

        FirebaseRecyclerAdapter<ModelClass,CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ModelClass, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull ModelClass model) {
                holder.comment.setText(model.getComment());
                holder.user.setText(model.getUsername());
                holder.date.setText(model.getDate());
                holder.time.setText(model.getTime());
                Glide.with(holder.imgUser.getContext()).load(model.getUserimage()).into(holder.imgUser);

            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.samplelist_layout,parent,false);
                return new CommentsViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }



    }
