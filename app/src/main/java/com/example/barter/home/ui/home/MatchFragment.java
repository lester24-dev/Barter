package com.example.barter.home.ui.home;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Match;
import com.example.barter.database.Product;
import com.example.barter.home.Home;
import com.example.barter.login.LoginActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;


public class MatchFragment extends Fragment {
   View root;
    private TextInputLayout product_year, product_category,product_price1,product_price2,regAdd;
    private AutoCompleteTextView category,reg_address;
    private Button match;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference fieldDB;
    private ProgressDialog progressDialog;


    String[] categories = {
            "Men Clothes", "Women Clothes", "Men Accessories And Shoes", "Women Accessories And Shoes", "Sport Accessories", "Sport Clothes And Shoes", "Gadget Accessories",
            "Appliances", "Unisex Clothes", "Unisex Accessories And Shoes", "Rentals", "Furniture", "Baby", "Auto Parts",
            "Books, Movies & Music", "Healthy and Beauty", "Patio and Garden", "Pet Supplies", "Toy and Games", "Vehicles",
            "Music and Intruments", "Miscellaneous", "Kids Fashion and Accessories", "Foods"
    };


    public MatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.ThemeDark);
        } else {
            getActivity().setTheme(R.style.ThemeLight);
        }
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_match, container, false);

        category = root.findViewById(R.id.productCats);
        category.setAdapter(new ArrayAdapter<String>(root.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories));
        product_category = root.findViewById(R.id.productCat);
        product_year = root.findViewById(R.id.product_year);
        product_price1 = root.findViewById(R.id.product_price1);
        product_price2 = root.findViewById(R.id.product_price2);
        match = root.findViewById(R.id.match);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fieldDB = FirebaseDatabase.getInstance().getReference("Match");


        fieldDB.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Match product = snapshot.getValue(Match.class);
                    //category.setText(product.getProduct_category());
                    product_year.getEditText().setText(product.getProduct_year());
                    product_price1.getEditText().setText(product.getProduct_price1());
                    product_price2.getEditText().setText(product.getProduct_price2());

                }else{
                    product_category.getEditText().setText("Men Accessories");
                    product_year.getEditText().setText("2022");
                    product_price1.getEditText().setText("100.00");
                    product_price2.getEditText().setText("500.00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Match Loading");
                progressDialog.setProgress(10);
                progressDialog.setMax(200);
                progressDialog.show();

                NumberFormat formatter = new DecimalFormat("#,###.##");

                float prices1 = Float.parseFloat(product_price1.getEditText().getText().toString());
                float prices2 = Float.parseFloat(product_price2.getEditText().getText().toString());


                String category = product_category.getEditText().getText().toString();
                String year = product_year.getEditText().getText().toString();
                String price1 = formatter.format(prices1);
                String price2 = formatter.format(prices2);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("product_category",category);
                hashMap.put("product_year",year);
                hashMap.put("product_price1",price1);
                hashMap.put("product_price2",price2);

                final DatabaseReference matchDB = firebaseDatabase.getReference("Match").child(firebaseUser.getUid());

            matchDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        matchDB.child("product_category").setValue(category);
                        matchDB.child("product_year").setValue(year);
                        matchDB.child("product_price1").setValue("100.00");
                        matchDB.child("product_price2").setValue("500.00");
                        progressDialog.dismiss();
                        startActivity(new Intent(getContext(), Home.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();

                    }else {
                        matchDB.updateChildren(hashMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(getContext(), Home.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            }
        });

        return  root;
    }

}