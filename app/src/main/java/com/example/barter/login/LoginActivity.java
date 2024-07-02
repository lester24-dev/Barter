package com.example.barter.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.home.Home;
import com.example.barter.register.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout reg_email, regPassword;
    Button regToLoginBtn, reg_btns, forget_btn;
    private FirebaseAuth mAuth;
    String email,password;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    public static final int MY_PERMISSIONS_REQUEST_PERMISSION = 99;
    ProgressDialog progressDialog;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    ImageView logo;
    private static final int REQUEST_CODE = 101;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        reg_email = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regToLoginBtn = findViewById(R.id.reg_login_btn);
        reg_btns = findViewById(R.id.reg_btn);
        logo = findViewById(R.id.logo);
        forget_btn = findViewById(R.id.forget_btn);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("User");

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null && user.isEmailVerified()){
            startActivity(new Intent(LoginActivity.this, Home.class));
            finish();
        }

        regToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = reg_email.getEditText().getText().toString().trim();
                password = regPassword.getEditText().getText().toString();

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Login Loading");
                progressDialog.show();

                if (email.isEmpty()){
                    reg_email.getEditText().getHint();
                    reg_email.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                if (password.isEmpty()){
                    regPassword.setError("Must be filled");
                    progressDialog.dismiss();
                    return;
                }

                else{
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(
                                                @NonNull Task<AuthResult> task)
                                        {
                                            if (task.isSuccessful()) {

                                                if (mAuth.getCurrentUser().isEmailVerified()){
                                                    FirebaseDatabase.getInstance().getReference("User").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()){
                                                                if (snapshot.child("admin_approved").getValue().equals("approved")){
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(getApplicationContext(),
                                                                                    "Login Success!!",
                                                                                    Toast.LENGTH_LONG)
                                                                            .show();

                                                                    startActivity(new Intent(LoginActivity.this, Home.class)
                                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                                    finish();
                                                                }else if(snapshot.child("admin_approved").getValue().equals("disapproved")){
                                                                    Toast.makeText(getApplicationContext(),
                                                                                    "Your account not approved by admin!!",
                                                                                    Toast.LENGTH_LONG)
                                                                            .show();
                                                                    progressDialog.dismiss();

                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }else{
                                                    FirebaseDatabase.getInstance().getReference("BlackUser").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()){
                                                                Toast.makeText(getApplicationContext(),
                                                                                "Your account not approved by admin!!",
                                                                                Toast.LENGTH_LONG)
                                                                        .show();
                                                                progressDialog.cancel();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    Toast.makeText(getApplicationContext(),
                                                                    "This email not verified!!",
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                    progressDialog.dismiss();
                                                }
                                            }

                                            else {
                                                Toast.makeText(getApplicationContext(),
                                                                "This email hasn't registered yet!!",
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                }

            }
        });

        forget_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Login Loading");
                progressDialog.show();

                email = reg_email.getEditText().getText().toString();

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            // if isSuccessful then done message will be shown
                            // and you can change the password
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Done sent",Toast.LENGTH_LONG).show();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Error Occurred",Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Error Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        reg_btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Register.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        fetchLocation();

    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reg_btn:
                registerToFirebase();
                break;
        }
    }

    private void registerToFirebase() {

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
    }



}