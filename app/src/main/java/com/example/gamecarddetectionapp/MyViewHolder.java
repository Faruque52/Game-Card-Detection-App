package com.example.gamecarddetectionapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView,likeButton,commentsButton;
    TextView textView,userTextView,likeCount;
    DatabaseReference likeReference;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imgVw);
        userTextView = itemView.findViewById(R.id.userTextVw);
        textView = itemView.findViewById(R.id.textVw);
        likeButton = itemView.findViewById(R.id.likeBtn);
        likeCount = itemView.findViewById(R.id.likeCnt);
        commentsButton = itemView.findViewById(R.id.commentButtonId);

    }

    public void getLikeStatus(final String postkey, final String userId){
        likeReference = FirebaseDatabase.getInstance().getReference("Likes");
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postkey).hasChild(userId)){
                    int total_Like = (int) snapshot.child(postkey).getChildrenCount();
                    likeCount.setText(total_Like+"likes");
                    likeButton.setImageResource(R.drawable.ic_liked);
                }
                else{
                    int total_Like = (int) snapshot.child(postkey).getChildrenCount();
                    likeCount.setText(total_Like+"likes");
                    likeButton.setImageResource(R.drawable.ic_dislike);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
