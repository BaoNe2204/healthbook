package com.example.healthbook.data.models;

import com.google.gson.annotations.SerializedName;

public class Appointment implements java.io.Serializable {
    private String id;
    
    private Doctor doctor;
    
    @SerializedName("doctor_id")
    private String doctor_id;
    
    @SerializedName("doctorName")
    private String doctorName;
    
    @SerializedName("specialty")
    private String specialty;
    
    @SerializedName("hospital")
    private String hospital;

    @SerializedName("appointment_date")
    private String date;

    @SerializedName("appointment_time")
    private String time;

    @SerializedName("status")
    private String status; // Sắp tới, Đã qua

    @SerializedName("type")
    private String type; // Khám tại bệnh viện, Khám online

    @SerializedName("patient_id")
    private String patient_id;

    @SerializedName("patient_name")
    private String patient_name;

    @SerializedName("patient_phone")
    private String patient_phone;

    @SerializedName("patient_dob")
    private String patient_dob;

    @SerializedName("patient_gender")
    private String patient_gender;

    public Appointment() {}

    public Appointment(String id, Doctor doctor, String date, String time, String status, String type) {
        this.id = id;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.status = status;
        this.type = type;
        if (doctor != null) {
            this.doctor_id = doctor.getId();
            this.doctorName = doctor.getName();
            this.specialty = doctor.getSpecialty();
            this.hospital = doctor.getHospital();
        }
    }

    public String getId() { return id; }
    
    public Doctor getDoctor() { 
        if (doctor == null && doctorName != null) {
            doctor = new Doctor(doctor_id, doctorName, specialty, hospital, 4.8, 100, 0, 5, 300000, "");
        }
        return doctor; 
    }
    
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getType() { return type; }

    public void setId(String id) { this.id = id; }
    public void setDoctor(Doctor doctor) { 
        this.doctor = doctor; 
        if (doctor != null) {
            this.doctor_id = doctor.getId();
            this.doctorName = doctor.getName();
            this.specialty = doctor.getSpecialty();
            this.hospital = doctor.getHospital();
        }
    }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setStatus(String status) { this.status = status; }
    public void setType(String type) { this.type = type; }
    
    public String getDoctorId() { return doctor_id; }
    public void setDoctorId(String doctor_id) { this.doctor_id = doctor_id; }
    
    public String getPatient_name() { return patient_name; }
    public void setPatient_name(String patient_name) { this.patient_name = patient_name; }
    public String getPatient_phone() { return patient_phone; }
    public void setPatient_phone(String patient_phone) { this.patient_phone = patient_phone; }
    public String getPatient_dob() { return patient_dob; }
    public void setPatient_dob(String patient_dob) { this.patient_dob = patient_dob; }
    public String getPatient_gender() { return patient_gender; }
    public void setPatient_gender(String patient_gender) { this.patient_gender = patient_gender; }
    public String getPatient_id() { return patient_id; }
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }
}

