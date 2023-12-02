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

public class ReportFragment extends Fragment {
    private RecyclerView categoryRecyclerView;

    private ProgressBar progressBar;

    private ReportAdapter ReportAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_list, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressBar =rootView.findViewById(R.id.CatgeoryprogressBar);
        categoryRecyclerView = rootView.findViewById(R.id.categoryRecyclerView);
        ReportAdapter = new ReportAdapter(new ArrayList<>(), db, getActivity().getSupportFragmentManager());

        categoryRecyclerView.setAdapter(ReportAdapter);
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
                        progressBar.setVisibility(View.GONE);
                        updateRecyclerView(categories);

                    } else {
                        // Handle failure
                        Toast.makeText(requireContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateRecyclerView(List<Category_modal> categories) {
        ReportAdapter.setCategories(categories);
        ReportAdapter.notifyDataSetChanged();
    }
}
