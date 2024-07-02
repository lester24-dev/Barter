package com.example.barter.verify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barter.R;
import com.example.barter.home.Home;
import com.example.barter.login.LoginActivity;
import com.example.barter.register.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {
Button verify;
TextView email;
private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        email = findViewById(R.id.email);
        verify = findViewById(R.id.verify);

       String emails = getIntent().getExtras().getString("email");
       String password = getIntent().getExtras().getString("password");

       email.setText(emails);

        progressDialog = new ProgressDialog(VerifyEmail.this);
        progressDialog.setMessage("Verify Email Loading");
        progressDialog.setProgress(10);
        progressDialog.setMax(100);
        progressDialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        firebaseUser.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        startActivity(new Intent(VerifyEmail.this, GovtIDActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("id", getIntent().getExtras().getString("id")).putExtra("email", emails).putExtra("password", password));
                        finish();
                        Toast.makeText(VerifyEmail.this, "Verify Sent...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VerifyEmail.this, "Failed to send due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        if (firebaseUser.isEmailVerified()) {
            verify.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "User isn't verified...", Toast.LENGTH_SHORT).show();
        }
    }
}