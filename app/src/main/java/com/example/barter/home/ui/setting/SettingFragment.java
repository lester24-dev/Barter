package com.example.barter.home.ui.setting;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.database.UserHelperClass;
import com.example.barter.help.HelpActivity;
import com.example.barter.home.Home;
import com.example.barter.login.LoginActivity;
import com.example.barter.profile.UserProfile;
import com.example.barter.safetytip.SafetyTipActivity;
import com.example.barter.termandcon.TermAConActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingFragment extends Fragment {

    TextView profileName, profileEmail;
    ImageView imgprofile;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStore;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private CardView cardView, terms, safety, help;
    Switch aSwitch;
    View root;
    SharedPref sharedPref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedPref = new SharedPref(getActivity());
        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.ThemeDark);
        } else {
            getActivity().setTheme(R.style.ThemeLight);
        }
        root = inflater.inflate(R.layout.fragment_setting, container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStore = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference  = firebaseDatabase.getReference("User");
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseStore = FirebaseStorage.getInstance();

        profileName = (TextView) root.findViewById(R.id.profileName);
        profileEmail = (TextView) root.findViewById(R.id.profileEmail);
        imgprofile = (ImageView) root.findViewById(R.id.profileImg);
        aSwitch = (Switch) root.findViewById(R.id.switchs);
        cardView = root.findViewById(R.id.signOut);
//        terms = root.findViewById(R.id.terms);
        //safety = root.findViewById(R.id.safety);
        help = root.findViewById(R.id.help);
        Query query = databaseReference.child(userID);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userProfile = snapshot.getValue(UserHelperClass.class);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (userProfile != null){
                    String name = userProfile.getName().trim();
                    String email = userProfile.getEmail().trim();
                    String imageUri = userProfile.getFilepath().trim();
                    profileName.setText(name);
                    profileEmail.setText(email);
                    Picasso.with(getContext()).load(imageUri).into(imgprofile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("save",MODE_PRIVATE);
        aSwitch.setChecked(sharedPreferences.getBoolean("value",false));

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sharedPref.setNightModeState(true);
                    //getActivity().finish();
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingFragment()).commit();
                    startActivity(new Intent(getContext(), Home.class));
                    getActivity().finish();
                    SharedPreferences.Editor editor= getActivity().getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    aSwitch.setChecked(true);

                }else{
                    sharedPref.setNightModeState(false);
                    startActivity(new Intent(getContext(), Home.class));
                    getActivity().finish();
                    SharedPreferences.Editor editor= getActivity().getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    aSwitch.setChecked(false);
                }
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure want to logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                getActivity().finish();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("status","offline");
                                databaseReference.child(userID).updateChildren(hashMap);
                                DocumentReference lucknowRef= FirebaseFirestore.getInstance().collection("User").document(firebaseUser.getUid());
                                lucknowRef.update(hashMap);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();


            }
        });

//        terms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), TermAConActivity.class));
//            }
//        });

//        safety.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), SafetyTipActivity.class));
//            }
//        });

        imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserProfile.class);
                intent.putExtra("product_seller",firebaseUser.getUid());
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HelpActivity.class));
            }
        });


        return root;

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}