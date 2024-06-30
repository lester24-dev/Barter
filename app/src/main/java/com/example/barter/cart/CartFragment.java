package com.example.barter.cart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.barter.R;
import com.example.barter.adapter.AddTradeAdapter;
import com.example.barter.adapter.ProductAdapter;
import com.example.barter.adapter.TradeReqComAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.AddTrade;
import com.example.barter.database.Product;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class CartFragment extends Fragment {

 View root;
 RecyclerView recyclerView;
 private Context mContext;
 private Activity mActivity;
 private ArrayList<AddTrade> cartsList;
 private AddTradeAdapter addTradeAdapter;
 private FirebaseFirestore firebaseFirestore;
 private FirebaseUser firebaseUser;
private String userID;
private ProgressDialog progressDialog;
private FirebaseDatabase firebaseDatabase;
private DatabaseReference databaseReference;
ImageView no_item;

 public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPref sharedPref = new SharedPref(getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.ThemeDark);
        } else {
            getActivity().setTheme(R.style.ThemeLight);
        }
        root = inflater.inflate(R.layout.fragment_cart, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference  = firebaseDatabase.getInstance().getReference("User");
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mActivity = getActivity();
        mContext = getContext();

        no_item = root.findViewById(R.id.no_item);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.show();

        recyclerView = root.findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false));
        cartsList = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("AddToTrade")
                .whereEqualTo("user", userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot document : value) {
                            AddTrade addTrade = document.toObject(AddTrade.class);
                            cartsList.add(addTrade);
                        }
                        if (value.isEmpty()){
                            progressDialog.dismiss();
                            no_item.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }else{
                            no_item.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            addTradeAdapter = new AddTradeAdapter(mContext,mActivity, (ArrayList<AddTrade>) cartsList);
                            recyclerView.setAdapter(addTradeAdapter);
                            addTradeAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();

                        }
                    }
                });


        return  root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}