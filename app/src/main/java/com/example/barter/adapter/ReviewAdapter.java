package com.example.barter.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.database.Rating;
import com.example.barter.database.UserHelperClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Rating> userList;
    private Context context;
    private Activity mActivity;

    public ReviewAdapter(Context context, Activity mActivity, ArrayList<Rating> userList){
        this.context = context;
        this.mActivity = mActivity;
        this.userList = userList;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        public ImageView user_image;
        public TextView user_name,review;
        public CardView layout;
        public RatingBar ratings;
        public ReviewViewHolder(View itemView, int viewType){
            super(itemView);
            user_image = (ImageView) itemView.findViewById(R.id.profilePic);
            user_name = (TextView) itemView.findViewById(R.id.name);
            ratings = (RatingBar) itemView.findViewById(R.id.rating);
            review = (TextView) itemView.findViewById(R.id.review);
            layout = itemView.findViewById(R.id.reviewCard);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
        return new ReviewViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;

        reviewViewHolder.ratings.setRating(Float.parseFloat(userList.get(position).getTotalStarGiven()));
        reviewViewHolder.review.setText(userList.get(position).getReview());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.child(userList.get(position).getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    reviewViewHolder.user_name.setText(userProfile.getName());
                    String imageUri = userProfile.getFilepath().trim();
                    Picasso.with(holder.itemView.getContext()).load(imageUri).into(reviewViewHolder.user_image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
