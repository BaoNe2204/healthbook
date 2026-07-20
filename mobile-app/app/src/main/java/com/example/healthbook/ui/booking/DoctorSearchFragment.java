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
                    Bundle args = new Bundle();
                    args.putSerializable("doctor", doctor);
                    Navigation.findNavController(view).navigate(R.id.doctorProfileFragment, args);
                });
                if (tabLayout.getSelectedTabPosition() == 0) {
                    rvList.setAdapter(doctorAdapter);
                }
                
                // Update tab selection logic
                setupTabSelection(tabLayout, rvList, tvSuggestedTitle, doctorAdapter, null, null, null);
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
                setupTabSelection(tabLayout, rvList, tvSuggestedTitle, null, specialtyAdapter, null, null);
            }
            @Override
            public void onFailure(Exception e) {}
        });

        repo.getClinics(new ApiRepository.Callback<List<com.example.healthbook.data.models.Clinic>>() {
            @Override
            public void onSuccess(List<com.example.healthbook.data.models.Clinic> result) {
                com.example.healthbook.adapters.ClinicSearchAdapter clinicAdapter = new com.example.healthbook.adapters.ClinicSearchAdapter(result);
                if (tabLayout.getSelectedTabPosition() == 2) {
                    rvList.setAdapter(clinicAdapter);
                }
                setupTabSelection(tabLayout, rvList, tvSuggestedTitle, null, null, clinicAdapter, null);
            }
            @Override
            public void onFailure(Exception e) {}
        });

        repo.getHospitals(new ApiRepository.Callback<List<Hospital>>() {
            @Override
            public void onSuccess(List<Hospital> result) {
                HospitalAdapter hospitalAdapter = new HospitalAdapter(result);
                if (tabLayout.getSelectedTabPosition() == 3) {
                    rvList.setAdapter(hospitalAdapter);
                }
                setupTabSelection(tabLayout, rvList, tvSuggestedTitle, null, null, null, hospitalAdapter);
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
    private com.example.healthbook.adapters.ClinicSearchAdapter currentClinicAdapter;
    private HospitalAdapter currentHospitalAdapter;
    private TabLayout.OnTabSelectedListener currentTabListener;

    private void refreshCurrentTab(TabLayout tabLayout, RecyclerView rvList, TextView tvSuggestedTitle) {
        if (getContext() == null) return;
        int pos = tabLayout.getSelectedTabPosition();
        switch (pos) {
            case 0:
                tvSuggestedTitle.setText("Bác sĩ gợi ý cho bạn");
                rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                rvList.setAdapter(currentDoctorAdapter);
                break;
            case 1:
                tvSuggestedTitle.setText("Chuyên khoa phổ biến");
                rvList.setLayoutManager(new GridLayoutManager(getContext(), 4));
                rvList.setAdapter(currentSpecialtyAdapter);
                break;
            case 2:
                tvSuggestedTitle.setText("Phòng khám nổi bật");
                rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                rvList.setAdapter(currentClinicAdapter);
                break;
            case 3:
                tvSuggestedTitle.setText("Bệnh viện nổi bật");
                rvList.setLayoutManager(new GridLayoutManager(getContext(), 2));
                rvList.setAdapter(currentHospitalAdapter);
                break;
        }
    }

    private void setupTabSelection(TabLayout tabLayout, RecyclerView rvList, TextView tvSuggestedTitle, DoctorAdapter dAdapter, SpecialtyAdapter sAdapter, com.example.healthbook.adapters.ClinicSearchAdapter cAdapter, HospitalAdapter hAdapter) {
        if (dAdapter != null) currentDoctorAdapter = dAdapter;
        if (sAdapter != null) currentSpecialtyAdapter = sAdapter;
        if (cAdapter != null) currentClinicAdapter = cAdapter;
        if (hAdapter != null) currentHospitalAdapter = hAdapter;

        refreshCurrentTab(tabLayout, rvList, tvSuggestedTitle);

        if (currentTabListener != null) {
            tabLayout.removeOnTabSelectedListener(currentTabListener);
        }

        currentTabListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshCurrentTab(tabLayout, rvList, tvSuggestedTitle);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        };
        tabLayout.addOnTabSelectedListener(currentTabListener);
    }
}
