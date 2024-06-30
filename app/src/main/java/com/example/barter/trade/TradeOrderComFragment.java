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
import com.example.barter.adapter.TradeOrderAdapter;
import com.example.barter.adapter.TradeReqAdapter;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TradeOrderComFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TradeOrderComFragment extends Fragment {

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
    private List<TradeReq> tradeReqArrayList;
    private TradeOrderAdapter tradeOrderAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userID;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    View root;
    ImageView no_item;

    public TradeOrderComFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TradeOrderComFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TradeOrderComFragment newInstance(String param1, String param2) {
        TradeOrderComFragment fragment = new TradeOrderComFragment();
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
        root = inflater.inflate(R.layout.fragment_trade_order_com, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference  = firebaseDatabase.getInstance().getReference("User");
        userID = firebaseUser.getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        no_item = root.findViewById(R.id.no_item);

        mActivity = getActivity();
        mContext = getContext();

        recyclerView = (RecyclerView) root.findViewById(R.id.tradeOrder);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1, GridLayoutManager.VERTICAL, false));
        tradeReqArrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Product Loading");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.show();


        firebaseFirestore.collection("TradeReq")
                .whereEqualTo("userid", userID).whereEqualTo("status", "Complete").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        tradeReqArrayList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            TradeReq tradeReq = document.toObject(TradeReq.class);
                            tradeReqArrayList.add(tradeReq);
                        }

                        if (value.isEmpty()){
                            no_item.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            progressDialog.dismiss();

                        }else{
                            recyclerView.setVisibility(View.VISIBLE);
                            no_item.setVisibility(View.GONE);
                            tradeOrderAdapter = new TradeOrderAdapter(tradeReqArrayList,mContext);
                            recyclerView.setAdapter(tradeOrderAdapter);
                            tradeOrderAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();

                        }

                    }
                });

        return  root;
    }
}