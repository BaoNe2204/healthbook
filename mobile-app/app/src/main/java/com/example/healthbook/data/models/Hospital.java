package com.example.healthbook.data.models;

public class Hospital {
    private String id;
    private String name;
    private String address;

    public Hospital() {}

    public Hospital(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
}
