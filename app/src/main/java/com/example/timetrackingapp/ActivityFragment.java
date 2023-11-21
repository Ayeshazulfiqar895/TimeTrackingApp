package com.example.timetrackingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private List<ListItem> itemList;

    // Firebase Firestore references
    private FirebaseFirestore firestore;
    private CollectionReference itemsCollection;
    private FirebaseAuth auth; // Firebase Authentication
    private FirebaseUser currentUser;
    private List<String> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        itemList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize an empty categoryList
        categoryList = new ArrayList<>();

        // Set up the adapter and RecyclerView (assuming you have this part in your code)
        adapter = new ActivityAdapter(itemList, new ActivityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click if needed
            }

            @Override
            public void onDeleteClick(int position) {
                // Remove the item from the list and Firestore
                String documentId = itemList.get(position).getDocumentId();
                itemList.remove(position);
                adapter.notifyItemRemoved(position);
                deleteItemFromFirestore(documentId);
            }
        });

        Button addButton = view.findViewById(R.id.add_button_activity);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchCategoriesFromFirestore();
            }
        });

        return view;
    }

    private void showAddActivityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.add_activity_dialog, null);
        builder.setView(dialogView);

        EditText editTextActivityName = dialogView.findViewById(R.id.customActivityEditText);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner);
        Button buttonAddActivity = dialogView.findViewById(R.id.customAddButton);

        // Set up the spinner adapter with your categoryList
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        AlertDialog dialog = builder.create();

        // Set a click listener for the "Add Activity" button
        buttonAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newActivityName = editTextActivityName.getText().toString();
                String selectedCategory = spinnerCategory.getSelectedItem().toString();

                if (!newActivityName.isEmpty() && !selectedCategory.isEmpty()) {
                    addItemToFirestore(selectedCategory, newActivityName);
                    dialog.dismiss();  // Dismiss the dialog after adding the activity
                } else {
                }
            }
        });

        dialog.show();
    }

    private void fetchCategoriesFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryList.clear();  // Clear existing categories
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String categoryName = document.getString("name");
                            if (categoryName != null && !categoryName.isEmpty()) {
                                categoryList.add(categoryName);
                            }
                        }

                        showAddActivityDialog();
                    } else {

                    }
                });
    }

    private void addItemToFirestore(String selectedCategory, String newItemText) {
        // Your code to add an item to Firestore, similar to your existing logic
    }

    private void deleteItemFromFirestore(String documentId) {
        // Your code to delete an item from Firestore, similar to your existing logic
    }
}
