package com.example.barter.trade;

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
import com.example.barter.adapter.TradeReqAdapter;
import com.example.barter.adapter.TradeReqComAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.AddTrade;
import com.example.barter.database.TradeReq;
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


public class TradeReqsFragment extends Fragment {
    RecyclerView recyclerView;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<TradeReq> tradeReqArrayList;
    private TradeReqAdapter tradeReqAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userID;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    View root;
    ImageView no_item;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static TradeReqsFragment newInstance(String param1, String param2) {
        TradeReqsFragment fragment = new TradeReqsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TradeReqsFragment() {
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
        root = inflater.inflate(R.layout.fragment_trade_reqs, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference  = firebaseDatabase.getInstance().getReference("User");
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mActivity = getActivity();
        mContext = getContext();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Product Loading");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.show();

        no_item = root.findViewById(R.id.no_item);

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1, GridLayoutManager.VERTICAL, false));
        tradeReqArrayList = new ArrayList<>();

        firebaseFirestore.collection("TradeReq")
                .whereEqualTo("sellerid", userID).whereEqualTo("status", "Pending").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot document : value) {
                            TradeReq tradeReq = document.toObject(TradeReq.class);
                            tradeReqArrayList.add(tradeReq);
                        }
                        if (value.isEmpty()){
                            no_item.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }else{
                            no_item.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            tradeReqAdapter = new TradeReqAdapter(mContext,mActivity,  tradeReqArrayList);
                            recyclerView.setAdapter(tradeReqAdapter);
                            tradeReqAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    }
                });



        return root;
    }
}