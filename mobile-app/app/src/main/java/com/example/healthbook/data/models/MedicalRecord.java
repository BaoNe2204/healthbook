package com.example.healthbook.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class MedicalRecord implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("doctor_id")
    private String doctorId;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("appointment_id")
    private String appointmentId;

    @SerializedName("diagnosis")
    private String diagnosis;

    @SerializedName("prescription")
    private String prescription;

    @SerializedName("notes")
    private String notes;

    @SerializedName("doctorName")
    private String doctorName;

    @SerializedName("specialty")
    private String specialty;

    @SerializedName("hospital")
    private String hospital;

    @SerializedName("appointment_date")
    private String appointmentDate;

    @SerializedName("appointment_time")
    private String appointmentTime;

    @SerializedName("created_at")
    private String created_at;

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
}
