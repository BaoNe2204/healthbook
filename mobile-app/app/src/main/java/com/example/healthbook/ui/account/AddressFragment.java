package com.example.healthbook.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

public class AddressFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        com.google.android.material.textfield.TextInputEditText etCity = view.findViewById(R.id.etCity);
        com.google.android.material.textfield.TextInputEditText etDistrict = view.findViewById(R.id.etDistrict);
        com.google.android.material.textfield.TextInputEditText etWard = view.findViewById(R.id.etWard);
        com.google.android.material.textfield.TextInputEditText etStreet = view.findViewById(R.id.etStreet);

        // Load address from Firebase Database
        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new retrofit2.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, retrofit2.Response<com.example.healthbook.data.models.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getAddress() != null) {
                    String[] parts = response.body().getAddress().split(",");
                    if (parts.length >= 4) {
                        etStreet.setText(parts[0].trim());
                        etWard.setText(parts[1].trim());
                        etDistrict.setText(parts[2].trim());
                        etCity.setText(parts[3].trim());
                    } else {
                        etStreet.setText(response.body().getAddress());
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, Throwable t) {
            }
        });

        view.findViewById(R.id.btnSaveAddress).setOnClickListener(v -> {
            String fullAddress = etStreet.getText().toString() + ", " + etWard.getText().toString() + ", " + etDistrict.getText().toString() + ", " + etCity.getText().toString();
            
            com.example.healthbook.data.models.UserProfile profile = new com.example.healthbook.data.models.UserProfile();
            profile.setAddress(fullAddress);

            com.example.healthbook.network.RetrofitClient.getInstance().getApiService().updateUserProfile(profile).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), "Đã lưu địa chỉ lên Firebase!", android.widget.Toast.LENGTH_SHORT).show();
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), "Không thể lưu địa chỉ: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
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
