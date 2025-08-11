package com.example.teamtaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class TeamLeadDashboardActivity extends AppCompatActivity {

    Button assignTaskButton, viewTasksButton, logoutButton;
    TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_lead_dashboard);

        assignTaskButton = findViewById(R.id.assignTaskButton);
        viewTasksButton = findViewById(R.id.viewTasksButton);
        logoutButton = findViewById(R.id.logoutButton);
        welcomeTextView = findViewById(R.id.welcomeTextView);

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
    }
}
