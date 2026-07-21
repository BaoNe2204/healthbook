package com.example.healthbook.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

public class HealthInsuranceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_insurance, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        com.google.android.material.textfield.TextInputEditText etInsuranceCode = view.findViewById(R.id.etInsuranceCode);
        com.google.android.material.textfield.TextInputEditText etHospitalRegister = view.findViewById(R.id.etHospitalRegister);
        com.google.android.material.textfield.TextInputEditText etExpiryDate = view.findViewById(R.id.etExpiryDate);

        // Load insurance info from Firebase Database
        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new retrofit2.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, retrofit2.Response<com.example.healthbook.data.models.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.healthbook.data.models.UserProfile profile = response.body();
                    if (profile.getInsuranceCode() != null) etInsuranceCode.setText(profile.getInsuranceCode());
                    if (profile.getHospitalRegister() != null) etHospitalRegister.setText(profile.getHospitalRegister());
                    if (profile.getInsuranceExpiry() != null) etExpiryDate.setText(profile.getInsuranceExpiry());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, Throwable t) {
            }
        });

        view.findViewById(R.id.btnSaveInsurance).setOnClickListener(v -> {
            com.example.healthbook.data.models.UserProfile profile = new com.example.healthbook.data.models.UserProfile();
            profile.setInsuranceCode(etInsuranceCode.getText().toString());
            profile.setHospitalRegister(etHospitalRegister.getText().toString());
            profile.setInsuranceExpiry(etExpiryDate.getText().toString());

            com.example.healthbook.network.RetrofitClient.getInstance().getApiService().updateUserProfile(profile).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), "Đã lưu thẻ BHYT lên Firebase!", android.widget.Toast.LENGTH_SHORT).show();
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), "Không thể lưu BHYT: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        return view;
    }
}
