package com.example.barter.payment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.adapter.ProductAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.ChatUser;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.NotifHelperClass;
import com.example.barter.database.TradeReq;
import com.example.barter.database.UserHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

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


public class PaymentActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<TradeReq> tradeReqArrayList;
    private ProductAdapter productAdapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    SharedPref sharedPref;
    TextView name, name2, product_price, product_price2;
    Button add_trade, add_paypal;
    CircleImageView productePic,productePic2;
    String userid, product_name_user, product_price_user, sellerid, product_name_seller, product_price_seller, usernames, useraddress;
    Uri product_image_user, product_image_seller;
    String Messagename, Messagename1, message, message1, imageUri, imageUri1;
    public static final int PAYPAL_REQUEST_CODE = 7171;
    TextInputLayout orderType;
    AutoCompleteTextView orderTypes;

    String [] Ordertype = { "Meet Up", "Delivery" };

    public static PayPalConfiguration payPalConfiguration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYMENT_CLIENT_ID);

    // public static final String ACCOUNT_SID = "ACCOUNT_SID";
    // public static final String AUTH_TOKEN = "AUTH_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Payment");

        add_trade = findViewById(R.id.add_trade);
        add_paypal = findViewById(R.id.add_paypal);

        name = findViewById(R.id.name);
        name2 = findViewById(R.id.name2);
        productePic = findViewById(R.id.productePic);
        productePic2 = findViewById(R.id.productePic2);
        product_price = findViewById(R.id.product_price);
        product_price2 = findViewById(R.id.product_price2);
        orderType = findViewById(R.id.orderType);
        orderTypes = findViewById(R.id.orderTypes);

        orderTypes.setAdapter(new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Ordertype));

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userid = getIntent().getStringExtra("userid");
        product_name_user = getIntent().getStringExtra("product_name_user");
        product_price_user = getIntent().getStringExtra("product_price_user");
        product_image_user = Uri.parse(getIntent().getStringExtra("product_image_user"));

        sellerid = getIntent().getStringExtra("sellerid");
        product_name_seller = getIntent().getStringExtra("product_name_seller");
        usernames = getIntent().getStringExtra("user_name");
        useraddress = getIntent().getStringExtra("user_address");
        product_price_seller = getIntent().getStringExtra("product_price_seller");
        product_image_seller = Uri.parse(getIntent().getStringExtra("product_image_seller"));

        name.setText(product_name_user);
        product_price.setText(product_price_user);
        Picasso.with(getApplicationContext()).load(product_image_user).into(productePic);

        name2.setText(product_name_seller);
        product_price2.setText(product_price_seller);
        Picasso.with(getApplicationContext()).load(product_image_seller).into(productePic2);

        firebaseDatabase.getReference("User").child(sellerid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    Messagename = userProfile.getName();
                    imageUri = userProfile.getFilepath();
                    //   textView.setText(name);
                    //   Picasso.with(getActivity()).load(imageUri).into(imageView);
                    String orderTypeMethod = orderType.getEditText().getText().toString().trim();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference("User").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    Messagename1 = userProfile.getName();
                    imageUri1 = userProfile.getFilepath();
                    //   textView.setText(name);
                    //   Picasso.with(getActivity()).load(imageUri).into(imageView);
//                    message = "You suggest to the exchange on " + Messagename1 +
//                            " Discuss the details in chat";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add_trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                String date = s.format(new Date());

                Random numRandom = new Random();
                int id = numRandom.nextInt(999999999);
                String tracking_id = String.valueOf(id);

                String orderTypeMethod = orderType.getEditText().getText().toString();

                message = "Trader \n" + "Product Name: " + product_name_seller + "\n" + "Product Price: " + product_price_seller + "\n"
                        + "\n" + "Your \n" + "Product Name: " + product_name_user + "\n" + "Product Price: " + product_price_user + "\n"
                        + "Order Type: " + orderTypeMethod;

                TradeReq tradeReq = new TradeReq("BR" + tracking_id,userid,product_name_user,product_price_user,product_image_user.toString(),sellerid,
                        product_name_seller,product_price_seller,product_image_seller.toString(),"Pending",date,"Paypal", "Meet Up", useraddress, usernames);

                firebaseDatabase.getReference().child("TradeReq").child("BR"+tracking_id).setValue(tradeReq);
                firebaseFirestore.collection("TradeReq").add(tradeReq);

                String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                        (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
                );

                Map<String, String> data = new HashMap<>();
                data.put("From", "+12676519456");
                data.put("To", "+639074075420");
                data.put("Body", "Seller Item \n" + "Product Name: " + product_name_seller + "\n" + "Product Price: " + product_price_seller + "\n"
                + "\n" + "Customer Item \n" + "Product Name: " + product_name_user + "\n" + "Product Price: " + product_price_user + "\n"
                + "Order Type: " + orderTypeMethod);

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

                ChatUserHelper chatUserHelper = new ChatUserHelper(sellerid,userid,message,date,Messagename,imageUri,"0", "text");
                firebaseDatabase.getReference().child("Chat").push().setValue(chatUserHelper);
                firebaseFirestore.collection("Chat").add(chatUserHelper);

                final DatabaseReference chatList = FirebaseDatabase.getInstance().getReference("Chatlist").child(userid);
                chatList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatList.child(sellerid).child("id").setValue(sellerid);
                            chatList.child(sellerid).child("name").setValue(Messagename);
                            chatList.child(sellerid).child("filepath").setValue(imageUri);
                            // firebaseFirestore.collection("Chatlist").document(sellerid).set(new ChatUser(sellerid,Messagename,imageUri));
                        }else {
                            chatList.child(sellerid).child("id").setValue(sellerid);
                            chatList.child(sellerid).child("name").setValue(Messagename);
                            chatList.child(sellerid).child("filepath").setValue(imageUri);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final DatabaseReference chatList1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(sellerid);
                chatList1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatList1.child(userid).child("id").setValue(userid);
                            chatList1.child(userid).child("name").setValue(Messagename1);
                            chatList1.child(userid).child("filepath").setValue(imageUri1);
                            //firebaseFirestore.collection("Chatlist").document(sellerid).set(new ChatUser(sellerid,Messagename,imageUri));
                        }else {
                            chatList1.child(userid).child("id").setValue(userid);
                            chatList1.child(userid).child("name").setValue(Messagename1);
                            chatList1.child(userid).child("filepath").setValue(imageUri1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                String payMessage = "New trade request tracking id " + tracking_id + " from " + Messagename;
                NotifHelperClass notifHelperClass = new NotifHelperClass("Payment Notification", Messagename, userid, sellerid, payMessage, "payment", "0");
                firebaseDatabase.getReference().child("Notification").push().setValue(notifHelperClass);
                firebaseFirestore.collection("Notification").add(notifHelperClass);

                finish();
                startActivity(new Intent(getApplicationContext(), SuccessActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        add_paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                String date = s.format(new Date());

                Random numRandom = new Random();
                int id = numRandom.nextInt(999999999);
                String tracking_id = String.valueOf(id);
                String orderTypeMethod = orderType.getEditText().getText().toString();

                message = "Trader \n" + "Product Name: " + product_name_seller + "\n" + "Product Price: " + product_price_seller + "\n"
                        + "\n" + "Your \n" + "Product Name: " + product_name_user + "\n" + "Product Price: " + product_price_user + "\n \n"
                        + "Order Type: " + orderTypeMethod;

                TradeReq tradeReq = new TradeReq("BR" + tracking_id,userid,product_name_user,product_price_user,product_image_user.toString(),sellerid,
                        product_name_seller,product_price_seller,product_image_seller.toString(),"Pending",date,"Paypal", "Delivery", useraddress, usernames);

                firebaseDatabase.getReference().child("TradeReq").child("BR"+tracking_id).setValue(tradeReq);
                firebaseFirestore.collection("TradeReq").add(tradeReq);

                String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                        (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
                );

                Map<String, String> data = new HashMap<>();
                data.put("From", "+12676519456");
                data.put("To", "+639074075420");
                data.put("Body", "Seller Item \n" + "Product Name: " + product_name_seller + "\n" + "Product Price: " + product_price_seller + "\n"
                        + "\n" + "Customer Item \n" + "Product Name: " + product_name_user + "\n" + "Product Price: " + product_price_user + "\n"
                        + "Order Type: " + orderTypeMethod);

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

                ChatUserHelper chatUserHelper = new ChatUserHelper(sellerid,userid,message,date,Messagename,imageUri, "0", "text");
                firebaseDatabase.getReference().child("Chat").push().setValue(chatUserHelper);
                firebaseFirestore.collection("Chat").add(chatUserHelper);

                final DatabaseReference chatList = FirebaseDatabase.getInstance().getReference("Chatlist").child(userid);
                chatList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatList.child(sellerid).child("id").setValue(sellerid);
                            chatList.child(sellerid).child("name").setValue(Messagename);
                            chatList.child(sellerid).child("filepath").setValue(imageUri);
                            // firebaseFirestore.collection("Chatlist").document(sellerid).set(new ChatUser(sellerid,Messagename,imageUri));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final DatabaseReference chatList1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(sellerid);
                chatList1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatList1.child(userid).child("id").setValue(userid);
                            chatList1.child(userid).child("name").setValue(Messagename1);
                            chatList1.child(userid).child("filepath").setValue(imageUri1);
                            //firebaseFirestore.collection("Chatlist").document(sellerid).set(new ChatUser(sellerid,Messagename,imageUri));
                        }else {
                            chatList1.child(userid).child("id").setValue(userid);
                            chatList1.child(userid).child("name").setValue(Messagename1);
                            chatList1.child(userid).child("filepath").setValue(imageUri1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                String payMessage = "New trade request tracking id " + tracking_id + " from " + Messagename;
                NotifHelperClass notifHelperClass = new NotifHelperClass("Payment Notification", Messagename, userid, sellerid, payMessage, "payment", "0");
                firebaseDatabase.getReference().child("Notification").push().setValue(notifHelperClass);
                firebaseFirestore.collection("Notification").add(notifHelperClass);

                finish();
                startActivity(new Intent(getApplicationContext(), SuccessActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

    interface TwilioApi {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/SMS/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );

    }

    private void proceedPayment(){
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(product_price_seller)), "USD",
                    "Exchange Payment for " + Messagename , PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getApplicationContext(), com.paypal.android.sdk.payments.PaymentActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                PaymentConfirmation configuration = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (configuration != null) {
                    try {
                        String paymentDetails = configuration.toJSONObject().toString(4);

                        startActivity(new Intent(getApplicationContext(), SuccessActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }

            }
            else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "cancel", Toast.LENGTH_LONG);
        } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "invalid", Toast.LENGTH_LONG);
    }
}