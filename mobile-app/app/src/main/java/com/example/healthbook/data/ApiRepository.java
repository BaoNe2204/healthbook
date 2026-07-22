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
        RetrofitClient.getInstance().getApiService().getDoctors().enqueue(new retrofit2.Callback<List<Doctor>>() {
            @Override
            public void onResponse(Call<List<Doctor>> call, Response<List<Doctor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("API Error fetching doctors"));
                }
            }

            @Override
            public void onFailure(Call<List<Doctor>> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }

    public void getSpecialties(Callback<List<Specialty>> callback) {
        RetrofitClient.getInstance().getApiService().getSpecialties().enqueue(new retrofit2.Callback<List<Specialty>>() {
            @Override
            public void onResponse(Call<List<Specialty>> call, Response<List<Specialty>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("API Error fetching specialties"));
                }
            }

            @Override
            public void onFailure(Call<List<Specialty>> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }

    public void getHospitals(Callback<List<Hospital>> callback) {
        RetrofitClient.getInstance().getApiService().getHospitals().enqueue(new retrofit2.Callback<List<Hospital>>() {
            @Override
            public void onResponse(Call<List<Hospital>> call, Response<List<Hospital>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("API Error fetching hospitals"));
                }
            }

            @Override
            public void onFailure(Call<List<Hospital>> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }

    public void getAppointments(Callback<List<Appointment>> callback) {
        RetrofitClient.getInstance().getApiService().getAppointments().enqueue(new retrofit2.Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("API Error fetching appointments"));
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }

    public void cancelAppointment(String id, Callback<Void> callback) {
        RetrofitClient.getInstance().getApiService().cancelAppointment(id).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("API Error cancelling appointment"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
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
        RetrofitClient.getInstance().getApiService().getClinics().enqueue(new retrofit2.Callback<List<com.example.healthbook.data.models.Clinic>>() {
            @Override
            public void onResponse(Call<List<com.example.healthbook.data.models.Clinic>> call, Response<List<com.example.healthbook.data.models.Clinic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("API Error fetching clinics"));
                }
            }

            @Override
            public void onFailure(Call<List<com.example.healthbook.data.models.Clinic>> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }
}
