package com.example.healthbook.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Patient implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("gender")
    private String gender;

    @SerializedName("dob")
    private String dob;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
