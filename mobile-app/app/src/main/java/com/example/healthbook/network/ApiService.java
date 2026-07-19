package com.example.healthbook.network;

import com.example.healthbook.data.models.Appointment;
import com.example.healthbook.data.models.Doctor;
import com.example.healthbook.data.models.Hospital;
import com.example.healthbook.data.models.Specialty;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("/api/doctors")
    Call<List<Doctor>> getDoctors();

    @GET("/api/specialties")
    Call<List<Specialty>> getSpecialties();

    @GET("/api/hospitals")
    Call<List<Hospital>> getHospitals();

    @GET("/api/appointments")
    Call<List<Appointment>> getAppointments();

    @POST("/api/appointments")
    Call<Appointment> createAppointment(@Body Appointment appointment);

    @POST("/api/users/register")
    Call<Void> registerUser(@Body Map<String, String> body);

    @GET("/api/users/profile")
    Call<com.example.healthbook.data.models.UserProfile> getUserProfile();

    @retrofit2.http.PUT("/api/users/profile")
    Call<Void> updateUserProfile(@Body com.example.healthbook.data.models.UserProfile profile);
}
