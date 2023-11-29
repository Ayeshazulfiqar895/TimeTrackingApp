package com.example.timetrackingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private List<Activity_Modal> itemList;
    private ProgressBar progressBar;
    // Firebase Firestore references
    private FirebaseFirestore firestore;
    private FirebaseAuth auth; // Firebase Authentication
    private FirebaseUser currentUser;
    private List<String> categoryList = new ArrayList<>(); // Initialize categoryList

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        itemList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        progressBar =view.findViewById(R.id.ActivityprogressBar);
        // Initialize the RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.activityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ActivityAdapter(itemList, new ActivityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click if needed
            }
            @Override
            public void onDeleteClick(int position) {
                // Retrieve the correct documentId for the activity
                String documentId = itemList.get(position).getDocumentId();



                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Delete Category");
                builder.setMessage("Are you sure you want to delete this Activity?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle delete action
                        itemList.remove(position);
                        adapter.notifyItemRemoved(position);
                        deleteItemFromFirestore(documentId);
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });
        recyclerView.setAdapter(adapter);

        // Fetch all activities from Firestore after initializing categoryList
        fetchAllActivitiesFromFirestore();

        Button addButton = view.findViewById(R.id.add_button_activity);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddActivityDialog();
            }
        });

        return view;
    }

    private void showAddActivityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.add_activity_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        EditText editTextActivityName = dialogView.findViewById(R.id.customActivityEditText);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner);
        Button buttonAddActivity = dialogView.findViewById(R.id.dialogAddButton);
        Button activityCancelButton = dialogView.findViewById(R.id.ActivityCancelButton);
        Button moveToCategoryFragment=dialogView.findViewById(R.id.moveToCategoryFragment);
        moveToCategoryFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.nav_category);
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.box1, new CategoryFragment());
                fragmentTransaction.commit();
                dialog.dismiss();

            }
        });
        // Set up the spinner adapter with your categoryList
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        if (categoryList.isEmpty()) {
            List<String> noCategoryList = new ArrayList<>();
            noCategoryList.add("No Category");
            ArrayAdapter<String> noCategoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, noCategoryList);
            noCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(noCategoryAdapter);
        }

        // Set a click listener for the "Add Activity" button
        buttonAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newActivityName = editTextActivityName.getText().toString();
                String selectedCategory = spinnerCategory.getSelectedItem().toString();

                if (!newActivityName.isEmpty() && !selectedCategory.isEmpty()&&selectedCategory!="No Category") {
                    addItemToFirestore(selectedCategory, newActivityName);
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Activity Created Successfully", Toast.LENGTH_SHORT).show();
                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setSelectedItemId(R.id.nav_activity);
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.box1, new ActivityFragment());
                    fragmentTransaction.commit();


                } else {
                    Toast.makeText(requireContext(), "Please create Category First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set a click listener for the "Cancel" button
        activityCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void fetchAllActivitiesFromFirestore() {
        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnCompleteListener(categoryTask -> {
                    if (categoryTask.isSuccessful()) {
                        categoryList.clear();  // Clear existing categories

                        for (QueryDocumentSnapshot categoryDocument : categoryTask.getResult()) {
                            String categoryName = categoryDocument.getString("name");
                            categoryList.add(categoryName);
                        }


                        adapter.notifyDataSetChanged();


                        fetchActivitiesForCategories();
                    } else {
                        Log.e("Firestore", "Error fetching categories: " + categoryTask.getException());
                        // Handle the case where fetching categories fails
                    }
                });
    }
    private void addItemToFirestore(String selectedCategory, String newItemText) {
        String userId = auth.getCurrentUser().getUid();

        // Create a new activity
        Activity_Modal activity = new Activity_Modal();
        activity.setName(newItemText);
        activity.setCategory(selectedCategory);

        // Set documentId to be equal to the name
        activity.setDocumentId(newItemText);

        // Create a reference to the categories collection for the current user
        CollectionReference categoriesCollection = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories");

        // Query for the selected category
        categoriesCollection.whereEqualTo("name", selectedCategory)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the category document ID
                            String categoryId = document.getId();

                            // Store the activity under the selected category
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userId)
                                    .collection("categories")
                                    .document(categoryId)
                                    .collection("activities")
                                    .document(newItemText)  // Set documentId to be equal to the name
                                    .set(activity)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure
                                            // You might want to show an error message to the user
                                        }
                                    });
                        }
                    } else {
                        // Handle the case where fetching categories fails
                    }
                });
    }

    private void fetchActivitiesForCategories() {
        String userId = auth.getCurrentUser().getUid();

        for (String category : categoryList) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("categories")
                    .whereEqualTo("name", category)
                    .get()
                    .addOnCompleteListener(categoryTask -> {
                        if (categoryTask.isSuccessful()) {
                            for (QueryDocumentSnapshot categoryDocument : categoryTask.getResult()) {
                                String categoryId = categoryDocument.getId();

                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(userId)
                                        .collection("categories")
                                        .document(categoryId)
                                        .collection("activities")
                                        .get()
                                        .addOnCompleteListener(activityTask -> {
                                            if (activityTask.isSuccessful()) {

                                                for (QueryDocumentSnapshot activityDocument : activityTask.getResult()) {
                                                    Activity_Modal activity = activityDocument.toObject(Activity_Modal.class);
                                                    Log.d("Firestore", "Activity Name: " + activity.getName());
                                                    itemList.add(activity);
                                                }
                                                progressBar.setVisibility(View.GONE);

                                                adapter.notifyDataSetChanged();

                                            } else {
                                                Log.e("Firestore", "Error fetching activities: " + activityTask.getException());
                                                // Handle the case where fetching activities fails
                                            }
                                        });
                            }
                        } else {
                            Log.e("Firestore", "Error fetching categories: " + categoryTask.getException());
                            // Handle the case where fetching categories fails
                        }
                    });
        }
    }
    private void deleteItemFromFirestore(String documentId) {
        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnCompleteListener(categoryTask -> {
                    if (categoryTask.isSuccessful()) {
                        for (QueryDocumentSnapshot categoryDocument : categoryTask.getResult()) {
                            String categoryId = categoryDocument.getId();

                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userId)
                                    .collection("categories")
                                    .document(categoryId)
                                    .collection("activities")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Activity deleted successfully
                                            Toast.makeText(requireContext(), "Activity Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            // Optionally, you may want to refresh the UI or perform other actions after deletion.
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure
                                            Log.e("Firestore", "Error deleting activity: " + e.getMessage());
                                        }
                                    });
                        }
                    } else {
                        // Handle the case where fetching categories fails
                        Log.e("Firestore", "Error fetching categories: " + categoryTask.getException());
                    }
                });
    }

}
