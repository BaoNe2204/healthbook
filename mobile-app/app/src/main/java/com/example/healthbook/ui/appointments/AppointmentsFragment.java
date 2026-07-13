package com.example.healthbook.ui.appointments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.adapters.AppointmentAdapter;
import com.example.healthbook.data.ApiRepository;
import com.example.healthbook.data.models.Appointment;
import java.util.List;
import android.util.Log;

public class AppointmentsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        
        RecyclerView rvAppointments = view.findViewById(R.id.rvAppointments);
        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        ApiRepository repo = new ApiRepository();
        repo.getAppointments(new ApiRepository.Callback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> result) {
                rvAppointments.setAdapter(new AppointmentAdapter(result));
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("Appointments", "Failed to load", e);
            }
        });
        
        return view;
    }
}
