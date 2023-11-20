package com.example.timetrackingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryRecyclerViewAdapter adapter;
    private List<YourDataModel> dataList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        // Find RecyclerView in the inflated layout
        recyclerView = view.findViewById(R.id.categoryRecyclerView);

        // Set up RecyclerView with a LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize your data list
        dataList = initData();

        // Initialize and set the adapter
        adapter = new CategoryRecyclerViewAdapter(getActivity(), dataList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<YourDataModel> initData() {
        // Create and return your data list here
        // This could be a list of objects with the data for each item
        List<YourDataModel> data = new ArrayList<>();

        // Add your data to the list, for example:
        data.add(new YourDataModel(/*data parameters*/));

        return data;
    }
}
