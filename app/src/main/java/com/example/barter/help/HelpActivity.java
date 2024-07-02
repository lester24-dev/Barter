package com.example.barter.help;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.ChatUserHelper;
import com.example.barter.database.UserHelperClass;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HelpActivity extends AppCompatActivity {
TextInputLayout help;
SharedPref sharedPref;
Button btn_helps;
String Messagename, imageUri, id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        help = findViewById(R.id.help);
        btn_helps = findViewById(R.id.btn_helps);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        id = firebaseUser.getUid();

        FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);

                if (userProfile != null){
                    Messagename = userProfile.getName();
                     imageUri = userProfile.getFilepath();
                    //   textView.setText(name);
                    //   Picasso.with(getActivity()).load(imageUri).into(imageView);
                    //String orderTypeMethod = orderType.getEditText().getText().toString().trim();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_helps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = help.getEditText().getText().toString();

                SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                String date = s.format(new Date());

                Map<String, String> data = new HashMap<>();
                data.put("sender", firebaseUser.getUid());
                data.put("reciever", "kajshdkjasd3213j12jh3Tkj2hkkashd");
                data.put("reciever_name", "Admin");
                data.put("sender_name",Messagename);
                data.put("sender_img",imageUri);
                data.put("message", message);
                data.put("timestamp", date);

                FirebaseDatabase.getInstance().getReference().child("AdminChat").push().setValue(data);
                FirebaseFirestore.getInstance().collection("AdminChat").add(data);

            }
        });
    }
}