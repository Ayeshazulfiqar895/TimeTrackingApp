package com.example.timetrackingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrackTimeFragment extends Fragment {
    private RecyclerView categoryRecyclerView;
    private TrackTimeAdapter categoryAdapter;
    private FirebaseFirestore db;
    private RecyclerView activityRecyclerView;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_time, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        activityRecyclerView = rootView.findViewById(R.id.activity_RecyclerView);
        categoryRecyclerView = rootView.findViewById(R.id.category_RecyclerView);
        categoryAdapter = new TrackTimeAdapter(new ArrayList<>(), db,
                new TrackTimeAdapter.OnItemClickListener() {
                    @Override
                    public void onDeleteClick(int position) {
                        // Handle delete action if needed
                    }

                    @Override
                    public void onEditClick(int position) {
                        // Handle edit action if needed
                    }

                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(requireContext(), "hello", Toast.LENGTH_SHORT).show();

                        onCategoryItemClick(position);

                    }
                });

        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fetchCategoriesFromFirestore();

        return rootView;
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
                        updateRecyclerView(categories);
                    } else {
                        // Handle failure
                        Toast.makeText(requireContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onCategoryItemClick(int position) {
        activityRecyclerView.setVisibility(View.VISIBLE);


    }

    private void updateRecyclerView(List<Category_modal> categories) {
        categoryAdapter.setCategories(categories);
        categoryAdapter.notifyDataSetChanged();
    }
}
