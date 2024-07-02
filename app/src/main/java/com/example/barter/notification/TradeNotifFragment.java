package com.example.barter.notification;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.adapter.CustomAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Notif;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TradeNotifFragment extends Fragment {

View root;
private FirebaseUser firebaseUser;
FirebaseFirestore firebaseFirestore;
ListView listView;
private ArrayList<Notif> notifArrayList;
CustomAdapter notifAdapter;

    public TradeNotifFragment() {
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
        root = inflater.inflate(R.layout.fragment_trade_notif, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

       listView = root.findViewById(R.id.recycler_view);
       notifArrayList = new ArrayList<>();

        firebaseFirestore.collection("Notification").whereEqualTo("reciever", firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty()){

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot documentSnapshot : list){
                        Notif notif = documentSnapshot.toObject(Notif.class);
                        notifArrayList.add(notif);
                    }
                    notifAdapter = new CustomAdapter(getContext(), notifArrayList);
                    listView.setAdapter(notifAdapter);
                }else {
                    // if the snapshot is empty we are displaying a toast message.
                    Toast.makeText(getContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return  root;

    }
}