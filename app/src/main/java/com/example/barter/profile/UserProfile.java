package com.example.barter.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.barter.R;
import com.example.barter.adapter.ProductAdapter;
import com.example.barter.adapter.ReviewAdapter;
import com.example.barter.chat.MessageActivity;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Product;
import com.example.barter.database.Rating;
import com.example.barter.database.UserHelperClass;
import com.example.barter.productDetails.ProductDetails;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    CircleImageView profileImage, img_on, img_off;
    TextView userName;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    private ProductAdapter productAdapter;
    private ProgressDialog progressDialog;
    RecyclerView recyclerView,rating_view;
    private ArrayList<Product> productsList;
    private String uid;
    private Button chat;
    private ReviewAdapter reviewAdapter;
    private ArrayList<Rating> ratingArrayList;
    private String current_user_name;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapseTB);

        profileImage = findViewById(R.id.profilePic);
        img_on = findViewById(R.id.img_on);
        img_off = findViewById(R.id.img_off);
        userName = findViewById(R.id.name);
        chat = findViewById(R.id.chat);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        uid = getIntent().getExtras().getString("product_seller");

        firebaseDatabase.getReference("User").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    userName.setText(userProfile.getName());
                    current_user_name = userProfile.getName().trim();
                    Picasso.with(getApplicationContext()).load(Uri.parse(userProfile.getFilepath())).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false));
        productsList = new ArrayList<>();

        firebaseFirestore.collection("Product")
                .whereEqualTo("product_seller", uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot document : value) {
                            Product product = document.toObject(Product.class);
                            productsList.add(product);
                        }
                        productAdapter = new ProductAdapter(getApplicationContext(),UserProfile.this, productsList);
                        recyclerView.setAdapter(productAdapter);
                        productAdapter.notifyDataSetChanged();
                    }
                });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.getReference("User").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                        if (userProfile != null){
                            String name = userProfile.getName();
                            Intent chatIntent = new Intent(getApplicationContext(), MessageActivity.class);
                            chatIntent.putExtra("id", userProfile.getId());
                            chatIntent.putExtra("name", name);
                            chatIntent.putExtra("filepath", userProfile.getFilepath());
                            startActivity(chatIntent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        rating_view = findViewById(R.id.rating_view);
        rating_view.setHasFixedSize(true);
        rating_view.setLayoutManager(new GridLayoutManager(getApplicationContext(),1, GridLayoutManager.VERTICAL, false));
        ratingArrayList = new ArrayList<>();

        firebaseFirestore.collection("Reviews")
                .whereEqualTo("product_seller",uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot documentSnapshot: value){
                            Rating rating = documentSnapshot.toObject(Rating.class);
                            ratingArrayList.add(rating);
                        }
                        reviewAdapter = new ReviewAdapter(getApplicationContext(), UserProfile.this, ratingArrayList);
                        rating_view.setAdapter(reviewAdapter);
                        reviewAdapter.notifyDataSetChanged();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case android.R.id.home:
                // addSomething();
                finish();
                break;
            default:
        }
        return true;
    }

}