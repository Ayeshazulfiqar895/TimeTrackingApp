package com.example.timetrackingapp;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class StopWatchFragment extends Fragment {
    String storedCategoryName ;

    String activityName;
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stop_watch, container, false);
        Bundle args = getArguments();
        TextView catTextView= rootView.findViewById(R.id.CategoryText);
        if (args != null) {
            storedCategoryName = Singleton.getInstance().getClickedCategoryName();
            activityName = args.getString("activityName");
            TextView textView = rootView.findViewById(R.id.ActivityText);
            catTextView.setText(storedCategoryName);
            textView.setText(activityName);
        }


        chronometer = rootView.findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        chronometer.setOnChronometerTickListener(null); // Remove the existing listener

        rootView.findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChronometer(v);
            }
        });

        rootView.findViewById(R.id.btnPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseChronometer(v);
            }
        });

        rootView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChronometer(v);
            }
        });

        return rootView;
    }

    public void startChronometer(View v) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
    public void resetChronometer(View v) {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        running = false;
    }
}


