package com.example.timetrackingapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.CategoryViewHolder> {
    private List<Category_modal> categories;
    private FirebaseFirestore db;
    private FragmentManager fragmentManager;

    // Constructor to initialize Firestore reference and FragmentManager
    public ReportAdapter(List<Category_modal> categories, FirebaseFirestore db, FragmentManager fragmentManager) {
        this.categories = categories;
        this.db = db;
        this.fragmentManager = fragmentManager;
    }

    public void setCategories(List<Category_modal> categories) {
        this.categories = categories;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryNameTextView;
        private Button detailsButton;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryitem_text);
            detailsButton = itemView.findViewById(R.id.Details);

            detailsButton.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsFragment(position);
                }
            });
        }

        private void openDetailsFragment(int position) {
            chartFragment detailsFragment = new chartFragment();
            Category_modal selectedCategory = categories.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("categoryId", categories.get(position).getId());
            bundle.putString("categoryName", selectedCategory.getName());
            detailsFragment.setArguments(bundle);

            // Replace the current fragment with the detailsFragment
            fragmentManager.beginTransaction()
                    .replace(R.id.box1, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        }

        public void bind(Category_modal data) {
            categoryNameTextView.setText(data.getName());
            itemView.setTag(data);  // Set the category object as a tag to retrieve it later
        }
    }
}
