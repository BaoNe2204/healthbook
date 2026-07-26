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

public class HealthInsuranceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_insurance, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        TextInputEditText etInsuranceCode = view.findViewById(R.id.etInsuranceCode);
        TextInputEditText etHospitalRegister = view.findViewById(R.id.etHospitalRegister);
        TextInputEditText etExpiryDate = view.findViewById(R.id.etExpiryDate);

        TextView tvCardInsuranceCode = view.findViewById(R.id.tvCardInsuranceCode);
        TextView tvCardHospitalRegister = view.findViewById(R.id.tvCardHospitalRegister);
        TextView tvCardExpiryDate = view.findViewById(R.id.tvCardExpiryDate);

        // Load insurance info from API / Firebase
        RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    if (profile.getInsuranceCode() != null && !profile.getInsuranceCode().isEmpty()) {
                        etInsuranceCode.setText(profile.getInsuranceCode());
                        if (tvCardInsuranceCode != null) tvCardInsuranceCode.setText(profile.getInsuranceCode());
                    }
                    if (profile.getHospitalRegister() != null && !profile.getHospitalRegister().isEmpty()) {
                        etHospitalRegister.setText(profile.getHospitalRegister());
                        if (tvCardHospitalRegister != null) tvCardHospitalRegister.setText("Nơi KCB: " + profile.getHospitalRegister());
                    }
                    if (profile.getInsuranceExpiry() != null && !profile.getInsuranceExpiry().isEmpty()) {
                        etExpiryDate.setText(profile.getInsuranceExpiry());
                        if (tvCardExpiryDate != null) tvCardExpiryDate.setText("Hạn dùng: " + profile.getInsuranceExpiry());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
            }
        });

        view.findViewById(R.id.btnSaveInsurance).setOnClickListener(v -> {
            String code = etInsuranceCode.getText() != null ? etInsuranceCode.getText().toString().trim() : "";
            String hospital = etHospitalRegister.getText() != null ? etHospitalRegister.getText().toString().trim() : "";
            String expiry = etExpiryDate.getText() != null ? etExpiryDate.getText().toString().trim() : "";

            if (code.isEmpty()) {
                etInsuranceCode.setError("Vui lòng nhập mã thẻ BHYT");
                return;
            }

            // Update card preview live
            if (tvCardInsuranceCode != null) tvCardInsuranceCode.setText(code);
            if (tvCardHospitalRegister != null) tvCardHospitalRegister.setText("Nơi KCB: " + hospital);
            if (tvCardExpiryDate != null) tvCardExpiryDate.setText("Hạn dùng: " + expiry);

            UserProfile profile = new UserProfile();
            profile.setInsuranceCode(code);
            profile.setHospitalRegister(hospital);
            profile.setInsuranceExpiry(expiry);

            RetrofitClient.getInstance().getApiService().updateUserProfile(profile).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Đã lưu và xác thực thẻ BHYT!", Toast.LENGTH_SHORT).show();
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Không thể lưu thông tin BHYT: " + response.code(), Toast.LENGTH_SHORT).show();
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
