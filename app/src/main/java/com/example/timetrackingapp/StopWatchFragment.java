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

import java.util.Calendar;
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

                                                // Get the current date and month
                                                String currentDateAndMonth = getCurrentDateAndMonth();

                                                // Check if a document with the same dateAndMonth already exists
                                                newDataCollection.whereEqualTo("dateAndMonth", currentDateAndMonth)
                                                        .get()
                                                        .addOnCompleteListener(dateQueryTask -> {
                                                            if (dateQueryTask.isSuccessful() && !dateQueryTask.getResult().isEmpty()) {
                                                                // If document exists, update consuming time
                                                                updateConsumingTime(userId, categoryId, activityId, currentDateAndMonth, elapsedSeconds);
                                                            } else {
                                                                // If document doesn't exist, create a new one
                                                                createNewDateCollection(userId, categoryId, activityId, currentDateAndMonth, elapsedSeconds);
                                                            }
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

    private void updateConsumingTime(String userId, String categoryId, String activityId, String currentDateAndMonth, long elapsedSeconds) {
        // Update consuming time in the existing document
        CollectionReference newDataCollection = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories")
                .document(categoryId)
                .collection("activities")
                .document(activityId)
                .collection("newDataCollection");

        newDataCollection.whereEqualTo("dateAndMonth", currentDateAndMonth)
                .get()
                .addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful() && !updateTask.getResult().isEmpty()) {
                        // Get the document ID
                        String documentId = updateTask.getResult().getDocuments().get(0).getId();

                        // Update consuming time
                        newDataCollection.document(documentId)
                                .update("consumingTime", FieldValue.increment(elapsedSeconds))
                                .addOnSuccessListener(documentReference -> {
                                    // Consuming time updated successfully
                                    Toast.makeText(requireContext(), "Consuming time updated", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Toast.makeText(requireContext(), "Failed to update consuming time", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void createNewDateCollection(String userId, String categoryId, String activityId, String currentDateAndMonth, long elapsedSeconds) {
        // Create a new document with consuming time
        CollectionReference newDataCollection = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories")
                .document(categoryId)
                .collection("activities")
                .document(activityId)
                .collection("newDataCollection");

        Map<String, Object> data = new HashMap<>();
        data.put("dateAndMonth", currentDateAndMonth);
        data.put("consumingTime", elapsedSeconds);

        newDataCollection.add(data)
                .addOnSuccessListener(documentReference -> {
                    // New document created successfully
                    Toast.makeText(requireContext(), "New document created", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(requireContext(), "Failed to create new document", Toast.LENGTH_SHORT).show();
                });
    }

    private String getCurrentDateAndMonth() {
        // Use your preferred way to get the current date and month
        // Here, I'm using the Calendar class
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Format the date and month as a string (you can customize the format as needed)
        return day + "/" + month + "/" + year;
    }

}