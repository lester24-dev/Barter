package com.example.barter.notification;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.adapter.CustomAdapter;
import com.example.barter.adapter.CustomsAdapter;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.Notif;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BlackNotifFragment extends Fragment {
View root;
    FirebaseFirestore firebaseFirestore;
    ListView listView;
    private ArrayList<Notif> notifArrayList;
    private FirebaseUser firebaseUser;
    CustomsAdapter notifAdapter;

    public BlackNotifFragment() {
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
        root = inflater.inflate(R.layout.fragment_black_notif, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        listView = root.findViewById(R.id.recycler_view);
        notifArrayList = new ArrayList<>();

        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("Notif-" + firebaseUser.getUid());

        Query query = databaseReference;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Notif notif = dataSnapshot.getValue(Notif.class);
                        notifArrayList.add(notif);
                    }
                    notifAdapter = new CustomsAdapter(getContext(), notifArrayList);
                    listView.setAdapter(notifAdapter);
                }else{
                    Toast.makeText(getContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return  root;
    }
}