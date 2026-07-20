package com.example.healthbook.data.models;

public class Appointment implements java.io.Serializable {
    private String id;
    private Doctor doctor;
    private String date;
    private String time;
    private String status; // Sắp tới, Đã qua
    private String type; // Khám tại bệnh viện, Khám online
    private String patient_name;
    private String patient_phone;
    private String patient_dob;
    private String patient_gender;

    public Appointment() {}

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

    public void setId(String id) { this.id = id; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setStatus(String status) { this.status = status; }
    public void setType(String type) { this.type = type; }
    public String getPatient_name() { return patient_name; }
    public void setPatient_name(String patient_name) { this.patient_name = patient_name; }
    public String getPatient_phone() { return patient_phone; }
    public void setPatient_phone(String patient_phone) { this.patient_phone = patient_phone; }
    public String getPatient_dob() { return patient_dob; }
    public void setPatient_dob(String patient_dob) { this.patient_dob = patient_dob; }
    public String getPatient_gender() { return patient_gender; }
    public void setPatient_gender(String patient_gender) { this.patient_gender = patient_gender; }
}
