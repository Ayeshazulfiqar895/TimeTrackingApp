package com.example.timetrackingapp;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {
    public static void setupBottomNavigation(BottomNavigationView bottomNavigationView, Activity activity) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Class<?> currentActivityClass = activity.getClass();

                if (itemId == R.id.nav_home && currentActivityClass != Home.class) {
                    navigateToActivity(activity, Home.class);
                } else if (itemId == R.id.nav_activity && currentActivityClass != Activity_list.class) {
                    navigateToActivity(activity, Activity_list.class);
                } else if (itemId == R.id.nav_track && currentActivityClass != Track_time.class) {
                    navigateToActivity(activity, Track_time.class);
                } else if (itemId == R.id.nav_report && currentActivityClass != Reports.class) {
                    navigateToActivity(activity, Reports.class);
                }

                return true;
            }
        });
    }


    private static void navigateToActivity(Activity currentActivity, Class<?> targetActivity) {
        Intent intent = new Intent(currentActivity, targetActivity);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
}
