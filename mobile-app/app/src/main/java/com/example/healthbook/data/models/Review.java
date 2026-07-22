package com.example.healthbook.data.models;

public class Review {
    private String id;
    private String patient_id;
    private String doctor_id;
    private float rating;
    private String comment;
    private String created_at;

    private String appointment_id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPatient_id() { return patient_id; }
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }

    public String getDoctor_id() { return doctor_id; }
    public void setDoctor_id(String doctor_id) { this.doctor_id = doctor_id; }

    public String getAppointment_id() { return appointment_id; }
    public void setAppointment_id(String appointment_id) { this.appointment_id = appointment_id; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
