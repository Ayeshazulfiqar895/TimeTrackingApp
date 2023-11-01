package com.example.timetrackingapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class Track_time extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_time);

        // Find the ImageView by ID
        ImageView imageView4 = findViewById(R.id.imageView4);


        // Set an OnClickListener for the ImageView
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here, e.g., navigate to the home page
                Intent intent = new Intent(Track_time.this, Home.class);
                startActivity(intent);
            }
        });
    }
}
