package com.example.timetrackingapp;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class StopWatchFragment extends Fragment {
    String storedCategoryName;
    String activityName;
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stop_watch, container, false);
        Bundle args = getArguments();
        TextView catTextView = rootView.findViewById(R.id.CategoryText);
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
        chronometer.setOnChronometerTickListener(null);

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
        Button startButton = rootView.findViewById(R.id.btnStart);

        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;

            startButton.setText("Stop");
        } else {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;

            // Get the elapsed time in seconds
            long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;

            // Upload data to Firestore
            uploadDataToFirestore(elapsedSeconds);

            startButton.setText("Start");
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

    private void uploadDataToFirestore(long elapsedSeconds) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the selected category
        CollectionReference categoriesCollection = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories");

        // Query for the selected category
        categoriesCollection.whereEqualTo("name", storedCategoryName)
                .get()
                .addOnCompleteListener(categoryTask -> {
                    if (categoryTask.isSuccessful()) {
                        for (QueryDocumentSnapshot categoryDocument : categoryTask.getResult()) {
                            // Get the category document ID
                            String categoryId = categoryDocument.getId();

                            // Reference to the selected activity
                            CollectionReference activitiesCollection = categoriesCollection.document(categoryId)
                                    .collection("activities");

                            // Query for the selected activity
                            activitiesCollection.whereEqualTo("name", activityName)
                                    .get()
                                    .addOnCompleteListener(activityTask -> {
                                        if (activityTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot activityDocument : activityTask.getResult()) {
                                                // Get the activity document ID
                                                String activityId = activityDocument.getId();

                                                // Reference to the new collection
                                                CollectionReference newDataCollection = activitiesCollection.document(activityId)
                                                        .collection("newDataCollection");

                                                // Create a map with the data to upload
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("date", FieldValue.serverTimestamp());
                                                data.put("consumingTime", elapsedSeconds);

                                                // Add the data to the new collection
                                                newDataCollection.add(data)
                                                        .addOnSuccessListener(documentReference -> {
                                                            // Handle success
                                                            Toast.makeText(requireContext(), "Data uploaded to Firestore", Toast.LENGTH_SHORT).show();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Handle failure
                                                            Toast.makeText(requireContext(), "Failed to upload data", Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        } else {
                                            // Handle the case where fetching the activity fails
                                            Toast.makeText(requireContext(), "Failed to fetch activity", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Handle the case where fetching the category fails
                        Toast.makeText(requireContext(), "Failed to fetch category", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}