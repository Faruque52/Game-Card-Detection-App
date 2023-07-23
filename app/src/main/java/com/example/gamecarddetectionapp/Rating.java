package com.example.gamecarddetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Map;

public class Rating extends AppCompatActivity {

    private RatingBar ratingBar,ratingBarShow;
    private TextView ratingValue, thankingText,ratingValueShow;



    private Button submitRating;
    private float userRating;

    DatabaseReference ratingReference;
    DatabaseReference ratedReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingBar = findViewById(R.id.ratingId);
        ratingValue = findViewById(R.id.ratingValueId);
        thankingText = findViewById(R.id.thankingId);
        submitRating = findViewById(R.id.ratingSubmitId);

        ratingBarShow = findViewById(R.id.showRatingId);
        ratingValueShow = findViewById(R.id.showRatingValueId);
        ratedReference = FirebaseDatabase.getInstance().getReference().child("Rating");



        ratingReference = FirebaseDatabase.getInstance().getReference("Rating");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = firebaseUser.getUid();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                userRating = rating;
                ratingValue.setText(""+userRating);

            }
        });

        submitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RatingDetails ratingDetails = new RatingDetails(userId, userRating);

                ratingReference.child(userId).setValue(ratingDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    thankingText.setText("Thank you for rating us");
                                }
                                else{
                                    thankingText.setText("Error");
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ratedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalUser = (int) dataSnapshot.getChildrenCount();
                float sumOfRating = 0;

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object totalRating = map.get("rating");
                    float rating = Float.parseFloat(String.valueOf(totalRating));
                    sumOfRating += rating;

                    float averageRating = (sumOfRating/totalUser);

                    DecimalFormat value = new DecimalFormat("#.#");
                    ratingBarShow.setRating(averageRating);
                    ratingValueShow.setText(value.format(averageRating));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });
    }

}