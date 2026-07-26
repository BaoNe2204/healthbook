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

public class RelativesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relatives, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        TextInputEditText etRelativeName = view.findViewById(R.id.etRelativeName);
        TextInputEditText etRelativeRelation = view.findViewById(R.id.etRelativeRelation);
        TextInputEditText etRelativePhone = view.findViewById(R.id.etRelativePhone);

        TextView tvCardRelativeName = view.findViewById(R.id.tvCardRelativeName);
        TextView tvCardRelativePhone = view.findViewById(R.id.tvCardRelativePhone);
        TextView tvCardRelativeRelation = view.findViewById(R.id.tvCardRelativeRelation);

        // Load relative info from Firebase / API
        RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    if (profile.getRelativeName() != null && !profile.getRelativeName().isEmpty()) {
                        etRelativeName.setText(profile.getRelativeName());
                        if (tvCardRelativeName != null) tvCardRelativeName.setText(profile.getRelativeName());
                    }
                    if (profile.getRelativeRelation() != null && !profile.getRelativeRelation().isEmpty()) {
                        etRelativeRelation.setText(profile.getRelativeRelation());
                        if (tvCardRelativeRelation != null) tvCardRelativeRelation.setText(profile.getRelativeRelation());
                    }
                    if (profile.getRelativePhone() != null && !profile.getRelativePhone().isEmpty()) {
                        etRelativePhone.setText(profile.getRelativePhone());
                        if (tvCardRelativePhone != null) tvCardRelativePhone.setText("SĐT: " + profile.getRelativePhone());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
            }
        });

        view.findViewById(R.id.btnSaveRelative).setOnClickListener(v -> {
            String name = etRelativeName.getText() != null ? etRelativeName.getText().toString().trim() : "";
            String relation = etRelativeRelation.getText() != null ? etRelativeRelation.getText().toString().trim() : "";
            String phone = etRelativePhone.getText() != null ? etRelativePhone.getText().toString().trim() : "";

            if (name.isEmpty()) {
                etRelativeName.setError("Vui lòng nhập tên người thân");
                return;
            }

            UserProfile profile = new UserProfile();
            profile.setRelativeName(name);
            profile.setRelativeRelation(relation);
            profile.setRelativePhone(phone);

            RetrofitClient.getInstance().getApiService().updateUserProfile(profile).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Đã lưu hồ sơ người thân thành công!", Toast.LENGTH_SHORT).show();
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Không thể lưu người thân: " + response.code(), Toast.LENGTH_SHORT).show();
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
