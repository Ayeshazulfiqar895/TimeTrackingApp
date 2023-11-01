package com.example.timetrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class login_pg extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private TextView registerTextView;
    ImageView passwordVisibilityToggle ;
    boolean isPasswordVisible = false;
    private FirebaseAuth auth;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pg);
        passwordVisibilityToggle= findViewById(R.id.passwordVisibilityToggle);
        // Initialize the UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.textView1);
        registerTextView = findViewById(R.id.register);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Set click listeners for the TextViews
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Forgot Password?" click
                Intent intent = new Intent(login_pg.this, Forgot_password.class);
                startActivity(intent);
            }
        });
 // Set click listeners for the Password Visibility
        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Password is currently visible, so change to hidden state
                    passwordVisibilityToggle.setImageResource(R.drawable.pass_eye);
                    isPasswordVisible = false;
                    // Update the EditText to hide the password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    // Password is currently hidden, so change to visible state
                    passwordVisibilityToggle.setImageResource(R.drawable.pass_eye_off);
                    isPasswordVisible = true;
                    // Update the EditText to show the password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Register Yourself" click
                Intent intent = new Intent(login_pg.this, register.class);
                startActivity(intent);
            }
        });

        // Set an OnClickListener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the EditText fields
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Perform basic validation (you should perform more checks)
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login_pg.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Call Firebase authentication function
                    signInWithFirebase(username, password);
                }
            }
        });
    }

    // Firebase authentication
    private void signInWithFirebase(String email, String password) {
        if (!isValidEmail(email)) {
            // Show an error message for invalid email format
            Toast.makeText(login_pg.this, "Invalid email address format", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = auth.getCurrentUser();


                        // Fetch the user's name from Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db.collection("users").document(user.getUid());

                        userRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String userName = documentSnapshot.getString("name");
                                Toast.makeText(login_pg.this, "Hi, " + userName + "! Login successful", Toast.LENGTH_SHORT).show();

                                // Create a Bundle to pass data to Home.java
                                Bundle bundle = new Bundle();
                                bundle.putString("userName", userName);

                                 // Redirect to the home page activity
                                Intent intent = new Intent(login_pg.this, bottomNavigation.class);
                                intent.putExtras(bundle);
                                startActivity(intent);

                                // Finish the current activity to prevent going back to the login page
                                finish();
                            } else {
                                Toast.makeText(login_pg.this, "User data not found. Login failed.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(login_pg.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // If sign in fails, it will display a message to the user.
                        Toast.makeText(login_pg.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }
    // Validate email format
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}