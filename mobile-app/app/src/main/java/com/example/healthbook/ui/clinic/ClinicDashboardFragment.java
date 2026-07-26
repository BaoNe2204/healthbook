package com.example.healthbook.ui.clinic;

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
import com.example.healthbook.adapters.ClinicAppointmentAdapter;
import com.example.healthbook.data.ApiRepository;
import com.example.healthbook.data.models.Appointment;

import java.util.ArrayList;
import java.util.List;

public class ClinicDashboardFragment extends Fragment {

    private RecyclerView rvAppointments;
    private ClinicAppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clinic_dashboard, container, false);

        rvAppointments = view.findViewById(R.id.rvClinicAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new ClinicAppointmentAdapter(appointmentList, this::loadAppointments);
        rvAppointments.setAdapter(adapter);

        loadAppointments();

        return view;
    }

    private void loadAppointments() {
        new ApiRepository().getClinicAppointments(new ApiRepository.Callback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> result) {
                if (getActivity() == null) return;
                appointmentList.clear();
                if (result != null) {
                    appointmentList.addAll(result);
                }
                adapter.notifyDataSetChanged();
                if (appointmentList.isEmpty()) {
                    Toast.makeText(getContext(), "Không có đơn đặt lịch nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() == null) return;
                Toast.makeText(getContext(), "Lỗi tải đơn khám: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
