package com.example.barter.notification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;

public class NotifInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.ThemeDark);
        } else {
            setTheme(R.style.ThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_info);
    }
}