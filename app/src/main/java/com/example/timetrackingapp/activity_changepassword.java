package com.example.timetrackingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.android.gms.tasks.Task;

public class activity_changepassword extends AppCompatActivity {
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button saveButton;
    private Button cancelButton;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        oldPasswordEditText = findViewById(R.id.editText);
        newPasswordEditText = findViewById(R.id.editText2);
        confirmPasswordEditText = findViewById(R.id.editText7);
        saveButton = findViewById(R.id.button);
        cancelButton = findViewById(R.id.cancel_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                // Check if the new password and confirm password match
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(activity_changepassword.this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Reauthenticate the user with their current password
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    // Create an AuthCredential with the user's email and old password
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                    // Reauthenticate the user with the credential
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Password reauthentication successful, now update the password
                                        user.updatePassword(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(activity_changepassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                            finish(); // Close the activity after successful password change
                                                        } else {
                                                            Toast.makeText(activity_changepassword.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(activity_changepassword.this, "Failed to reauthenticate with current password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the password change process and close the activity
                finish();
            }
        });
    }
}