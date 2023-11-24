package com.example.timetrackingapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
public class TrackTimeAdapter extends RecyclerView.Adapter<TrackTimeAdapter.CategoryViewHolder> {
    private List<Category_modal> categories;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TrackTimeActivityAdapter adapter;

    private OnItemClickListener onItemClickListener;
    private OnEditClickListener onEditClickListener;

    private List<String> categoryList = new ArrayList<>();
    private List<Activity_Modal> itemList = new ArrayList<>();

    public TrackTimeAdapter(List<Category_modal> categories, FirebaseFirestore db, OnItemClickListener onItemClickListener) {
        this.categories = categories;
        this.db = db;
        this.onItemClickListener = onItemClickListener;
    }

    public void setCategories(List<Category_modal> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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
        auth = FirebaseAuth.getInstance();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);

                        if (holder.activityRecyclerView.getVisibility() == View.VISIBLE) {
                            holder.activityRecyclerView.setVisibility(View.GONE);

                        } else {
                            holder.activityRecyclerView.setVisibility(View.VISIBLE);

                            fetchAllActivitiesFromFirestore(holder.activityRecyclerView);
                            // Pass the reference of the inner RecyclerView to the adapter
                            adapter = new TrackTimeActivityAdapter(itemList, new TrackTimeActivityAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // Handle item click in the inner RecyclerView if needed
                                }

                                @Override
                                public void onDeleteClick(int position) {
                                    // Handle delete action in the inner RecyclerView if needed
                                }
                            });

                            // Set the adapter for the inner RecyclerView
                            holder.activityRecyclerView.setAdapter(adapter);

                            // Fetch items for the inner RecyclerView when it becomes visible

                        }
                    }
                }
            }
        });
    }

    private void fetchAllActivitiesFromFirestore(RecyclerView activityRecyclerView) {
        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnCompleteListener(categoryTask -> {
                    if (categoryTask.isSuccessful()) {
                        categoryList.clear();

                        for (QueryDocumentSnapshot categoryDocument : categoryTask.getResult()) {
                            String categoryName = categoryDocument.getString("name");
                            categoryList.add(categoryName);
                        }

                        itemList.clear();
                        adapter.notifyDataSetChanged();

                        fetchActivitiesForCategories(activityRecyclerView);
                    } else {
                        Log.e("Firestore", "Error fetching categories: " + categoryTask.getException());
                    }
                });
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
        private RecyclerView activityRecyclerView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.category_trackName);
            activityRecyclerView = itemView.findViewById(R.id.activity_RecyclerView);
            activityRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));


        }

        public void bind(Category_modal data) {
            categoryNameTextView.setText(data.getName());
            itemView.setTag(data);
        }
    }

    private void fetchActivitiesForCategories(RecyclerView activityRecyclerView) {
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
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                Log.e("Firestore", "Error fetching activities: " + activityTask.getException());
                                            }
                                        });
                            }
                        } else {
                            Log.e("Firestore", "Error fetching categories: " + categoryTask.getException());
                        }
                    });
        }
    }
}
