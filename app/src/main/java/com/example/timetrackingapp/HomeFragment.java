package com.example.timetrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
//    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the Bundle passed from login_pg.java

            TextView welcomeTextView = view.findViewById(R.id.userName);
            TextView nametext=getActivity().findViewById(R.id.hiddenname);

            welcomeTextView.setText("Welcome, " +  nametext.getText() + "!");

                    // Set click listener for the logout ImageView
        ImageView logoutImageView = view.findViewById(R.id.logoutImage);
        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutAndRedirectToLogin();
            }
        });

                    //Set click listener for Add Activity Card Button
        CardView activityCard = view.findViewById(R.id.activityCard);
        activityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the TrackTimeFragment
                ActivityCategoryFragment activityListFragment = new ActivityCategoryFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.box1, activityListFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                bottomNavigationView.setSelectedItemId(R.id.nav_activity);
            }
        });

                    //Set click listener for Report Card Button
        CardView viewReportCard = view.findViewById(R.id.viewReportCard);
        viewReportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the TrackTimeFragment
                ReportFragment reportFragment = new ReportFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.box1, reportFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                bottomNavigationView.setSelectedItemId(R.id.nav_report);
            }
        });

                    //Set click listener for Track Card Button

        CardView trackCard = view.findViewById(R.id.trackCard);
        trackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the TrackTimeFragment
                TrackTimeFragment trackTimeFragment = new TrackTimeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.box1, trackTimeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                bottomNavigationView.setSelectedItemId(R.id.nav_track);
            }
        });

        return view;
    }

    private void logoutAndRedirectToLogin() {
        // Log out the current user
        mAuth.signOut();

        // Start the Sign In activity
        Intent intent = new Intent(getActivity(), login_pg.class);
        startActivity(intent);

        // Finish the current activity to prevent going back to it
        getActivity().finish();
    }
}
