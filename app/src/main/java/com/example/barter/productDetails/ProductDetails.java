package com.example.barter.productDetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.adapter.ProductAdapter;
import com.example.barter.adapter.ReviewAdapter;
import com.example.barter.adapter.SuggestionAdapter;
import com.example.barter.cart.CartFragment;
import com.example.barter.chat.MessageActivity;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.AddTrade;
import com.example.barter.database.AddTradeHelperClass;
import com.example.barter.database.ChatList;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.Product;
import com.example.barter.database.Rating;
import com.example.barter.database.TradeReq;
import com.example.barter.database.UserHelperClass;
import com.example.barter.payment.PaymentActivity;
import com.example.barter.profile.UserProfile;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetails extends AppCompatActivity {
TextView product_name, product_des,product_color,product_price,product_brand;
ImageView product_image;
Button add_trade_btn, request_trade_btn, address, rating, chat;
FirebaseDatabase rootNode;
private Context mContext;
private Activity mActivity;
private ArrayList<Product> productsList;
private TradeReqAdapter tradeReqAdapter;
private ProgressDialog progressDialog;
private FirebaseFirestore firebaseFirestore;
private FirebaseDatabase firebaseDatabase;
private SimpleDateFormat currentDate;
private SimpleDateFormat currentTime;
private Calendar calendar;
private FirebaseUser firebaseUser;
CartFragment cartFragment;
Uri saveImage;
String uid, saveName, saveDate,savePrice,userID, imageUri, Messagename, message, product_seller, userNames, userAddress;
private CollapsingToolbarLayout collapsingToolbarLayout;
CircleImageView profileImage, img_on, img_off;
TextView userName, sellerName;
RecyclerView recyclerView, rating_view;
private SuggestionAdapter suggestionAdapter;
private ArrayList<Rating> ratingArrayList;
private ReviewAdapter reviewAdapter;
private RatingBar ratings_updated;


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
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapseTB);
        collapsingToolbarLayout.setTitle(getIntent().getExtras().getString("product_name"));
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        product_name = (TextView) findViewById(R.id.product_name);
        product_des = (TextView) findViewById(R.id.product_des);
        product_price = (TextView) findViewById(R.id.product_price);
        product_color = (TextView) findViewById(R.id.product_color);
        product_brand = (TextView) findViewById(R.id.product_brand);
        product_image = (ImageView) findViewById(R.id.product_image);
        add_trade_btn = (Button) findViewById(R.id.add_trade_btn);
        request_trade_btn = (Button) findViewById(R.id.request_trade_btn);
        rating = findViewById(R.id.rating);
        ratings_updated = findViewById(R.id.ratings_updated);
        chat = findViewById(R.id.chat);

        profileImage = findViewById(R.id.profilePic);
        img_on = findViewById(R.id.img_on);
        img_off = findViewById(R.id.img_off);
        userName = findViewById(R.id.name);
        address = findViewById(R.id.address);
        sellerName = findViewById(R.id.sellerName);

        saveName = getIntent().getExtras().getString("product_name");
        product_name.setText(saveName);
        product_des.setText(getIntent().getExtras().getString("product_des"));
        savePrice = getIntent().getExtras().getString("product_price");
        product_price.setText("₱"+savePrice);
        product_color.setText(getIntent().getExtras().getString("product_color"));
        product_brand.setText(getIntent().getExtras().getString("product_brand"));
        Picasso.with(getApplicationContext()).load(Uri.parse(getIntent().getExtras().getString("product_image"))).into(product_image);
        saveImage = Uri.parse(getIntent().getExtras().getString("product_image"));
        product_seller = getIntent().getExtras().getString("product_seller");
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

        calendar = Calendar.getInstance();
        currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveDate = currentDate.format(calendar.getTime());
        currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveTime = currentTime.format(calendar.getTime());


        uid = getIntent().getExtras().getString("product_seller");

        firebaseDatabase.getReference("User").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    String name = userProfile.getName();
                    userName.setText(name);
                    sellerName.setText(userProfile.getName()+ " Product Suggestions");

                    if (userProfile.getFilepath() != null){
                        Picasso.with(getApplicationContext()).load(Uri.parse(userProfile.getFilepath())).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference("User").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    String userID = userProfile.getId();
                    Messagename = userProfile.getName();
                    imageUri = userProfile.getFilepath();
                    userNames = userProfile.getName();
                    userAddress = userProfile.getAddress();
                    //   textView.setText(name);
                    //   Picasso.with(getActivity()).load(imageUri).into(imageView);
                    message = "You suggest to the exchange on " + Messagename +
                            " Discuss the details in chat";

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseFirestore.collection("AddToTrade").whereEqualTo("user", firebaseUser.getUid()).whereEqualTo("product_name",saveName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : value){
                                        AddTrade addTrade = queryDocumentSnapshot.toObject(AddTrade.class);
                                        if (addTrade.getProduct_name().equals(saveName) || addTrade.getUser().equals(firebaseUser.getUid())){
                                            add_trade_btn.setEnabled(false);
                                            add_trade_btn.setBackgroundColor(Color.RED);
                                            add_trade_btn.setTextColor(Color.WHITE);
                                        }else {
                                            add_trade_btn.setEnabled(true);
                                            add_trade_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    cartFragment = new CartFragment();
                                                    AddTradeHelperClass addTradeHelperClass = new AddTradeHelperClass(saveDate,saveTime,saveName,savePrice,userID,saveImage.toString());
                                                    firebaseDatabase.getReference().child("AddToTrade").push().setValue(addTradeHelperClass);
                                                    firebaseFirestore.collection("AddToTrade").add(addTradeHelperClass);
                                                    Toast.makeText(getApplicationContext(),
                                                                    "Add To Cart Success!!",
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            });
                                        }

                                    }
                            }
                        });


        firebaseFirestore.collection("TradeReq").whereEqualTo("product_name_seller",saveName).whereEqualTo("status", "Complete")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : value){
                                    TradeReq tradeReq = queryDocumentSnapshot.toObject(TradeReq.class);
                                    if (tradeReq.getStatus().equals("Complete")){
                                        request_trade_btn.setEnabled(false);
                                        request_trade_btn.setBackgroundColor(Color.RED);
                                        request_trade_btn.setTextColor(Color.WHITE);

                                        add_trade_btn.setEnabled(false);
                                        add_trade_btn.setBackgroundColor(Color.RED);
                                        add_trade_btn.setTextColor(Color.WHITE);

                                    }else {
                                        request_trade_btn.setEnabled(true);
                                        request_trade_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                firebaseDialog();
                                            }
                                        });
                                    }
                                }
                            }
                        });

        request_trade_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDialog();
            }
        });

        add_trade_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cartFragment = new CartFragment();
                AddTradeHelperClass addTradeHelperClass = new AddTradeHelperClass(saveDate,saveTime,saveName,savePrice,userID,saveImage.toString());
                firebaseDatabase.getReference().child("AddToTrade").push().setValue(addTradeHelperClass);
                firebaseFirestore.collection("AddToTrade").add(addTradeHelperClass);
                Toast.makeText(getApplicationContext(),
                                "Add To Cart Success!!",
                                Toast.LENGTH_LONG)
                        .show();
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.putExtra("product_seller",uid);
                startActivity(intent);

            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
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
                        suggestionAdapter = new SuggestionAdapter(getApplicationContext(),ProductDetails.this, productsList);
                        recyclerView.setAdapter(suggestionAdapter);
                        suggestionAdapter.notifyDataSetChanged();
                    }
                });



        rating_view = findViewById(R.id.rating_view);
        rating_view.setHasFixedSize(true);
        rating_view.setLayoutManager(new GridLayoutManager(getApplicationContext(),1, GridLayoutManager.VERTICAL, false));
        ratingArrayList = new ArrayList<>();

        firebaseFirestore.collection("Reviews")
                        .whereEqualTo("name",saveName)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        for (QueryDocumentSnapshot documentSnapshot: value){
                                            Rating rating = documentSnapshot.toObject(Rating.class);
                                            ratingArrayList.add(rating);
                                        }
                                        reviewAdapter = new ReviewAdapter(getApplicationContext(),ProductDetails.this, ratingArrayList);
                                        rating_view.setAdapter(reviewAdapter);
                                        reviewAdapter.notifyDataSetChanged();
                                    }
                                });

        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog();
            }
        });

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    long ratingg = ds.child("totalStarGiven").getValue(Long.class);
                    count = count + ratingg;
                }

                if (dataSnapshot.exists()){
                    long avarage = count / dataSnapshot.getChildrenCount();
                    Log.d("TAG", "avarage: " + avarage);

                    ratings_updated.setRating(avarage);
                } else{
                    ratings_updated.setRating(dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage()); //Don't ignore errors!
            }
        };

        Query query = FirebaseDatabase.getInstance().getReference("Reviews").child("name").equalTo(saveName);

        query.addListenerForSingleValueEvent(valueEventListener);

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
    }


    public void ratingDialog(){
        RelativeLayout bottomSheetRL = findViewById(R.id.ratingCard);

        // creating a variable for our bottom sheet dialog.
        final BottomSheetDialog bottomSheetTradeDialog = new BottomSheetDialog(ProductDetails.this, R.style.BottomSheetDialogTheme);

        // passing a layout file for our bottom sheet dialog.
        View layout = LayoutInflater.from(ProductDetails.this).inflate(R.layout.bottom_rating, bottomSheetRL);

        Button ratingBtn = layout.findViewById(R.id.rating);
        RatingBar ratingCount = layout.findViewById(R.id.ratingBar);
        TextInputLayout review = layout.findViewById(R.id.review);

        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ratings = String.valueOf(ratingCount.getRating());
                String reviews = review.getEditText().getText().toString();
                SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                String date = s.format(new Date());

                Rating insertRating = new Rating(saveName,reviews,date,ratings,firebaseUser.getUid(),product_seller);
                firebaseDatabase.getReference("Reviews").push().setValue(insertRating);
                firebaseFirestore.collection("Reviews").add(insertRating);
                bottomSheetTradeDialog.dismiss();


            }
        });

        // passing our layout file to our bottom sheet dialog.
        bottomSheetTradeDialog.setContentView(layout);

        // below line is to display our bottom sheet dialog.
        bottomSheetTradeDialog.show();

    }

    public void firebaseDialog(){

        RelativeLayout bottomSheetRL =  findViewById(R.id.idRLBottomSheet);

        // creating a variable for our bottom sheet dialog.
        final BottomSheetDialog bottomSheetTradeDialog = new BottomSheetDialog(ProductDetails.this, R.style.BottomSheetDialogTheme);

        // passing a layout file for our bottom sheet dialog.
        View layout = LayoutInflater.from(ProductDetails.this).inflate(R.layout.bottom_trade, bottomSheetRL);

        // passing our layout file to our bottom sheet dialog.
        bottomSheetTradeDialog.setContentView(layout);

        // below line is to display our bottom sheet dialog.
        bottomSheetTradeDialog.show();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button raingBtn = layout.findViewById(R.id.rating);


      RecyclerView recyclerView = layout.findViewById(R.id.tradeProduct);


        productsList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));


        firebaseFirestore.collection("Product")
                .whereEqualTo("product_seller",userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot document : value) {
                            Product product = document.toObject(Product.class);
                            productsList.add(product);
                        }
                        tradeReqAdapter = new TradeReqAdapter(getApplicationContext(),ProductDetails.this,  productsList);
                        recyclerView.setAdapter(tradeReqAdapter);
                        tradeReqAdapter.notifyDataSetChanged();
                    }
                });

        // passing our layout file to our bottom sheet dialog.
        bottomSheetTradeDialog.setContentView(layout);

        // below line is to display our bottom sheet dialog.
        bottomSheetTradeDialog.show();
    }

    public class TradeReqAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private ArrayList<Product> userList;
        private Context context;
        private Activity mActivity;

        public TradeReqAdapter(Context context, Activity mActivity, ArrayList<Product> userList){
            this.context = context;
            this.mActivity = mActivity;
            this.userList = userList;
        }

        public class ProductViewHolder extends RecyclerView.ViewHolder{
            public ImageView user_image;
            public TextView user_name, user_price;
            public CardView layout;
            public ProductViewHolder(View itemView, int viewType){
                super(itemView);
                user_image = (ImageView) itemView.findViewById(R.id.user_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_price = (TextView) itemView.findViewById(R.id.user_price);
                layout = itemView.findViewById(R.id.productcard);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
            return new ProductViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ProductViewHolder viewHolder = (ProductViewHolder) holder;

            viewHolder.user_name.setText(userList.get(position).getProduct_name());
            viewHolder.user_price.setText("₱"+userList.get(position).getProduct_price());
            Picasso.with(viewHolder.user_image.getContext()).load(userList.get(position).getProduct_image()).into(viewHolder.user_image);

            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra("userid", userID);
                    intent.putExtra("product_name_user", saveName);
                    intent.putExtra("product_price_user", savePrice);
                    intent.putExtra("product_image_user", saveImage.toString());
                    intent.putExtra("sellerid", uid);
                    intent.putExtra("product_name_seller", userList.get(position).getProduct_name());
                    intent.putExtra("product_price_seller", userList.get(position).getProduct_price());
                    intent.putExtra("product_image_seller", userList.get(position).getProduct_image());
                    intent.putExtra("status", userList.get(position).getProduct_image());
                    intent.putExtra("user_name", userNames);
                    intent.putExtra("user_address", userAddress);

                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
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