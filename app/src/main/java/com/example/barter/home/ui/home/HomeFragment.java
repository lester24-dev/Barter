package com.example.barter.home.ui.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.barter.R;
import com.example.barter.adapter.HomeFragmentAdapter;
import com.example.barter.adapter.ProductAdapter;
import com.example.barter.adapter.TradeFragmentAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Product;
import com.example.barter.database.User;
import com.example.barter.database.UserHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

public class HomeFragment extends Fragment {

   View root;
    private TextInputLayout product_name, product_des, product_category, product_size, product_color, product_year, product_brand, product_price;
    private AutoCompleteTextView category, color;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    int tradePen, tradeCom, orderPen, orderComs;
    String userID;


    String[] categories = {
            "Men Clothes", "Women Clothes", "Men Accessories", "Women Accessories", "Sport Accessories", "Gadget Accessories",
            "Appliances", "Unisex Clothes", "Unisex Accessories"
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.ThemeDark);
        } else {
            getActivity().setTheme(R.style.ThemeLight);
        }
        root = inflater.inflate(R.layout.fragment_home, container,false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        tabLayout = root.findViewById(R.id.view_pager_tab);

        viewPager = root.findViewById(R.id.viewpager);

        // Set the adapter
        viewPager.setAdapter(new HomeFragmentAdapter(getActivity()));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position){
                    case 0: {
                        tab.setText("Product");
                        //tab.setIcon(R.drawable.ic_baseline_pending_24);
                        break;
                    }
                    case 1: {
                        tab.setText("Categories");
                        //tab.setIcon(R.drawable.ic_baseline_settings_24);
                        break;
                    }

//                    case 2: {
//                        tab.setText("Match Making");
//                        tab.setIcon(R.drawable.ic_baseline_settings_24);
//                        break;
//                    }
                }

            }
        });
        tabLayoutMediator.attach();


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}