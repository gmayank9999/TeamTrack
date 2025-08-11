package com.example.teamtaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeDashboardActivity extends AppCompatActivity {

    private TextView tvPendingCount, tvCompletedCount, tvLatestTitle, tvLatestDescription;
    private Button btnViewAll;
    private DatabaseReference tasksRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        // --- Toolbar (make sure your layout has a Toolbar with id "toolbar") ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Employee Dashboard");
        }

        // --- Views (IDs must match those in activity_employee_dashboard.xml) ---
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvLatestTitle = findViewById(R.id.tvLatestTitle);
        tvLatestDescription = findViewById(R.id.tvLatestDescription);
        btnViewAll = findViewById(R.id.btnViewAll);

        // --- Auth check ---
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = user.getUid();

        // --- Database reference (ensure your tasks node name is "tasks") ---
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");

        // --- View All button ---
        btnViewAll.setOnClickListener(v -> {
            startActivity(new Intent(EmployeeDashboardActivity.this, EmployeeViewTasksActivity.class));
        });

        // --- Load counts & latest task ---
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Query tasks assigned to this user (by UID)
        tasksRef.orderByChild("assignedToId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int pendingCount = 0;
                        int completedCount = 0;
                        Task latestTask = null;
                        long latestTimestamp = -1L;

                        for (DataSnapshot taskSnap : snapshot.getChildren()) {
                            Task task = taskSnap.getValue(Task.class);
                            if (task == null) continue;

                            String status = task.getStatus();
                            if (status != null && status.equalsIgnoreCase("completed")) {
                                completedCount++;
                            } else {
                                // treat anything else as pending/in-progress
                                pendingCount++;
                            }

                            // determine latest by timestamp (if available)
                            long ts = task.getTimestamp();
                            if (ts > latestTimestamp) {
                                latestTimestamp = ts;
                                latestTask = task;
                            }
                        }

                        // Update UI safely
                        tvPendingCount.setText(String.valueOf(pendingCount));
                        tvCompletedCount.setText(String.valueOf(completedCount));

                        if (latestTask != null) {
                            tvLatestTitle.setText(latestTask.getTitle() != null ? latestTask.getTitle() : "Untitled");
                            tvLatestDescription.setText(latestTask.getDescription() != null ? latestTask.getDescription() : "");
                        } else {
                            tvLatestTitle.setText("No tasks yet");
                            tvLatestDescription.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EmployeeDashboardActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
