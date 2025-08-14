package com.example.teamtaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeamLeadDashboardActivity extends AppCompatActivity {

    Button assignTaskButton, viewTasksButton, logoutButton;
    TextView welcomeTextView, tvTotalTasks, tvActiveTasks, tvRecentActivity;
    private DatabaseReference tasksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_lead_dashboard);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Team Lead Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        assignTaskButton = findViewById(R.id.assignTaskButton);
        viewTasksButton = findViewById(R.id.viewTasksButton);
        logoutButton = findViewById(R.id.logoutButton);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvActiveTasks = findViewById(R.id.tvActiveTasks);
        tvRecentActivity = findViewById(R.id.tvRecentActivity);

        assignTaskButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AssignTaskActivity.class));
        });

        viewTasksButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeamLeadDashboardActivity.this, ViewTasksActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Initialize database reference
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");

        // Load dashboard data
        loadDashboardData();
    }

    private void loadDashboardData() {
        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalTasks = 0;
                int activeTasks = 0;
                StringBuilder recentActivity = new StringBuilder();

                for (DataSnapshot taskSnap : snapshot.getChildren()) {
                    totalTasks++;
                    Task task = taskSnap.getValue(Task.class);
                    if (task != null) {
                        String status = task.getStatus();
                        if (status != null && !status.equalsIgnoreCase("completed")) {
                            activeTasks++;
                        }

                        // Add to recent activity (last 3 tasks)
                        if (recentActivity.length() < 200) { // Limit text length
                            String taskTitle = task.getTitle() != null ? task.getTitle() : "Untitled";
                            String taskStatus = task.getStatus() != null ? task.getStatus() : "Unknown";
                            recentActivity.append("• ").append(taskTitle).append(" (").append(taskStatus).append(")\n");
                        }
                    }
                }

                // Update UI
                tvTotalTasks.setText(String.valueOf(totalTasks));
                tvActiveTasks.setText(String.valueOf(activeTasks));

                if (recentActivity.length() > 0) {
                    tvRecentActivity.setText(recentActivity.toString());
                } else {
                    tvRecentActivity.setText("No recent activity");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeamLeadDashboardActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
