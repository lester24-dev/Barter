package com.example.barter.notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.adapter.CustomAdapter;
import com.example.barter.adapter.NotifsAdapter;
import com.example.barter.adapter.TradeFragmentAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Notif;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    SharedPref sharedPref;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Notification");

        tabLayout = findViewById(R.id.view_pager_tab);

        viewPager = findViewById(R.id.viewpager);

        // Set the adapter
        viewPager.setAdapter(new NotifsAdapter(this));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator
                (tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                        switch(position){
                            case 0: {

//                                final com.google.firebase.firestore.Query orderComQuery  = firebaseFirestore.collection("Notification")
//                                        .whereEqualTo("sellerid", userID).whereEqualTo("status", "Pending");
//                                orderComQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                        if (value.isEmpty()){
//                                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
//                                            badgeDrawable.setVisible(false);
//                                        }else{
//                                            orderComs = value.size();
//                                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
//                                            badgeDrawable.setVisible(true);
//                                            badgeDrawable.setNumber(value.size());
//                                            Log.d("TAG", value.size() + "");
//                                        }
//                                    }
//                                });
                                tab.setText("Trade Notification");
                                break;
                            }
                            case 1: {
//                                final com.google.firebase.firestore.Query orderComsQuery  = firebaseFirestore.collection("TradeReq")
//                                        .whereEqualTo("sellerid", userID).whereEqualTo("status", "Complete");
//                                orderComsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                        if (value.isEmpty()){
//                                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
//                                            badgeDrawable.setVisible(false);
//                                        }else{
//                                            orderComs = value.size();
//                                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
//                                            badgeDrawable.setVisible(true);
//                                            badgeDrawable.setNumber(value.size());
//                                            Log.d("TAG", value.size() + "");
//                                        }
//                                    }
//                                });
                                tab.setText("Report Notification");
                                break;
                            }

                        }
                    }
                });
        tabLayoutMediator.attach();
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