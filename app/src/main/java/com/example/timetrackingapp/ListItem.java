package com.example.timetrackingapp;

public class ListItem {
    private int imageResource;
    private String text;
    private String documentId;

    public ListItem(int imageResource, String text, String documentId) {
        this.imageResource = imageResource;
        this.text = text;
        this.documentId = documentId;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDocumentId() {
        return documentId;
    }
}

