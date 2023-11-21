package com.example.timetrackingapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private TextView categoryNameTextView;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        categoryNameTextView = itemView.findViewById(R.id.categoryitem_text);
    }

    public void bindData(Category_modal data) {
        categoryNameTextView.setText(data.getName());
        // Bind other data to views as needed
    }
}
