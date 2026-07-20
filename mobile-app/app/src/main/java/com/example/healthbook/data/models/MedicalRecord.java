package com.example.healthbook.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class MedicalRecord implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("appointment_id")
    private int appointmentId;

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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

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
