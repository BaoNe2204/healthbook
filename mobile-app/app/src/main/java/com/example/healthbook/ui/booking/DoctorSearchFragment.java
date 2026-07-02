package com.example.healthbook.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;
import com.example.healthbook.adapters.DoctorAdapter;
import com.example.healthbook.adapters.SpecialtyAdapter;
import com.example.healthbook.adapters.HospitalAdapter;
import com.example.healthbook.data.MockData;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;

public class DoctorSearchFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_search, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        RecyclerView rvList = view.findViewById(R.id.rvDoctors);
        TextView tvSuggestedTitle = view.findViewById(R.id.tvSuggestedTitle);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        // Define adapters
        DoctorAdapter doctorAdapter = new DoctorAdapter(MockData.getDoctors(), doctor -> {
            Navigation.findNavController(view).navigate(R.id.timeSelectionFragment);
        });
        SpecialtyAdapter specialtyAdapter = new SpecialtyAdapter(MockData.getSpecialties());
        HospitalAdapter hospitalAdapter = new HospitalAdapter(MockData.getHospitals());

        // Default to tab 0
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(doctorAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tvSuggestedTitle.setText("Bác sĩ gợi ý cho bạn");
                        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvList.setAdapter(doctorAdapter);
                        break;
                    case 1:
                        tvSuggestedTitle.setText("Chuyên khoa phổ biến");
                        rvList.setLayoutManager(new GridLayoutManager(getContext(), 4));
                        rvList.setAdapter(specialtyAdapter);
                        break;
                    case 2:
                        tvSuggestedTitle.setText("Bệnh viện nổi bật");
                        rvList.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        rvList.setAdapter(hospitalAdapter);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Pre-select tab if argument is passed
        if (getArguments() != null) {
            int tabIndex = getArguments().getInt("tabIndex", 0);
            TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
            if (tab != null) {
                tab.select();
            }
        }

        return view;
    }
}
