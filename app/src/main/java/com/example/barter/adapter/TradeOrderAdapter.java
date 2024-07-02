package com.example.barter.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.NotifHelperClass;
import com.example.barter.database.Rating;
import com.example.barter.database.ReturnHelper;
import com.example.barter.database.TradeReq;
import com.example.barter.database.UserHelperClass;
import com.example.barter.database.Users;
import com.example.barter.productDetails.ProductDetails;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class TradeOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TradeReq> tradeList;
    private Context context;
    private Activity mActivity;
    private FirebaseUser firebaseUser;
    String name;
    public static final String ACCOUNT_SID = "ACCOUNT_SID";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    String Messagename, imageUri, message, SellerName, imageUri1, customerName, customerAddress;
    FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    public TradeOrderAdapter(List<TradeReq> tradeList, Context context){
        this.tradeList = tradeList;
        this.context = context;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{
        public ImageView productePic, productePic2;
        public TextView name, name2, product_price, product_price2, status;
        public CardView layout;
        public Button btn_return;
        public OrderViewHolder(View itemView, int viewType){
            super(itemView);
            productePic = (ImageView) itemView.findViewById(R.id.productePic);
            productePic2 = (ImageView) itemView.findViewById(R.id.productePic2);
            name = (TextView) itemView.findViewById(R.id.name);
            name2 = (TextView) itemView.findViewById(R.id.name2);
            product_price = (TextView) itemView.findViewById(R.id.product_price);
            product_price2 = (TextView) itemView.findViewById(R.id.product_price2);
            status = itemView.findViewById(R.id.status);
            btn_return = itemView.findViewById(R.id.btn_return);
            layout = itemView.findViewById(R.id.orderTrade);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_order, parent, false);
        return new OrderViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderViewHolder tradeViewHolder = (OrderViewHolder) holder;

        Picasso.with(tradeViewHolder.productePic.getContext()).load(tradeList.get(position).getProduct_image_seller()).into(tradeViewHolder.productePic);
        Picasso.with(tradeViewHolder.productePic2.getContext()).load(tradeList.get(position).getProduct_image_user()).into(tradeViewHolder.productePic2);
        tradeViewHolder.name.setText(tradeList.get(position).getProduct_name_seller());
        tradeViewHolder.name2.setText(tradeList.get(position).getProduct_name_user());
        tradeViewHolder.product_price.setText("₱"+tradeList.get(position).getProduct_price_seller());
        tradeViewHolder.product_price2.setText("₱"+tradeList.get(position).getProduct_price_user());
        tradeViewHolder.status.setText(tradeList.get(position).getStatus());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("TradeReq").child(tradeList.get(position).getId());

        if (tradeList.get(position).getStatus().equals("Pending")){
            tradeViewHolder.btn_return.setEnabled(false);
            tradeViewHolder.btn_return.setBackgroundColor(Color.RED);
            tradeViewHolder.btn_return.setTextColor(Color.WHITE);
        }else if (tradeList.get(position).getStatus().equals("Complete")){
            tradeViewHolder.btn_return.setEnabled(true);
        }else if (tradeList.get(position).getStatus().equals("Returned")){
            tradeViewHolder.btn_return.setEnabled(false);
            tradeViewHolder.btn_return.setBackgroundColor(Color.RED);
            tradeViewHolder.btn_return.setTextColor(Color.WHITE);
        }

        FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userHelperClass = snapshot.getValue(UserHelperClass.class);

                name = userHelperClass.getName();
                Messagename = userHelperClass.getName();
                imageUri = userHelperClass.getFilepath();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("User").child(tradeList.get(position).getSellerid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    SellerName = userProfile.getName();
                    imageUri1 = userProfile.getFilepath();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tradeViewHolder.btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout bottomSheetRL =  v.findViewById(R.id.idRLBottomSheet);

                // creating a variable for our bottom sheet dialog.
                final BottomSheetDialog bottomSheetTradeDialog = new BottomSheetDialog(v.getContext(), R.style.BottomSheetDialogTheme);

                // passing a layout file for our bottom sheet dialog.
                View layout = LayoutInflater.from(v.getContext()).inflate(R.layout.return_layout, bottomSheetRL);

                // passing our layout file to our bottom sheet dialog.
                bottomSheetTradeDialog.setContentView(layout);

                // below line is to display our bottom sheet dialog.
                bottomSheetTradeDialog.show();

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button returnBtn = layout.findViewById(R.id.btn_returns);
                TextInputLayout review = layout.findViewById(R.id.returns);

                returnBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status","Returned");

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    reference.updateChildren(hashMap);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        firebaseFirestore.collection("TradeReq")
                                .whereEqualTo("id", tradeList.get(position).getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        for (QueryDocumentSnapshot document : value) {
                                            TradeReq tradeReq = document.toObject(TradeReq.class);
                                            document.getReference().update("status", "Returned");
                                        }

                                    }
                                });

                        String reviews = review.getEditText().getText().toString();
                        SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                        String date = s.format(new Date());
                        ReturnHelper returnHelper = new ReturnHelper(name, reviews, firebaseUser.getUid(), tradeList.get(position).getSellerid(),tradeList.get(position).getProduct_name_seller());
                        FirebaseDatabase.getInstance().getReference("Return").push().setValue(returnHelper);
                        FirebaseFirestore.getInstance().collection("Return").add(returnHelper);
                        bottomSheetTradeDialog.dismiss();

                        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
                        );

                        String twilioMsg =  "Your order tracking id " + tradeList.get(position).getId() + " has been returned by the customer " + "\n" + "\n"
                               + "Here the reson:" + "\n" + reviews;

                        Map<String, String> data = new HashMap<>();
                        data.put("From", "+17278554264");
                        data.put("To", "+639924318086");
                        data.put("Body", twilioMsg);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://api.twilio.com/2010-04-01/")
                                .build();
                        TwilioApi api = retrofit.create(TwilioApi.class);

                        api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) Log.d("TAG", "onResponse->success");
                                else Log.d("TAG", "onResponse->failure");
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("TAG", "onFailure");
                            }
                        });

                        ChatUserHelper chatUserHelper = new ChatUserHelper(tradeList.get(position).getSellerid(),tradeList.get(position).getUserid(),twilioMsg,date,Messagename,imageUri,"0","text");
                        FirebaseDatabase.getInstance().getReference().child("Chat").push().setValue(chatUserHelper);
                        FirebaseFirestore.getInstance().collection("Chat").add(chatUserHelper);

                        String payMessage = "Your trade request tracking id " + tradeList.get(position).getId() + " has been returned by customer";
                        NotifHelperClass notifHelperClass = new NotifHelperClass("Returned Notification", Messagename, tradeList.get(position).getUserid(), tradeList.get(position).getSellerid(), twilioMsg, "payment", "0");
                        FirebaseDatabase.getInstance().getReference().child("Notification").push().setValue(notifHelperClass);
                        firebaseFirestore.collection("Notification").add(notifHelperClass);
                    }
                });
            }
        });
    }

    interface TwilioApi {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/SMS/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );

    }

    @Override
    public int getItemCount() {
        return tradeList.size();
    }
}
