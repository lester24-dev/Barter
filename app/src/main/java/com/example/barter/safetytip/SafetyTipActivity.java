package com.example.barter.safetytip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;

public class SafetyTipActivity extends AppCompatActivity {
    SharedPref sharedPref;
    TextView text;

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
        setContentView(R.layout.activity_safety_tip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Safety Tips");

        text = findViewById(R.id.text);

        text.setText(
                "SAFETY TIPS \n \n \n"
                + "1. Use the E-XCHANGE APP only until you are convinced that the individual's interest is valid. \n\n"
                + "2. Keep your personal information as private as possible. Never give out financial information such as bank account number, credit card numbers, PayPal info, etc.) \n \n"
                + "3. Do not be afraid to voice your concerns with users who are selling their items for cash. \n\n"
                + "4. Negotiate the delivery timing for the goods in advance. \n\n"
                + "5. Always meet in an open & public place during daylight hours, and we suggest you always bring a friend."
        );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case android.R.id.home:
                // addSomething();
                finish();
                break;
            default:
        }
        return true;
    }
}