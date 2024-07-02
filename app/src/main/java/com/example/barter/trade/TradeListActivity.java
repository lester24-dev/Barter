package com.example.barter.trade;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.barter.R;
import com.example.barter.adapter.TradeFragmentAdapter;
import com.example.barter.darkmode.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class TradeListActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    int tradePen, tradeCom, orderPen, orderComs;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Trade List");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        tabLayout = findViewById(R.id.view_pager_tab);

        viewPager = findViewById(R.id.viewpager);

        // Set the adapter
        viewPager.setAdapter(new TradeFragmentAdapter(this));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator
                (tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch(position){
                    case 0: {

                        final Query orderComQuery  = firebaseFirestore.collection("TradeReq")
                                .whereEqualTo("sellerid", userID).whereEqualTo("status", "Pending");
                        orderComQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value.isEmpty()){
                                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                    badgeDrawable.setVisible(false);
                                }else{
                                    orderComs = value.size();
                                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                    badgeDrawable.setVisible(true);
                                    badgeDrawable.setNumber(value.size());
                                    Log.d("TAG", value.size() + "");
                                }
                            }
                        });
                        tab.setText("Trade Request");
                        break;
                    }
                    case 1: {
                        final Query orderComsQuery  = firebaseFirestore.collection("TradeReq")
                                .whereEqualTo("sellerid", userID).whereEqualTo("status", "Complete");
                        orderComsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value.isEmpty()){
                                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                    badgeDrawable.setVisible(false);
                                }else{
                                    orderComs = value.size();
                                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                    badgeDrawable.setVisible(true);
                                    badgeDrawable.setNumber(value.size());
                                    Log.d("TAG", value.size() + "");
                                }
                            }
                        });
                        tab.setText("Trade Complete");
                        break;
                    }
                    case 2: {
                        final Query orderComsQuery  = firebaseFirestore.collection("TradeReq")
                                .whereEqualTo("userid", userID).whereEqualTo("status", "Pending");
                        orderComsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value.isEmpty()){
                                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                    badgeDrawable.setVisible(false);
                                }else{
                                    orderComs = value.size();
                                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                    badgeDrawable.setVisible(true);
                                    badgeDrawable.setNumber(value.size());
                                    Log.d("TAG", value.size() + "");
                                }
                            }
                        });
                        tab.setText("Order Pending");
                        break;
                    }
                    case 3: {

                        final Query orderComsQuery = firebaseFirestore.collection("TradeReq")
                                .whereEqualTo("userid", userID).whereEqualTo("status", "Complete");
                        orderComsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value.isEmpty()) {
                                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                    badgeDrawable.setVisible(false);
                                } else {
                                    orderComs = value.size();
                                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                    badgeDrawable.setVisible(true);
                                    badgeDrawable.setNumber(value.size());
                                    Log.d("TAG", value.size() + "");
                                }
                            }
                        });
                    }
                        tab.setText("Order Complete");
                        break;
                        case 4: {

                            final Query orderRefsQuery  = firebaseFirestore.collection("TradeReq")
                                    .whereEqualTo("userid", userID).whereEqualTo("status", "Returned");
                            orderRefsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (value.isEmpty()){
                                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                        badgeDrawable.setVisible(false);
                                    }else{
                                        orderComs = value.size();
                                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                        badgeDrawable.setVisible(true);
                                        badgeDrawable.setNumber(value.size());
                                        Log.d("TAG", value.size() + "");
                                    }
                                }
                            });
                            tab.setText("Order Returned");
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