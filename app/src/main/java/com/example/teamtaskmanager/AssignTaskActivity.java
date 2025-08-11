package com.example.teamtaskmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class AssignTaskActivity extends AppCompatActivity {

    private EditText editTitle, editDescription;
    private Spinner spinnerUsers;
    private Button buttonAssign;
    private ProgressBar progressBar;

    private DatabaseReference usersRef, tasksRef;
    private ArrayAdapter<String> emailAdapter;
    private ArrayList<String> emailList = new ArrayList<>();
    private ArrayList<String> uidList = new ArrayList<>();
    private String selectedUserName = null; // This will store email or name
    private String selectedUserId = null;   // This will store UID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Assign Task");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize Views
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        spinnerUsers = findViewById(R.id.spinnerUsers);
        buttonAssign = findViewById(R.id.buttonAssign);
        progressBar = findViewById(R.id.progressBar);

        // Firebase Refs
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");

        // Setup Spinner
        emailAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, emailList);
        spinnerUsers.setAdapter(emailAdapter);

        loadEmployeeEmails();



// Inside onCreate
        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUserName = emailList.get(position); // email or name
                selectedUserId = uidList.get(position);     // UID
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUserName = null;
                selectedUserId = null;
            }
        });


        buttonAssign.setOnClickListener(v -> assignTask());
    }

    private void loadEmployeeEmails() {
        progressBar.setVisibility(View.VISIBLE);
        usersRef.orderByChild("role").equalTo("Employee")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emailList.clear();
                        uidList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String email = child.child("email").getValue(String.class);
                            String uid = child.getKey();
                            if (email != null && uid != null) {
                                emailList.add(email);
                                uidList.add(uid);
                            }
                        }
                        emailAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AssignTaskActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void assignTask() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || selectedUserId == null || selectedUserName == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskId = tasksRef.push().getKey();
        long timestamp = System.currentTimeMillis();

        Task task = new Task(
                taskId,
                title,
                description,
                selectedUserName,  // assignedTo (name/email)
                selectedUserId,    // assignedToId (UID)
                timestamp,
                "Pending"
        );

        tasksRef.child(taskId).setValue(task)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Task assigned", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
