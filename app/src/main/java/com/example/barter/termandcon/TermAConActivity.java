package com.example.barter.termandcon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.barter.R;
import com.example.barter.darkmode.SharedPref;
import com.example.barter.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class TermAConActivity extends AppCompatActivity {
    SharedPref sharedPref;
    TextView text, text1;
    Button btnAccept;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_acon);

        text = findViewById(R.id.des5);
        text1 = findViewById(R.id.des7);
        btnAccept = findViewById(R.id.btnAccept);

        String password = getIntent().getExtras().getString("password");
        String emails = getIntent().getExtras().getString("email");

        text.setText(
                        "1. Be Respectful We want to create a secure environment in which people can enjoy mobile shopping. Keep your content clean and appropriate for all audiences. This includes no offensive language, hate messages, or spam \n. \n"
                                + "2. Be Skeptical when necessary. \n\n"
                        + "Always keep your personal information as private as possible. Never give out financial information such as bank accounts, credit card numbers, etc. To avoid problems, always check to see if the person you're trading with is legitimate. \n\n"
                        + "3. Don't Barter for Something You Don't Want. \n\n"
                        + "4. Always bring a friend\n\n"
                        + "We encourage our users to always bring a friend when they meet up with their trader. We also advise meeting in an open and public place during daylight."

        );

        text1.setText(
                "1. 1. No cash allowed When bartering with someone, we do not allow the use of cash. Do not be afraid to voice your concerns with users who are selling their items for cash.\n \n" +
                        "2. Misleading trades or scam Any kind of scam is prohibited on our platform.\n \n"
                        +"3. Use the platform for illegal purposes We do not permit any illegal activities to take place on our platform.\n \n\n"
                        + "3. Don't Barter for Something You Don't Want. \n\n"
                        + "4. Post, or promote through the platform any prohibited materials It is forbidden to post any items that could be harmful to our users.\n\n\n"
                        + "Actions we may take if you engage in any restricted activities. \n\n" +
                        "If we believe you have engaged in any of these activities, we may, at our sole discretion, take a number of actions to protect ourselves, our users, and others. Our actions include, but are not limited to, the following: \n\n" +
                        "● Account warning \n" +
                        "● Permanent account suspension \n" +
                        "● Take legal action against you \n\n" +
                        "Complaint \n\n" +
                        "If you have any complaints about us and our services, you may contact us at exchangeapps@gmail.com"

        );

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TermAConActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
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