package com.example.teamtaskmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployeeViewTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noTasksTextView;

    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FirebaseAuth mAuth;
    private DatabaseReference tasksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_view_tasks);

        recyclerView = findViewById(R.id.recyclerViewEmployeeTasks);
        progressBar = findViewById(R.id.progressBar);
        noTasksTextView = findViewById(R.id.noTasksTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList,this);
        recyclerView.setAdapter(taskAdapter);

        mAuth = FirebaseAuth.getInstance();
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");

        loadEmployeeTasks();
    }

    private void loadEmployeeTasks() {
        progressBar.setVisibility(View.VISIBLE);
        noTasksTextView.setVisibility(View.GONE);

        if (mAuth.getCurrentUser() == null) {
            progressBar.setVisibility(View.GONE);
            noTasksTextView.setVisibility(View.VISIBLE);
            noTasksTextView.setText("User not logged in.");
            return;
        }

        String currentUserId = mAuth.getCurrentUser().getUid();

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null && task.getAssignedToId() != null &&
                            task.getAssignedToId().equals(currentUserId)) {
                        taskList.add(task);
                    }
                }

                progressBar.setVisibility(View.GONE);
                if (taskList.isEmpty()) {
                    noTasksTextView.setVisibility(View.VISIBLE);
                    noTasksTextView.setText("No tasks assigned.");
                } else {
                    noTasksTextView.setVisibility(View.GONE);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                noTasksTextView.setVisibility(View.VISIBLE);
                noTasksTextView.setText("Failed to load tasks: " + error.getMessage());
            }
        });
    }
}
