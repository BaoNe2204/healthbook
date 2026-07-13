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
import com.example.healthbook.data.ApiRepository;
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

        String specialtyNameTemp = "Chuyên khoa";
        if (getArguments() != null) {
            specialtyNameTemp = getArguments().getString("specialtyName", "Chuyên khoa");
        }
        final String finalSpecialtyName = specialtyNameTemp;

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSpecialtyName = view.findViewById(R.id.tvSpecialtyName);
        tvTitle.setText(finalSpecialtyName);
        tvSpecialtyName.setText(finalSpecialtyName);

        View btnChat = view.findViewById(R.id.btnChat);
        btnChat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng chat đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        // Filter doctors by specialty
        ApiRepository repo = new ApiRepository();
        repo.getDoctors(new ApiRepository.Callback<List<Doctor>>() {
            @Override
            public void onSuccess(List<Doctor> allDoctors) {
                List<Doctor> filteredDoctors = new ArrayList<>();
                for (Doctor d : allDoctors) {
                    if (d.getSpecialty() != null && (d.getSpecialty().toLowerCase().contains(finalSpecialtyName.toLowerCase()) || finalSpecialtyName.toLowerCase().contains(d.getSpecialty().toLowerCase()))) {
                        filteredDoctors.add(d);
                    }
                }
                
                if (filteredDoctors.isEmpty()) {
                    filteredDoctors.addAll(allDoctors);
                }

                RecyclerView rvDoctors = view.findViewById(R.id.rvDoctors);
                rvDoctors.setLayoutManager(new LinearLayoutManager(getContext()));
                rvDoctors.setAdapter(new DoctorAdapter(filteredDoctors, doctor -> {
                    Navigation.findNavController(view).navigate(R.id.timeSelectionFragment);
                }));
            }
            @Override
            public void onFailure(Exception e) {}
        });



        return view;
    }
}
