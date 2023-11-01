package com.example.timetrackingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;


public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Retrieve the Bundle passed from login_pg.java
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String userName = bundle.getString("userName");

            // Now, you can use the userName in your Home activity
            // For example, you can display it in a TextView
            TextView welcomeTextView = findViewById(R.id.userName);
            welcomeTextView.setText("Welcome, " + userName + "!");
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set click listener for the logout ImageView
        ImageView logoutImageView = findViewById(R.id.logoutImage);
        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutAndRedirectToLogin();
            }
        });

        // Set click listener for the activity CardView
        CardView activityCard = findViewById(R.id.activityCard);
        activityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the ActivityPage activity
                Intent intent = new Intent(Home.this, Activity_list.class);
                startActivity(intent);
            }
        });
        CardView viewReportCard = findViewById(R.id.viewReportCard);
        viewReportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here, e.g., navigate to the reports page
                Intent intent = new Intent(Home.this, Reports.class);
                startActivity(intent);
            }
        });

        CardView trackCard = findViewById(R.id.trackCard);
        trackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here, navigate to the Track_time activity
                Intent intent = new Intent(Home.this, Track_time.class);
                startActivity(intent);
            }
        });
    }


        private void logoutAndRedirectToLogin() {
        // Log out the current user
        mAuth.signOut();

        // Start the Sign In activity
        Intent intent = new Intent(Home.this, login_pg.class);
        startActivity(intent);

        // Finish the current activity to prevent going back to it
        finish();
    }
}
