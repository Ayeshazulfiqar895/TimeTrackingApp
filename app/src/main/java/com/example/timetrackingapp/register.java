package com.example.timetrackingapp;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        editTextName = findViewById(R.id.editText3);
        editTextPhoneNumber = findViewById(R.id.editText5);
        editTextEmail = findViewById(R.id.editText4);
        editTextPassword = findViewById(R.id.editText6);
        buttonRegister = findViewById(R.id.buttonRegister);
        ImageView imageView3 = findViewById(R.id.imageView3);

        // Handle registration button click
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                final String name = editTextName.getText().toString();
                final String phoneNumber = editTextPhoneNumber.getText().toString();
                final String email = editTextEmail.getText().toString();
                final String password = editTextPassword.getText().toString();
                progressBar=findViewById(R.id.LoginprogressBar);
                progressBar.setVisibility(View.VISIBLE);
                // Perform basic validation (you should perform more checks)
                if (name.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new user in Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Registration successful
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String userId = user.getUid();

                                        // Store user data in Firestore
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("name", name);
                                        userData.put("phoneNumber", phoneNumber);
                                        userData.put("email", email);
                                        userData.put("password", password);

                                        // Get a Firestore document reference
                                        DocumentReference docRef = db.collection("users").document(userId);

                                        docRef.set(userData) // Use .set() to overwrite existing data
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(register.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                                            // Clear input fields
                                                            editTextName.setText("");
                                                            editTextPhoneNumber.setText("");
                                                            editTextEmail.setText("");
                                                            editTextPassword.setText("");
                                                            progressBar.setVisibility(View.GONE);
                                                            Intent intent = new Intent(register.this, login_pg.class);
                                                            startActivity(intent);
                                                        } else {
                                                            progressBar.setVisibility(View.GONE);

                                                            Toast.makeText(register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Registration failed, handle the exception
                                        Exception exception = task.getException();
                                        if (exception != null) {
                                            Toast.makeText(register.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(register.this, "Registration failed: Unknown error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(register.this, login_pg.class);
                startActivity(signInIntent);
            }
        });
    }
}
