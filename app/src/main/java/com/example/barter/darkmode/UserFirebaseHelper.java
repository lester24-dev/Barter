package com.example.barter.darkmode;

import com.example.barter.database.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class UserFirebaseHelper {
    DatabaseReference db;
    Boolean saved;
    ArrayList<User> sectionDetailsArrayList = new ArrayList<>();

    public UserFirebaseHelper(DatabaseReference databaseReference){
        this.db = databaseReference;
    }

    public Boolean save(User sectionDetails) {

        if (sectionDetails == null) {
            saved = false;
        } else {
            try {
                db.push().setValue(sectionDetails);
                saved = true;

                //adapter.notifyDataSetChanged();

            } catch(DatabaseException e) {
                e.printStackTrace();
                saved = false;
            }
        }

        return saved;
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        sectionDetailsArrayList.clear();

        User sectionDetails = dataSnapshot.getValue(User.class);
        sectionDetailsArrayList.add(sectionDetails);
    }

    public ArrayList<User> retrieve() {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return sectionDetailsArrayList;
    }
}
