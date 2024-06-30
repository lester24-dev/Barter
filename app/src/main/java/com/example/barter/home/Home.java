package com.example.barter.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.barter.R;
import com.example.barter.cart.CartFragment;
import com.example.barter.chat.ChatActivity;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.Notif;
import com.example.barter.database.User;
import com.example.barter.home.ui.gallery.GalleryFragment;
import com.example.barter.home.ui.home.HomeFragment;
import com.example.barter.home.ui.setting.SettingFragment;
import com.example.barter.home.ui.users.UsersFragment;
import com.example.barter.notification.NotificationActivity;
import com.example.barter.profile.UserProfile;
import com.example.barter.trade.TradeListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;


public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    GalleryFragment galleryFragment;
    UsersFragment usersFragment;
    CartFragment cartFragment;
    SettingFragment settingFragment;
    private FirebaseAuth firebaseAuth;
    BottomAppBar bottomAppBar;
    private FirebaseUser firebaseUser;
    DatabaseReference reference, chatRef;
    FirebaseFirestore firebaseFirestore;
    SharedPref sharedPref;
    TextView chatCountTxt, tradeCountTxt, notifCountTxt;
    int pendingSMSCount = 10;
    Toolbar toolbar;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_gallery, R.id.navigation_users, R.id.navigation_cart, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        homeFragment = new HomeFragment();
        galleryFragment = new GalleryFragment();
        usersFragment = new UsersFragment();
        settingFragment = new SettingFragment();
        cartFragment = new CartFragment();

        reference = FirebaseDatabase.getInstance().getReference("User");
        chatRef = FirebaseDatabase.getInstance().getReference("Chat");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status","online");
        reference.child(userID).updateChildren(hashMap);

        firebaseFirestore.collection("Chat").whereEqualTo("reciever",firebaseUser.getUid()).whereEqualTo("status","unseen").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (QueryDocumentSnapshot document : value){
                    ChatUserHelper chatUserHelper = document.toObject(ChatUserHelper.class);

                    firebaseFirestore.collection("User").document(chatUserHelper.getSender())
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    User user = value.toObject(User.class);
                                    notifChat(user.getName(),chatUserHelper.getMessage());
                                }
                            });

                }
            }
        });

        firebaseFirestore.collection("Notification").whereEqualTo("reciever", firebaseUser.getUid()).whereEqualTo("status", "0").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot documentSnapshot : value){
                    Notif notif = documentSnapshot.toObject(Notif.class);
                    notifTrade(notif.getMessage());
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        final MenuItem menuChat = menu.findItem(R.id.navigation_message);
        final MenuItem menuTrade = menu.findItem(R.id.navigation_trade);
        final MenuItem menuNotif = menu.findItem(R.id.navigation_notif);

        View chatView = MenuItemCompat.getActionView(menuChat);
        View tradeView = MenuItemCompat.getActionView(menuTrade);
        View notifView = MenuItemCompat.getActionView(menuNotif);

        chatCountTxt = (TextView) chatView.findViewById(R.id.chat_badge);
        tradeCountTxt = (TextView) tradeView.findViewById(R.id.notification_badge);
        notifCountTxt = notifView.findViewById(R.id.notif_badge);

        setupBadge();

        chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, ChatActivity.class));
            }
        });

        tradeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, TradeListActivity.class));
            }
        });

        notifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, NotificationActivity.class));
            }
        });

        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.navigation_message:
               // addSomething();
                startActivity(new Intent(Home.this, ChatActivity.class));
                break;

            case R.id.navigation_trade:
                startActivity(new Intent(Home.this, TradeListActivity.class));
                break;
            default:
        }
        return true;
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
        DocumentReference lucknowRef= firebaseFirestore.collection("User").document(firebaseUser.getUid());
        lucknowRef.update(hashMap);
    }

    public void onResume() {
        super.onResume();
        status("online");
    }

    public void onPause() {
        super.onPause();
        status("offline");
    }

    private void setupBadge() {

        final Query documentReference = firebaseFirestore.collection("Chat").whereEqualTo("reciever", firebaseUser.getUid())
                .whereEqualTo("status", "unseen");

        documentReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()){
                    chatCountTxt.setVisibility(View.INVISIBLE);
                }else{
                    chatCountTxt.setVisibility(View.VISIBLE);
                    chatCountTxt.setText(String.valueOf(value.size()));
                }
                Log.d("TAG", value.size() + "");
            }
        });


        final Query tradeQuery  = firebaseFirestore.collection("TradeReq")
                .whereEqualTo("userid", firebaseUser.getUid())
                .whereEqualTo("sellerid",firebaseUser.getUid());


            tradeQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.isEmpty()){
                        tradeCountTxt.setVisibility(View.INVISIBLE);
                    }else{
                        tradeCountTxt.setVisibility(View.VISIBLE);
                        tradeCountTxt.setText(String.valueOf(value.size()));
                    }
                }
            });


        final Query notifQuery  = firebaseFirestore.collection("Notification")
                .whereEqualTo("reciever", firebaseUser.getUid())
                .whereEqualTo("status","0");


        notifQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()){
                    notifCountTxt.setVisibility(View.INVISIBLE);
                }else{
                    notifCountTxt.setVisibility(View.VISIBLE);
                    notifCountTxt.setText(String.valueOf(value.size()));
                }
            }
        });

    }

    private void notifChat(String reciever, String message){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setContentTitle(reciever)
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setAutoCancel(true)
                .setContentText(message);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());

    }

    private void notifTrade(String message){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setContentTitle("New Trade Request")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setAutoCancel(true)
                .setContentText(message);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }
}