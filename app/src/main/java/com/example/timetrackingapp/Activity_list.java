package com.example.timetrackingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity_list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



        ImageView imageView4 = findViewById(R.id.back_arrow);

        // Set an OnClickListener for the ImageView
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here, e.g., navigate to the home page
                Intent intent = new Intent(Activity_list.this, Home.class);
                startActivity(intent);
            }
        });

        // Find the "Add Activity" button by its ID
        Button addButton = findViewById(R.id.add_button);

        // Set an OnClickListener for the "Add Activity" button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AddActivity when the button is clicked
                Intent intent = new Intent(Activity_list.this, add_activity.class);
                startActivity(intent);
            }
        });


        ImageView editImageView = findViewById(R.id.edit);
        TextView workTextView = findViewById(R.id.work_text);

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show an EditText dialog to allow editing
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_list.this);
                builder.setTitle("Edit Text");

                final EditText editText = new EditText(Activity_list.this);
                editText.setText(workTextView.getText()); // Set initial text
                builder.setView(editText);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update the TextView with the edited text
                        String editedText = editText.getText().toString();
                        workTextView.setText(editedText);

                        dialog.dismiss(); // Close the dialog
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Close the dialog without making changes
                    }
                });

                builder.show();
            }
        });

    }
}
