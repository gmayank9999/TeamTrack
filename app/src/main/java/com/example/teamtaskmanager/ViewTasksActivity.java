package com.example.teamtaskmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewTasksActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private DatabaseReference tasksRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks);

        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Tasks");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyTextView = findViewById(R.id.emptyTextView);

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList,this);
        tasksRecyclerView.setAdapter(taskAdapter);

        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");

        loadTasks();
    }

    private void loadTasks() {
        progressBar.setVisibility(View.VISIBLE);
        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                emptyTextView.setVisibility(taskList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Back button support
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close this activity
        return true;
    }

}
