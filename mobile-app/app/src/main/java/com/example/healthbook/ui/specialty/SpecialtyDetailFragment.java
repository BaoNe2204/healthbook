package com.example.healthbook.ui.specialty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import com.example.healthbook.adapters.DoctorAdapter;
import com.example.healthbook.data.MockData;
import com.example.healthbook.data.models.Doctor;
import java.util.ArrayList;
import java.util.List;

public class SpecialtyDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_specialty_detail, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        String specialtyName = "Chuyên khoa";
        if (getArguments() != null) {
            specialtyName = getArguments().getString("specialtyName", "Chuyên khoa");
        }

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSpecialtyName = view.findViewById(R.id.tvSpecialtyName);
        tvTitle.setText(specialtyName);
        tvSpecialtyName.setText(specialtyName);

        View btnChat = view.findViewById(R.id.btnChat);
        btnChat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng chat đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        // Filter doctors by specialty
        List<Doctor> allDoctors = MockData.getDoctors();
        List<Doctor> filteredDoctors = new ArrayList<>();
        for (Doctor d : allDoctors) {
            if (d.getSpecialty().toLowerCase().contains(specialtyName.toLowerCase()) || specialtyName.toLowerCase().contains(d.getSpecialty().toLowerCase())) {
                filteredDoctors.add(d);
            }
        }
        
        // If no matches (mock data is limited), just show all to avoid empty list for demo
        if (filteredDoctors.isEmpty()) {
            filteredDoctors.addAll(allDoctors);
        }

        RecyclerView rvDoctors = view.findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDoctors.setAdapter(new DoctorAdapter(filteredDoctors, doctor -> {
            Navigation.findNavController(view).navigate(R.id.timeSelectionFragment);
        }));

        return view;
    }
}
