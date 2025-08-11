package com.example.teamtaskmanager;

public class User {
    public String email;
    public String role;

    public User() {} // Default constructor for Firebase

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }
}
