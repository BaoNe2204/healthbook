package com.example.healthbook.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;
import com.example.healthbook.data.models.UserProfile;
import com.example.healthbook.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        TextInputEditText etCity = view.findViewById(R.id.etCity);
        TextInputEditText etDistrict = view.findViewById(R.id.etDistrict);
        TextInputEditText etWard = view.findViewById(R.id.etWard);
        TextInputEditText etStreet = view.findViewById(R.id.etStreet);
        TextView tvFullAddressDisplay = view.findViewById(R.id.tvFullAddressDisplay);

        // Load address from API / Firebase
        RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getAddress() != null) {
                    String fullAddr = response.body().getAddress();
                    if (tvFullAddressDisplay != null) tvFullAddressDisplay.setText(fullAddr);
                    String[] parts = fullAddr.split(",");
                    if (parts.length >= 4) {
                        etStreet.setText(parts[0].trim());
                        etWard.setText(parts[1].trim());
                        etDistrict.setText(parts[2].trim());
                        etCity.setText(parts[3].trim());
                    } else {
                        etStreet.setText(fullAddr);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
            }
        });

        view.findViewById(R.id.btnSaveAddress).setOnClickListener(v -> {
            String street = etStreet.getText() != null ? etStreet.getText().toString().trim() : "";
            String ward = etWard.getText() != null ? etWard.getText().toString().trim() : "";
            String district = etDistrict.getText() != null ? etDistrict.getText().toString().trim() : "";
            String city = etCity.getText() != null ? etCity.getText().toString().trim() : "";

            if (street.isEmpty()) {
                etStreet.setError("Vui lòng nhập số nhà, tên đường");
                return;
            }

            String fullAddress = street + ", " + ward + ", " + district + ", " + city;
            UserProfile profile = new UserProfile();
            profile.setAddress(fullAddress);

            RetrofitClient.getInstance().getApiService().updateUserProfile(profile).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Đã cập nhật địa chỉ khám bệnh!", Toast.LENGTH_SHORT).show();
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Không thể lưu địa chỉ: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        return view;
    }
}
