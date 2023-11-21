package com.example.timetrackingapp;

public class Category_modal {
    private String id;  // Add an ID field
    private String name;

    // Required no-argument constructor for Firebase Firestore deserialization
    public Category_modal() {
    }

    public Category_modal(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    } public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
