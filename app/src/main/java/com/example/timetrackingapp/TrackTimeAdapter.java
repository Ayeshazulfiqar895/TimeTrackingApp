package com.example.timetrackingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TrackTimeAdapter extends RecyclerView.Adapter<TrackTimeAdapter.CategoryViewHolder> {
    private List<Category_modal> categories;
    private FirebaseFirestore db;
    private OnItemClickListener onItemClickListener;
    private OnEditClickListener onEditClickListener;

    // Constructor to initialize Firestore reference
    public TrackTimeAdapter(List<Category_modal> categories, FirebaseFirestore db, OnItemClickListener onItemClickListener) {
        this.categories = categories;
        this.db = db;
        this.onItemClickListener = onItemClickListener;
    }

    public void setCategories(List<Category_modal> categories) {
        this.categories = categories;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    // Method to set the edit click listener
    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    // Method to set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Method to delete a category from Firestore
    public void deleteCategory(int position) {
        Category_modal category = categories.get(position);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .collection("categories")
                .document(category.getName()) // Assuming "name" is a unique identifier for the category
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Category deleted successfully
                    categories.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_track_card, parent, false);
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

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onEditClick(int position);
        void onItemClick(int position);
    }

    public List<Category_modal> getCategories() {
        return categories;
    }

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryNameTextView;
        private ImageView deleteButton;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.category_trackName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Category_modal data) {
            categoryNameTextView.setText(data.getName());
            itemView.setTag(data);
        }
    }
}
