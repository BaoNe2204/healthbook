package com.example.healthbook.data.models;

import java.io.Serializable;

public class Doctor implements Serializable {
    private String id;
    private String name;
    private String specialty;
    private String hospital;
    private double rating;
    private int reviewCount;
    private int imageResId;
    private String imageUrl;
    
    private int experience;
    private int price;
    private String description;

    public Doctor() {}

    public Doctor(String id, String name, String specialty, String hospital, double rating, int reviewCount, int imageResId, int experience, int price, String description) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.hospital = hospital;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.imageResId = imageResId;
        this.experience = experience;
        this.price = price;
        this.description = description;
    }

    private int consultationFee;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getHospital() { return hospital; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }
    public int getExperience() { return experience; }
    public int getPrice() { return price; }
    public int getConsultationFee() { return consultationFee > 0 ? consultationFee : (price > 0 ? price : 200000); }
    public String getDescription() { return description; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public void setRating(double rating) { this.rating = rating; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setPrice(int price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
