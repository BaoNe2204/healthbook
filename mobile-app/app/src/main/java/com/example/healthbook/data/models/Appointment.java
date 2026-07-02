package com.example.healthbook.data.models;

public class Appointment {
    private String id;
    private Doctor doctor;
    private String date;
    private String time;
    private String status; // Sắp tới, Đã qua
    private String type; // Khám tại bệnh viện, Khám online

    public Appointment(String id, Doctor doctor, String date, String time, String status, String type) {
        this.id = id;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.status = status;
        this.type = type;
    }

    public String getId() { return id; }
    public Doctor getDoctor() { return doctor; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getType() { return type; }
}
