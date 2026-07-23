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
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.healthbook.adapters.HospitalAdapter;
import com.example.healthbook.adapters.SpecialtyAdapter;
import com.example.healthbook.data.ApiRepository;
import com.example.healthbook.data.models.Hospital;
import com.example.healthbook.data.models.Specialty;
import java.util.List;
import android.util.Log;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView tvUserName = view.findViewById(R.id.tvUserName);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            tvUserName.setText(user.getDisplayName());
        }

        // Fetch latest profile from server to update display name instantly
        
        // Load Banner Image
        android.widget.ImageView ivBanner = view.findViewById(R.id.ivBanner);
        if (ivBanner != null) {
            ivBanner.setPadding(0, 0, 0, 0);
            ivBanner.clearColorFilter();
            com.bumptech.glide.Glide.with(this)
                    .load("https://images.unsplash.com/photo-1505751172876-fa1923c5c528?q=80&w=400&auto=format&fit=crop")
                    .centerCrop()
                    .into(ivBanner);
        }

        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new retrofit2.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, retrofit2.Response<com.example.healthbook.data.models.UserProfile> response) {
                if (getContext() != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getDisplayName() != null && !response.body().getDisplayName().isEmpty()) {
                        tvUserName.setText(response.body().getDisplayName());
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, Throwable t) {
            }
        });

        RecyclerView rvSpecialties = view.findViewById(R.id.rvSpecialties);
        rvSpecialties.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ApiRepository repo = new ApiRepository();
        repo.getSpecialties(new ApiRepository.Callback<List<Specialty>>() {
            @Override
            public void onSuccess(List<Specialty> result) {
                rvSpecialties.setAdapter(new SpecialtyAdapter(result));
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("HomeFragment", "Failed to load specialties", e);
            }
        });

        RecyclerView rvHospitals = view.findViewById(R.id.rvHospitals);
        rvHospitals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        repo.getHospitals(new ApiRepository.Callback<List<Hospital>>() {
            @Override
            public void onSuccess(List<Hospital> result) {
                rvHospitals.setAdapter(new HospitalAdapter(result));
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("HomeFragment", "Failed to load hospitals", e);
            }
        });

        view.findViewById(R.id.btnBookAppointment).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.doctorSearchFragment);
        });

        View btnBookClinic = view.findViewById(R.id.btnBookClinic);
        if (btnBookClinic != null) {
            btnBookClinic.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putInt("tabIndex", 2);
                Navigation.findNavController(v).navigate(R.id.doctorSearchFragment, args);
            });
        }
        
        View btnBookHospital = view.findViewById(R.id.btnBookHospital);
        if (btnBookHospital != null) {
            btnBookHospital.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putInt("tabIndex", 3);
                Navigation.findNavController(v).navigate(R.id.doctorSearchFragment, args);
            });
        }

        view.findViewById(R.id.btnOnlineConsultation).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.onlineConsultationFragment);
        });

        view.findViewById(R.id.btnChatWithDoctor).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.chatFragment);
        });

        view.findViewById(R.id.tvSeeAllSpecialties).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("tabIndex", 1);
            Navigation.findNavController(v).navigate(R.id.doctorSearchFragment, args);
        });

        view.findViewById(R.id.tvSeeAllHospitals).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("tabIndex", 3);
            Navigation.findNavController(v).navigate(R.id.doctorSearchFragment, args);
        });

        View btnHealthRecords = view.findViewById(R.id.btnHealthRecords);
        if (btnHealthRecords != null) {
            btnHealthRecords.setOnClickListener(v -> {
                com.google.android.material.bottomnavigation.BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                if (navView != null) {
                    navView.setSelectedItemId(R.id.navigation_profile);
                } else {
                    Navigation.findNavController(v).navigate(R.id.navigation_profile);
                }
            });
        }

        View btnVaccination = view.findViewById(R.id.btnVaccination);
        if (btnVaccination != null) {
            btnVaccination.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.vaccinationFragment);
            });
        }

        return view;
    }
}
