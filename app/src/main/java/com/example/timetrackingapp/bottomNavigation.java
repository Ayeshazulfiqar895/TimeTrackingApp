package com.example.timetrackingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottomNavigation extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String userName = bundle.getString("userName");
            if (userName != null) {
                // You can now use 'userName' in your bottomNavigation activity
                // For example, display it in a TextView
                TextView userNameTextView = findViewById(R.id.hiddenname);
                userNameTextView.setText(userName);
            }
        }
// Initialize with the default fragment (e.g., HomeFragment)
        loadFragment(new HomeFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_activity) {
                    selectedFragment = new ActivityFragment();
                } else if (item.getItemId() == R.id.nav_category) {
                    selectedFragment = new CategoryFragment();
                }
                else if (item.getItemId() == R.id.nav_track) {
                    selectedFragment = new TrackTimeFragment();
                } else if (item.getItemId() == R.id.nav_report) {
                    selectedFragment = new ReportFragment();
                }

                return loadFragment(selectedFragment);
            }
        });
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("You really want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle logout logic here
                        finish(); // Finish the activity
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }



    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.box1, fragment);
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }
}
