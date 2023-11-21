package com.example.timetrackingapp;

public class Category_modal {
    private String name;


    // Required no-argument constructor for Firebase Firestore deserialization
    public Category_modal() {
    }
    public Category_modal(String name) {
        this.name = name;

    }



    public String getName() {
        return name;
    }
}
