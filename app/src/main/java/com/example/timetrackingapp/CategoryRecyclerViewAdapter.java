package com.example.timetrackingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private List<YourDataModel> dataList;
    private Context context;

    public CategoryRecyclerViewAdapter(Context context, List<YourDataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        YourDataModel data = dataList.get(position);
        holder.bindData(data); // Use the bindData method to update views
        // Bind other data to views as needed
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
