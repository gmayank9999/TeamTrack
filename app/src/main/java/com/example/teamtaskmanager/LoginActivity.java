package com.example.teamtaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView registerTextView;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> loginUser());

        registerTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish(); // so that user doesn’t return to login on back press
        });
    }

    private void loginUser()
    {


        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().getReference("users").child(uid)
                                .get().addOnSuccessListener(dataSnapshot -> {
                                    if (dataSnapshot.exists()) {
                                        String role = dataSnapshot.child("role").getValue(String.class);
                                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                                        if ("Team Lead".equals(role)) {
                                            startActivity(new Intent(this, TeamLeadDashboardActivity.class));
                                        } else {
                                            startActivity(new Intent(this, EmployeeDashboardActivity.class));
                                        }
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Role not found in database", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
