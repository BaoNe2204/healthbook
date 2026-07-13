package com.example.healthbook.data.models;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private String hospital;
    private double rating;
    private int reviewCount;
    private int imageResId;

    public Doctor() {}

    public Doctor(String id, String name, String specialty, String hospital, double rating, int reviewCount, int imageResId) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.hospital = hospital;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.imageResId = imageResId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getHospital() { return hospital; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public int getImageResId() { return imageResId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public void setRating(double rating) { this.rating = rating; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
