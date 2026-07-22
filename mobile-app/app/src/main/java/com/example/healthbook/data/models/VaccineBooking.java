package com.example.healthbook.data.models;

public class VaccineBooking {
    private String id;
    private String patient_id;
    private String vaccine_id;
    private String vaccine_name;
    private double price;
    private String appointment_date;
    private String appointment_time;
    private String status;
    private String created_at;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatient_id() { return patient_id; }
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }

    public String getVaccine_id() { return vaccine_id; }
    public void setVaccine_id(String vaccine_id) { this.vaccine_id = vaccine_id; }

    public String getVaccine_name() { return vaccine_name; }
    public void setVaccine_name(String vaccine_name) { this.vaccine_name = vaccine_name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getAppointment_date() { return appointment_date; }
    public void setAppointment_date(String appointment_date) { this.appointment_date = appointment_date; }

    public String getAppointment_time() { return appointment_time; }
    public void setAppointment_time(String appointment_time) { this.appointment_time = appointment_time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
