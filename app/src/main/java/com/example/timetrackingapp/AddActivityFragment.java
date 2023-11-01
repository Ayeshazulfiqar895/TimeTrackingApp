package com.example.timetrackingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AddActivityFragment extends Fragment {
    private Spinner spinner;
    private EditText activityNameEditText;
    private Button addButton;
    private Button cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_activity, container, false);

        spinner = view.findViewById(R.id.spinner);
        activityNameEditText = view.findViewById(R.id.tracktime_text);
        addButton = view.findViewById(R.id.button3);
        cancelButton = view.findViewById(R.id.cancel_button);

        // Populate the Spinner with categories (you can replace this with your category data)
        String[] categories = {"Category 1", "Category 2", "Category 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected category and activity name
                String selectedCategory = spinner.getSelectedItem().toString();
                String activityName = activityNameEditText.getText().toString();

                // TODO: Save the category and activity name to the database
                // You should implement the database operations here

                // Display a message indicating that the data has been added
                Toast.makeText(requireContext(), "Category: " + selectedCategory + "\nActivity Name: " + activityName, Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the EditText and reset the Spinner selection
                activityNameEditText.setText("");
                spinner.setSelection(0); // Set the first category as the default selection
            }
        });

        return view;
    }
}
