package com.example.timetrackingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Create a Handler to delay the transition to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null) {
                    // User is already logged in, it will navigate to the Home Screen
                    Intent homeIntent = new Intent(MainActivity.this, bottomNavigation.class);
                    startActivity(homeIntent);
                } else {
                    // User is not logged in,it will navigate to the sign-in page
                    Intent loginIntent = new Intent(MainActivity.this, login_pg.class);
                    startActivity(loginIntent);
                }

                // Finish the current (splash) activity
                finish();
            }
        }, 3000); // Delay for 3 seconds
    }
}
