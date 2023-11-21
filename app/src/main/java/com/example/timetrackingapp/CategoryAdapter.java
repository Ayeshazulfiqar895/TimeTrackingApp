package com.example.timetrackingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category_modal> categories;

    public CategoryAdapter(List<Category_modal> categories) {
        this.categories = categories;
    }

    public void setCategories(List<Category_modal> categories) {
        this.categories = categories;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
        return new CategoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Bind data to the ViewHolder
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryNameTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize your views
            categoryNameTextView = itemView.findViewById(R.id.categoryitem_text);
        }

        public void bind(Category_modal category) {
            // Bind category data to your ViewHolder's views
            categoryNameTextView.setText(category.getName());
        }
    }
}
