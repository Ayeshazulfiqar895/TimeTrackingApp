package com.example.timetrackingapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryFragment extends Fragment {
    private RecyclerView categoryRecyclerView;

    private ProgressBar progressBar;

    private CategoryAdapter categoryAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressBar =rootView.findViewById(R.id.CatgeoryprogressBar);
        categoryRecyclerView = rootView.findViewById(R.id.categoryRecyclerView);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), db,
                new CategoryAdapter.OnItemClickListener() {
                    @Override
                    public void onDeleteClick(int position) {
                        showDeleteConfirmationDialog(position);
                    }

                    @Override
                    public void onEditClick(int position) {
                        //showEditCategoryDialog(position);
                    }
                });
        categoryAdapter.setOnEditClickListener(new CategoryAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(int position) {
              //  showEditCategoryDialog(position);
            }
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fetchCategoriesFromFirestore();

        Button addButton = rootView.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCategoryDialog();
            }
        });

        return rootView;
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoryName = editTextCategoryName.getText().toString();

                if (!categoryName.isEmpty()) {
                    // Generate an ID for the category (you can use UUID.randomUUID().toString())
                    String categoryId = UUID.randomUUID().toString();
                    uploadCategoryToFirestore(categoryId, categoryName);

                    fetchCategoriesFromFirestore();

                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle cancel action if needed
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void uploadCategoryToFirestore(String categoryId, String categoryName) {
        String userId = auth.getCurrentUser().getUid();

        Category_modal category = new Category_modal(categoryId, categoryName);

        db.collection("users").document(userId)
                .collection("categories")
                .document(categoryId)  // Use the generated ID as the document ID
                .set(category)
                .addOnSuccessListener(documentReference -> {
                    // Category added successfully
                    // You can update the UI or perform any other action
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void fetchCategoriesFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Category_modal> categories = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category_modal category = document.toObject(Category_modal.class);
                            categories.add(category);
                        }
                        progressBar.setVisibility(View.GONE);
                        updateRecyclerView(categories);

                    } else {
                        // Handle failure
                        Toast.makeText(requireContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Category");
        builder.setMessage("Are you sure you want to delete this category?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle delete action
                deleteCategory(position);
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteCategory(int position) {
        Category_modal category = categoryAdapter.getCategories().get(position);
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .collection("categories")
                .document(category.getId())  // Use the category ID as the document ID
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    categoryAdapter.deleteCategory(position);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
//
//    private void showEditCategoryDialog(int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
//        builder.setView(dialogView);
//
//        EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);
//        editTextCategoryName.setText(categoryAdapter.getCategories().get(position).getName());
//
//        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String updatedCategoryName = editTextCategoryName.getText().toString();
//                if (!updatedCategoryName.isEmpty()) {
//                    updateCategoryInFirestore(position, updatedCategoryName);
//                }
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Handle cancel action if needed
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

//    private void updateCategoryInFirestore(int position, String updatedCategoryName) {
//        String userId = auth.getCurrentUser().getUid();
//        Category_modal category = categoryAdapter.getCategories().get(position);
//        db.collection("users").document(userId)
//                .collection("categories")
//                .document(category.getId())  // Use the category ID as the document ID
//                .update("name", updatedCategoryName)
//                .addOnSuccessListener(aVoid -> {
//                    // Handle success
//                    category.setName(updatedCategoryName);
//                    categoryAdapter.notifyItemChanged(position);
//                })
//                .addOnFailureListener(e -> {
//                    // Handle failure
//                });
//    }
    private void updateRecyclerView(List<Category_modal> categories) {
        categoryAdapter.setCategories(categories);
        categoryAdapter.notifyDataSetChanged();
    }
}
