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
import com.example.healthbook.data.ApiRepository;
import com.example.healthbook.data.models.Doctor;
import com.example.healthbook.data.models.Specialty;
import com.example.healthbook.data.models.Hospital;
import java.util.List;
import android.util.Log;
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

        // Fetch data from Firestore
        ApiRepository repo = new ApiRepository();
        
        repo.getDoctors(new ApiRepository.Callback<List<Doctor>>() {
            @Override
            public void onSuccess(List<Doctor> result) {
                DoctorAdapter doctorAdapter = new DoctorAdapter(result, doctor -> {
                    Navigation.findNavController(view).navigate(R.id.timeSelectionFragment);
                });
                if (tabLayout.getSelectedTabPosition() == 0) {
                    rvList.setAdapter(doctorAdapter);
                }
                
                // Update tab selection logic
                setupTabSelection(tabLayout, rvList, tvSuggestedTitle, doctorAdapter, null, null);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("DoctorSearch", "Failed", e);
            }
        });

        repo.getSpecialties(new ApiRepository.Callback<List<Specialty>>() {
            @Override
            public void onSuccess(List<Specialty> result) {
                SpecialtyAdapter specialtyAdapter = new SpecialtyAdapter(result);
                if (tabLayout.getSelectedTabPosition() == 1) {
                    rvList.setAdapter(specialtyAdapter);
                }
                setupTabSelection(tabLayout, rvList, tvSuggestedTitle, null, specialtyAdapter, null);
            }
            @Override
            public void onFailure(Exception e) {}
        });

        repo.getHospitals(new ApiRepository.Callback<List<Hospital>>() {
            @Override
            public void onSuccess(List<Hospital> result) {
                HospitalAdapter hospitalAdapter = new HospitalAdapter(result);
                if (tabLayout.getSelectedTabPosition() == 2) {
                    rvList.setAdapter(hospitalAdapter);
                }
                setupTabSelection(tabLayout, rvList, tvSuggestedTitle, null, null, hospitalAdapter);
            }
            @Override
            public void onFailure(Exception e) {}
        });

        // Default to tab 0 layout manager
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));



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

    private DoctorAdapter currentDoctorAdapter;
    private SpecialtyAdapter currentSpecialtyAdapter;
    private HospitalAdapter currentHospitalAdapter;
    private TabLayout.OnTabSelectedListener currentTabListener;

    private void setupTabSelection(TabLayout tabLayout, RecyclerView rvList, TextView tvSuggestedTitle, DoctorAdapter dAdapter, SpecialtyAdapter sAdapter, HospitalAdapter hAdapter) {
        if (dAdapter != null) currentDoctorAdapter = dAdapter;
        if (sAdapter != null) currentSpecialtyAdapter = sAdapter;
        if (hAdapter != null) currentHospitalAdapter = hAdapter;

        if (currentTabListener != null) {
            tabLayout.removeOnTabSelectedListener(currentTabListener);
        }

        currentTabListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tvSuggestedTitle.setText("Bác sĩ gợi ý cho bạn");
                        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                        if (currentDoctorAdapter != null) rvList.setAdapter(currentDoctorAdapter);
                        break;
                    case 1:
                        tvSuggestedTitle.setText("Chuyên khoa phổ biến");
                        rvList.setLayoutManager(new GridLayoutManager(getContext(), 4));
                        if (currentSpecialtyAdapter != null) rvList.setAdapter(currentSpecialtyAdapter);
                        break;
                    case 2:
                        tvSuggestedTitle.setText("Bệnh viện nổi bật");
                        rvList.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        if (currentHospitalAdapter != null) rvList.setAdapter(currentHospitalAdapter);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        };
        tabLayout.addOnTabSelectedListener(currentTabListener);
    }
}
