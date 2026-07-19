package com.example.healthbook.ui.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.DoctorAppointmentAdapter;
import com.example.healthbook.data.models.Appointment;
import com.example.healthbook.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorAppointmentsFragment extends Fragment {

    private RecyclerView rvAppointments;
    private DoctorAppointmentAdapter adapter;
    private List<Appointment> appointmentsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_appointments, container, false);

        rvAppointments = view.findViewById(R.id.rvDoctorAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new DoctorAppointmentAdapter(appointmentsList, this::loadAppointments);
        rvAppointments.setAdapter(adapter);

        loadAppointments();

        return view;
    }

    private void loadAppointments() {
        RetrofitClient.getInstance().getApiService().getDoctorAppointments().enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appointmentsList.clear();
                    appointmentsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách lịch hẹn.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}