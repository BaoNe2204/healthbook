package com.example.healthbook.data.models;

public class Hospital {
    private String id;
    private String name;
    private String address;
    private int imageResId;
    private String imageUrl;

    public Hospital() {}

    public Hospital(String id, String name, String address, int imageResId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.imageResId = imageResId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
