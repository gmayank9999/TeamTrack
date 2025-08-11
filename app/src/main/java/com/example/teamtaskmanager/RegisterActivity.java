//package com.example.teamtaskmanager;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.*;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.*;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    EditText emailEditText, passwordEditText;
//    RadioGroup roleRadioGroup;
//    Button registerButton;
//
//    FirebaseAuth mAuth;
//    DatabaseReference usersRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register); // Link to XML
//
//        // Initialize views
//        emailEditText = findViewById(R.id.emailEditText);
//        passwordEditText = findViewById(R.id.passwordEditText);
//        roleRadioGroup = findViewById(R.id.roleRadioGroup);
//        registerButton = findViewById(R.id.registerButton);
//        TextView loginTextView = findViewById(R.id.loginTextView);
//        loginTextView.setOnClickListener(v -> {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish(); // optional
//        });
//
//
//        // Firebase setup
//        mAuth = FirebaseAuth.getInstance();
//        usersRef = FirebaseDatabase.getInstance().getReference("Users");
//
//        registerButton.setOnClickListener(v -> registerUser());
//    }
//
//    private void registerUser() {
//        String email = emailEditText.getText().toString().trim();
//        String password = passwordEditText.getText().toString().trim();
//        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
//
//        if (email.isEmpty() || password.isEmpty() || selectedId == -1) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String role = ((RadioButton) findViewById(selectedId)).getText().toString();
//
//        Toast.makeText(this, "Registering user...", Toast.LENGTH_SHORT).show();
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        String uid = mAuth.getCurrentUser().getUid();
//                        User user = new User(email, role);
//
//                        usersRef.child(uid).setValue(user)
//                                .addOnCompleteListener(t -> {
//                                    if (t.isSuccessful()) {
//                                        Toast.makeText(this, "Registered successfully! Please login.", Toast.LENGTH_SHORT).show();
//                                        // ✅ Redirect to login
//                                        startActivity(new Intent(this, LoginActivity.class));
//                                        finish();
//                                    } else {
//                                        Toast.makeText(this, "Database error: " + t.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
//
//                    } else {
//                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//
//
//    // Firebase needs this model class
//    public static class User {
//        public String email, role;
//        public User() {}
//        public User(String email, String role) {
//            this.email = email;
//            this.role = role;
//        }
//    }
//}
package com.example.teamtaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity
{

    EditText emailEditText, passwordEditText;
    RadioGroup roleRadioGroup;
    Button registerButton;
    TextView loginTextView;

    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI elements
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Redirect to login screen if "Already have an account?" clicked
        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Handle Register button
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate role selected
        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected role
        RadioButton selectedRadioButton = findViewById(selectedId);
        String role = selectedRadioButton.getText().toString();

        // Validate fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Registering user...", Toast.LENGTH_SHORT).show();

        // Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            // Create user object to save in database
                            User user = new User(email, role);
                            usersRef.child(uid).setValue(user)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Database error: " + task2.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Auth error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
