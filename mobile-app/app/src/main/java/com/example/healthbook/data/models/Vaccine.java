package com.example.healthbook.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Vaccine implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private int price;

    @SerializedName("disease")
    private String disease;

    @SerializedName("requiredDoses")
    private int requiredDoses;

    @SerializedName("ageGroup")
    private String ageGroup;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public int getRequiredDoses() { return requiredDoses; }
    public void setRequiredDoses(int requiredDoses) { this.requiredDoses = requiredDoses; }

    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }
}
