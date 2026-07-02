package com.example.healthbook.ui.home;

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
import androidx.navigation.Navigation;
import com.example.healthbook.adapters.HospitalAdapter;
import com.example.healthbook.adapters.SpecialtyAdapter;
import com.example.healthbook.data.MockData;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rvSpecialties = view.findViewById(R.id.rvSpecialties);
        rvSpecialties.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSpecialties.setAdapter(new SpecialtyAdapter(MockData.getSpecialties()));

        RecyclerView rvHospitals = view.findViewById(R.id.rvHospitals);
        rvHospitals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvHospitals.setAdapter(new HospitalAdapter(MockData.getHospitals()));

        view.findViewById(R.id.btnBookAppointment).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.doctorSearchFragment);
        });

        view.findViewById(R.id.btnOnlineConsultation).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.onlineConsultationFragment);
        });

        view.findViewById(R.id.tvSeeAllSpecialties).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("tabIndex", 1);
            Navigation.findNavController(v).navigate(R.id.doctorSearchFragment, args);
        });

        view.findViewById(R.id.tvSeeAllHospitals).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("tabIndex", 2);
            Navigation.findNavController(v).navigate(R.id.doctorSearchFragment, args);
        });

        return view;
    }
}
