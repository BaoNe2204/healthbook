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

    @GET("/api/doctors/{id}/schedule")
    Call<List<String>> getDoctorSchedule(@retrofit2.http.Path("id") String doctorId, @retrofit2.http.Query("date") String date);

    @GET("/api/specialties")
    Call<List<Specialty>> getSpecialties();

    @GET("/api/hospitals")
    Call<List<Hospital>> getHospitals();

    @GET("/api/clinics")
    Call<List<com.example.healthbook.data.models.Clinic>> getClinics();

    @GET("/api/appointments")
    Call<List<Appointment>> getAppointments();

    @POST("/api/appointments")
    Call<Appointment> createAppointment(@Body Appointment appointment);

    @retrofit2.http.PUT("/api/appointments/{id}/cancel")
    Call<Void> cancelAppointment(@retrofit2.http.Path("id") String id);

    @POST("/api/users/register")
    Call<Void> registerUser(@Body Map<String, String> body);

    @GET("/api/users/profile")
    Call<com.example.healthbook.data.models.UserProfile> getUserProfile();

    @retrofit2.http.PUT("/api/users/profile")
    Call<Void> updateUserProfile(@Body com.example.healthbook.data.models.UserProfile profile);

    @retrofit2.http.Multipart
    @POST("/api/users/upload-avatar")
    Call<Map<String, String>> uploadAvatar(@retrofit2.http.Part okhttp3.MultipartBody.Part avatar);

    @GET("/api/users/medical-records")
    Call<List<com.example.healthbook.data.models.MedicalRecord>> getMedicalRecords();

    @GET("/api/vaccines")
    Call<List<com.example.healthbook.data.models.Vaccine>> getVaccines();

    @POST("/api/vaccine-bookings")
    Call<Void> createVaccineBooking(@Body Map<String, Object> bookingData);

    @GET("/api/vaccine-bookings")
    Call<List<com.example.healthbook.data.models.VaccineBooking>> getVaccineBookings();

    @GET("/api/users/notifications")
    Call<List<com.example.healthbook.data.models.NotificationItem>> getNotifications();

    // Doctor Panel Endpoints
    @GET("/api/doctor/appointments")
    Call<List<Appointment>> getDoctorAppointments();

    @retrofit2.http.PUT("/api/doctor/appointments/{id}/status")
    Call<Void> updateAppointmentStatus(@retrofit2.http.Path("id") String id, @Body Map<String, String> body);

    @POST("/api/doctor/schedule")
    Call<Void> updateDoctorSchedule(@Body Map<String, Object> body);

    @POST("/api/doctor/medical-records")
    Call<Void> createMedicalRecord(@Body Map<String, Object> body);

    // Admin Panel Endpoints
    @GET("/api/admin/dashboard")
    Call<Map<String, Object>> getAdminDashboard();

    @GET("/api/admin/users")
    Call<List<com.example.healthbook.data.models.UserProfile>> getAdminUsers();

    @retrofit2.http.PUT("/api/admin/users/{uid}/ban")
    Call<Void> banUser(@retrofit2.http.Path("uid") String uid, @Body Map<String, Boolean> body);

    @retrofit2.http.PUT("/api/admin/users/{uid}/role")
    Call<Void> updateUserRole(@retrofit2.http.Path("uid") String uid, @Body Map<String, String> body);

    @POST("/api/admin/hospitals")
    Call<Void> createHospital(@Body Map<String, Object> body);

    @retrofit2.http.DELETE("/api/admin/hospitals/{id}")
    Call<Void> deleteHospital(@retrofit2.http.Path("id") String id);

    @POST("/api/admin/specialties")
    Call<Void> createSpecialty(@Body Map<String, Object> body);

    @retrofit2.http.DELETE("/api/admin/specialties/{id}")
    Call<Void> deleteSpecialty(@retrofit2.http.Path("id") String id);

    // Advanced Doctor Features
    @GET("/api/doctor/patients/{patientId}/medical-records")
    Call<List<com.example.healthbook.data.models.MedicalRecord>> getPatientMedicalRecords(@retrofit2.http.Path("patientId") String patientId);

    @GET("/api/doctor/dashboard")
    Call<Map<String, Object>> getDoctorDashboard();

    @retrofit2.http.PUT("/api/doctor/profile/fee")
    Call<Void> updateConsultationFee(@Body Map<String, Integer> body);

    @GET("/api/doctor/reviews")
    Call<List<com.example.healthbook.data.models.Review>> getDoctorReviews();

    @POST("/api/users/reviews")
    Call<Void> submitReview(@Body com.example.healthbook.data.models.Review review);

    @GET("/api/doctor/my-patients")
    Call<List<com.example.healthbook.data.models.Patient>> getDoctorMyPatients();
}
