package com.example.healthbook.data;

import com.example.healthbook.data.models.Appointment;
import com.example.healthbook.data.models.Doctor;
import com.example.healthbook.data.models.Hospital;
import com.example.healthbook.data.models.Specialty;
import com.example.healthbook.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ApiRepository {

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public void seedDataIfNeeded() {
        // Node.js backend handles seeding or we can assume it's already seeded
    }

    public void getDoctors(Callback<List<Doctor>> callback) {
        try {
            callback.onSuccess(com.example.healthbook.data.MockData.getDoctors());
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void getSpecialties(Callback<List<Specialty>> callback) {
        try {
            callback.onSuccess(com.example.healthbook.data.MockData.getSpecialties());
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void getHospitals(Callback<List<Hospital>> callback) {
        try {
            callback.onSuccess(com.example.healthbook.data.MockData.getHospitals());
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void getAppointments(Callback<List<Appointment>> callback) {
        try {
            callback.onSuccess(com.example.healthbook.data.MockData.getAppointments());
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void getUserProfile(Callback<com.example.healthbook.data.models.UserProfile> callback) {
        RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new retrofit2.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onResponse(Call<com.example.healthbook.data.models.UserProfile> call, Response<com.example.healthbook.data.models.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("API Error"));
                }
            }

            @Override
            public void onFailure(Call<com.example.healthbook.data.models.UserProfile> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }

    public void getClinics(Callback<List<com.example.healthbook.data.models.Clinic>> callback) {
        // Since backend might not have this yet, return from MockData
        try {
            callback.onSuccess(com.example.healthbook.data.MockData.getClinics());
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
}
