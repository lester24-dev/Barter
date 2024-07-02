package com.example.barter.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.R;
import com.example.barter.database.AddTrade;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.NotifHelperClass;
import com.example.barter.database.TradeReq;
import com.example.barter.database.UserHelperClass;
import com.example.barter.database.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
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


public class TradeReqAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<TradeReq> tradeList;
    private Context context;
    private Activity mActivity;
    DatabaseReference reference;
    FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    public static final String ACCOUNT_SID = "ACCOUNT_SID";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String ACCOUNT_SID2 = "ACCOUNT_SID2";
    public static final String AUTH_TOKEN2 = "AUTH_TOKEN2";
    String Messagename, imageUri, message, SellerName, imageUri1, customerName, customerAddress;
    private FirebaseDatabase firebaseDatabase;

    public TradeReqAdapter(Context context, Activity mActivity, ArrayList<TradeReq> tradeList){
        this.context = context;
        this.mActivity = mActivity;
        this.tradeList = tradeList;
    }

    public class TradeViewHolder extends RecyclerView.ViewHolder{
        public ImageView productePic, productePic2;
        public TextView name, name2, product_price, product_price2, status, orderType;
        public CardView layout;
        Button btn_check, btn_denied;
        public TradeViewHolder(View itemView, int viewType){
            super(itemView);
            productePic = (ImageView) itemView.findViewById(R.id.productePic);
            productePic2 = (ImageView) itemView.findViewById(R.id.productePic2);
            name = (TextView) itemView.findViewById(R.id.name);
            name2 = (TextView) itemView.findViewById(R.id.name2);
            product_price = (TextView) itemView.findViewById(R.id.product_price);
            product_price2 = (TextView) itemView.findViewById(R.id.product_price2);
            status = itemView.findViewById(R.id.status);
            orderType = itemView.findViewById(R.id.orderType);
            btn_check = itemView.findViewById(R.id.btn_check);
            btn_denied = itemView.findViewById(R.id.btn_denied);
            layout = itemView.findViewById(R.id.tradelist);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_request_pending, parent, false);
        return new TradeViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        firebaseDatabase = FirebaseDatabase.getInstance();

        TradeViewHolder tradeViewHolder = (TradeViewHolder) holder;

        Picasso.with(tradeViewHolder.productePic.getContext()).load(tradeList.get(position).getProduct_image_seller()).into(tradeViewHolder.productePic);
        Picasso.with(tradeViewHolder.productePic2.getContext()).load(tradeList.get(position).getProduct_image_user()).into(tradeViewHolder.productePic2);
        tradeViewHolder.name.setText(tradeList.get(position).getProduct_name_seller());
        tradeViewHolder.name2.setText(tradeList.get(position).getProduct_name_user());
        tradeViewHolder.product_price.setText("₱"+tradeList.get(position).getProduct_price_seller());
        tradeViewHolder.product_price2.setText("₱"+tradeList.get(position).getProduct_price_user());

        if (tradeList.get(position).getOrderType().equals("Meet in Person")){
            tradeViewHolder.orderType.setText("Meet in Person");
        }
        else if (tradeList.get(position).getOrderType().equals("Delivery (The company will provide delivery rider in order to recieve your item)")){
            tradeViewHolder.orderType.setText("Delivery");
        }

        firebaseDatabase.getReference("User").child(tradeList.get(position).getSellerid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    Messagename = userProfile.getName();
                    imageUri = userProfile.getFilepath();
                    //   textView.setText(name);
                    //   Picasso.with(getActivity()).load(imageUri).into(imageView);
                    //String orderTypeMethod = orderType.getEditText().getText().toString().trim();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("User").child(tradeList.get(position).getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    Messagename = userProfile.getName();
                    imageUri = userProfile.getFilepath();
                }
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

        tradeViewHolder.status.setText(tradeList.get(position).getStatus());

        tradeViewHolder.btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading");
                progressDialog.setProgress(10);
                progressDialog.setMax(100);
                progressDialog.show();

                firebaseFirestore = FirebaseFirestore.getInstance();

                reference = FirebaseDatabase.getInstance().getReference("TradeReq").child(tradeList.get(position).getId());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status","Complete");

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
                                   document.getReference().update("status", "Complete");

                                    String uid = tradeList.get(position).getUserid();
                                    firebaseDatabase.getReference("User").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                Users users = snapshot.getValue(Users.class);
                                                customerName = users.getName();
                                                customerAddress = users.getAddress();
                                                Log.d("TAG",  "" + customerName + customerAddress);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    String base64EncodedCredentialss = "Basic " + Base64.encodeToString(
                                            (ACCOUNT_SID2 + ":" + AUTH_TOKEN2).getBytes(), Base64.NO_WRAP
                                    );

                                    Map<String, String> data = new HashMap<>();
                                    data.put("From", "+17278554264");
                                    data.put("To", "+639924318086");
                                    data.put("Body", "Hello rider you will deliver this parcel has tracking id " + tradeList.get(position).getId() + " to " + tradeList.get(position).getUser_name() + " the address is " + tradeList.get(position).getUser_address() + " thank you");

                                    Retrofit retrofits = new Retrofit.Builder()
                                            .baseUrl("https://api.twilio.com/2010-04-01/")
                                            .build();
                                    TwilioApi2 api2 = retrofits.create(TwilioApi2.class);

                                    api2.sendMessage(ACCOUNT_SID2, base64EncodedCredentialss, data).enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()) Log.d("TAG", "onResponse->success2");
                                            else Log.d("TAG", "onResponse->failure2");
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Log.d("TAG", "onFailure2");
                                        }
                                    });

                                    Map<String, String> data1 = new HashMap<>();
                                    data1.put("From", "+17278554264");
                                    data1.put("To", "+639924318086");
                                    data1.put("Body", "Your order tracking id " + tradeList.get(position).getId() + " has been approved by the seller, please wait for other information from the seller");

                                    api2.sendMessage(ACCOUNT_SID2, base64EncodedCredentialss, data1).enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()) Log.d("TAG", "onResponse->success1");
                                            else Log.d("TAG", "onResponse->failure1");
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Log.d("TAG", "onFailure1");
                                        }
                                    });

                                    progressDialog.dismiss();
                                    notifyDataSetChanged();
                                }

                            }
                        });

                String payMessage = "Your trade request tracking id " + tradeList.get(position).getId() + " has been successful approved";
                NotifHelperClass notifHelperClass = new NotifHelperClass("Trade Request Notification", Messagename, tradeList.get(position).getUserid(), tradeList.get(position).getSellerid(), payMessage, "payment", "0");
                FirebaseDatabase.getInstance().getReference().child("Notification").push().setValue(notifHelperClass);
                firebaseFirestore.collection("Notification").add(notifHelperClass);

                SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                String date = s.format(new Date());

                message = "Your order trading number " + tradeList.get(position).getId() + " has been approved. Below the order \n\n" +
                        "Seller Item \n" + "Product Name: " + tradeList.get(position).getProduct_name_seller() + "\n" + "Product Price: " + tradeList.get(position).getProduct_price_seller() + "\n"
                        + "\n" + "Customer Item \n" + "Product Name: " + tradeList.get(position).getProduct_name_seller() + "\n" + "Product Price: " + tradeList.get(position).getProduct_price_user() + "\n"
                        + "Order Type: " + tradeList.get(position).getOrderType();


                ChatUserHelper chatUserHelper = new ChatUserHelper(tradeList.get(position).getSellerid(),tradeList.get(position).getUserid(),message,date,Messagename,imageUri,"0","text");
                firebaseDatabase.getReference().child("Chat").push().setValue(chatUserHelper);
                firebaseFirestore.collection("Chat").add(chatUserHelper);

                final DatabaseReference chatList = FirebaseDatabase.getInstance().getReference("Chatlist").child(tradeList.get(position).getUserid());
                chatList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatList.child(tradeList.get(position).getUserid()).child("id").setValue(tradeList.get(position).getSellerid());
                            chatList.child(tradeList.get(position).getUserid()).child("name").setValue(SellerName);
                            chatList.child(tradeList.get(position).getUserid()).child("filepath").setValue(imageUri);
                            // firebaseFirestore.collection("Chatlist").document(sellerid).set(new ChatUser(sellerid,Messagename,imageUri));
                        }else {
                            chatList.child(tradeList.get(position).getUserid()).child("id").setValue(tradeList.get(position).getSellerid());
                            chatList.child(tradeList.get(position).getUserid()).child("name").setValue(SellerName);
                            chatList.child(tradeList.get(position).getUserid()).child("filepath").setValue(imageUri);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final DatabaseReference chatList1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(tradeList.get(position).getSellerid());
                chatList1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatList1.child(tradeList.get(position).getSellerid()).child("id").setValue(tradeList.get(position).getUserid());
                            chatList1.child(tradeList.get(position).getSellerid()).child("name").setValue(Messagename);
                            chatList1.child(tradeList.get(position).getSellerid()).child("filepath").setValue(imageUri1);
                            //firebaseFirestore.collection("Chatlist").document(sellerid).set(new ChatUser(sellerid,Messagename,imageUri));
                        }else {
                            chatList1.child(tradeList.get(position).getSellerid()).child("id").setValue(tradeList.get(position).getUserid());
                            chatList1.child(tradeList.get(position).getSellerid()).child("name").setValue(Messagename);
                            chatList1.child(tradeList.get(position).getSellerid()).child("filepath").setValue(imageUri1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        tradeViewHolder.btn_denied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                        (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
                );

                Map<String, String> data = new HashMap<>();
                data.put("From", "+12676519456");
                data.put("To", "+639074075420");
                data.put("Body", "Your order tracking id " + tradeList.get(position).getId() + " has been decline by the seller, please wait message from the seller");

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

                String payMessage = "Your trade request tracking id " + tradeList.get(position).getId() + " has been disapproved";
                NotifHelperClass notifHelperClass = new NotifHelperClass("Decline Notification", Messagename, tradeList.get(position).getUserid(), tradeList.get(position).getSellerid(), payMessage, "payment", "0");
                FirebaseDatabase.getInstance().getReference().child("Notification").push().setValue(notifHelperClass);
                FirebaseFirestore.getInstance().collection("Notification").add(notifHelperClass);

                SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                String date = s.format(new Date());

                message = "Your order trading number " + tradeList.get(position).getId() + " has been disapproved. Below the order \n\n" +
                        "Seller Item \n" + "Product Name: " + tradeList.get(position).getProduct_name_seller() + "\n" + "Product Price: " + tradeList.get(position).getProduct_price_seller() + "\n"
                        + "\n" + "Customer Item \n" + "Product Name: " + tradeList.get(position).getProduct_name_seller() + "\n" + "Product Price: " + tradeList.get(position).getProduct_price_user() + "\n"
                        + "Order Type: " + tradeList.get(position).getOrderType();


                ChatUserHelper chatUserHelper = new ChatUserHelper(tradeList.get(position).getSellerid(),tradeList.get(position).getUserid(),message,date,Messagename,imageUri,"0","text");
                firebaseDatabase.getReference().child("Chat").push().setValue(chatUserHelper);
                FirebaseFirestore.getInstance().collection("Chat").add(chatUserHelper);

                final DatabaseReference chatList = FirebaseDatabase.getInstance().getReference("Chatlist").child(tradeList.get(position).getUserid());
                chatList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatList.child(tradeList.get(position).getUserid()).child("id").setValue(tradeList.get(position).getSellerid());
                            chatList.child(tradeList.get(position).getUserid()).child("name").setValue(SellerName);
                            chatList.child(tradeList.get(position).getUserid()).child("filepath").setValue(imageUri);
                            // firebaseFirestore.collection("Chatlist").document(sellerid).set(new ChatUser(sellerid,Messagename,imageUri));
                        }else {
                            chatList.child(tradeList.get(position).getUserid()).child("id").setValue(tradeList.get(position).getSellerid());
                            chatList.child(tradeList.get(position).getUserid()).child("name").setValue(SellerName);
                            chatList.child(tradeList.get(position).getUserid()).child("filepath").setValue(imageUri);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final DatabaseReference chatList1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(tradeList.get(position).getSellerid());
                chatList1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatList1.child(tradeList.get(position).getSellerid()).child("id").setValue(tradeList.get(position).getUserid());
                            chatList1.child(tradeList.get(position).getSellerid()).child("name").setValue(Messagename);
                            chatList1.child(tradeList.get(position).getSellerid()).child("filepath").setValue(imageUri1);
                            //firebaseFirestore.collection("Chatlist").document(sellerid).set(new ChatUser(sellerid,Messagename,imageUri));
                        }else {
                            chatList1.child(tradeList.get(position).getSellerid()).child("id").setValue(tradeList.get(position).getUserid());
                            chatList1.child(tradeList.get(position).getSellerid()).child("name").setValue(Messagename);
                            chatList1.child(tradeList.get(position).getSellerid()).child("filepath").setValue(imageUri1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return tradeList.size();
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

    interface TwilioApi2 {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/SMS/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );

    }
}
