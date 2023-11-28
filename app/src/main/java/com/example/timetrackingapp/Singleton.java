package com.example.timetrackingapp;

public class Singleton {

    private static Singleton instance;
    private String clickedCategoryName;

    private void CategorySingleton() {
        // Private constructor to prevent instantiation
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public String getClickedCategoryName() {
        return clickedCategoryName;
    }

    public void setClickedCategoryName(String categoryName) {
        this.clickedCategoryName = categoryName;
    }
}
