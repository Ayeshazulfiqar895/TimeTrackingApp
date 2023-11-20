package com.example.timetrackingapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.categoryitem_text);
    }

    public void bindData(YourDataModel data) {
        textView.setText(data.getText());
        // Bind other data to views as needed
    }
}
