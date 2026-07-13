package com.example.healthbook.data.models;

public class Specialty {
    private String id;
    private String name;
    private int iconResId;

    public Specialty() {}

    public Specialty(String id, String name, int iconResId) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getIconResId() { return iconResId; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
}
