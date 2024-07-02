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
import com.example.barter.adapter.TradeReqAdapter;
import com.example.barter.adapter.TradeReqComAdapter;
import com.example.barter.darkmode.SharedPref;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TradeReqComFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TradeReqComFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<TradeReq> tradeReqArrayList;
    private TradeReqComAdapter tradeReqComAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userID;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    View root;
    ImageView no_item;

    public TradeReqComFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TradeReqComFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TradeReqComFragment newInstance(String param1, String param2) {
        TradeReqComFragment fragment = new TradeReqComFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        root =  inflater.inflate(R.layout.fragment_trade_req_com, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference  = firebaseDatabase.getInstance().getReference("User");
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        no_item = root.findViewById(R.id.no_item);

        mActivity = getActivity();
        mContext = getContext();

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1, GridLayoutManager.VERTICAL, false));
        tradeReqArrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Product Loading");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.show();

        firebaseFirestore.collection("TradeReq")
                .whereEqualTo("sellerid", userID).whereEqualTo("status", "Complete").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            tradeReqComAdapter = new TradeReqComAdapter(mContext,mActivity,  tradeReqArrayList);
                            recyclerView.setAdapter(tradeReqComAdapter);
                            tradeReqComAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    }
                });

        return root;
    }
}