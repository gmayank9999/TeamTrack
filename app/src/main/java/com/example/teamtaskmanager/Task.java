package com.example.teamtaskmanager;

public class Task {
    public String taskId;
    public String title;
    public String description;
    public String assignedTo;      // Employee name (for display)
    public String assignedToId;    // Employee UID (for filtering)
    public long timestamp;
    public String status;

    // Required empty constructor for Firebase
    public Task() {
    }

    public Task(String taskId, String title, String description, String assignedTo,
                String assignedToId, long timestamp, String status) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.assignedToId = assignedToId;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getAssignedToId() { return assignedToId; }
    public void setAssignedToId(String assignedToId) { this.assignedToId = assignedToId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
