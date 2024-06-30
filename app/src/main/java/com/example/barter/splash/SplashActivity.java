package com.example.barter.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.barter.R;
import com.example.barter.home.Home;
import com.example.barter.login.LoginActivity;
import com.example.barter.register.Register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
ImageView splash;
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash = findViewById(R.id.splash);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(2000);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null && user.isEmailVerified()){
                    finish();
                    startActivity(new Intent(SplashActivity.this, Home.class));
                }else{
                    finish();
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
        }, 2000);
    }
}