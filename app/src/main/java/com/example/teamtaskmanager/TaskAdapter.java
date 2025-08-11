package com.example.teamtaskmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final Context context;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, assignedToTextView, timestampTextView;
        Spinner statusSpinner;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.taskDescriptionTextView);
            assignedToTextView = itemView.findViewById(R.id.assignedToTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            statusSpinner = itemView.findViewById(R.id.statusSpinner);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_card, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.titleTextView.setText(task.getTitle() != null ? task.getTitle() : "Untitled");
        holder.descriptionTextView.setText(task.getDescription() != null ? task.getDescription() : "");
        holder.assignedToTextView.setText(task.getAssignedTo() != null ? "Assigned to: " + task.getAssignedTo() : "");
        // Format timestamp
        long ts = task.getTimestamp();
        if (ts > 0) {
            String formatted = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
                    .format(new Date(ts));
            holder.timestampTextView.setText("Assigned on: " + formatted);
        } else {
            holder.timestampTextView.setText("");
        }

        // Populate spinner with statuses from resources
        String[] statuses = context.getResources().getStringArray(R.array.task_status);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, statuses);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.statusSpinner.setAdapter(spinnerAdapter);

        // Determine index of current status
        int currentIndex = 0;
        String currStatus = task.getStatus() != null ? task.getStatus() : "Pending";
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(currStatus)) {
                currentIndex = i;
                break;
            }
        }

        // Set selection BEFORE setting listener (prevents initial trigger)
        holder.statusSpinner.setSelection(currentIndex, false);

        // Now set listener to handle user changes
        holder.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstCall = true; // defensive, but selection was set before listener, so okay
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Protect against initial unwanted callback
                if (firstCall) {
                    firstCall = false;
                    // If user picked same as current, do nothing
                    if (statuses[pos].equalsIgnoreCase(currStatus)) return;
                }

                String selectedStatus = statuses[pos];

                // If nothing changed, skip
                if (selectedStatus.equalsIgnoreCase(currStatus)) return;

                // Safety: ensure we have a taskId to update
                if (task.getTaskId() == null || task.getTaskId().isEmpty()) {
                    Toast.makeText(context, "Cannot update status: missing taskId", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update Firebase
                DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("tasks");
                tasksRef.child(task.getTaskId()).child("status").setValue(selectedStatus)
                        .addOnSuccessListener(aVoid -> {
                            // update local model (so UI remains consistent)
                            task.setStatus(selectedStatus);
                            // notify this item changed (optional)
                            notifyItemChanged(position);
                            Toast.makeText(context, "Status updated to " + selectedStatus, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            // revert spinner to previous value
                            int revertIndex = 0;
                            for (int i = 0; i < statuses.length; i++) {
                                if (statuses[i].equalsIgnoreCase(currStatus)) {
                                    revertIndex = i; break;
                                }
                            }
                            holder.statusSpinner.setSelection(revertIndex, false);
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
