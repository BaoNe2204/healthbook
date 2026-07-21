package com.example.healthbook.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

public class RelativesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relatives, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        com.google.android.material.textfield.TextInputEditText etRelativeName = view.findViewById(R.id.etRelativeName);
        com.google.android.material.textfield.TextInputEditText etRelativeRelation = view.findViewById(R.id.etRelativeRelation);
        com.google.android.material.textfield.TextInputEditText etRelativePhone = view.findViewById(R.id.etRelativePhone);

        // Load relative info from Firebase Database
        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new retrofit2.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, retrofit2.Response<com.example.healthbook.data.models.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.healthbook.data.models.UserProfile profile = response.body();
                    if (profile.getRelativeName() != null) etRelativeName.setText(profile.getRelativeName());
                    if (profile.getRelativeRelation() != null) etRelativeRelation.setText(profile.getRelativeRelation());
                    if (profile.getRelativePhone() != null) etRelativePhone.setText(profile.getRelativePhone());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, Throwable t) {
            }
        });

        view.findViewById(R.id.btnSaveRelative).setOnClickListener(v -> {
            com.example.healthbook.data.models.UserProfile profile = new com.example.healthbook.data.models.UserProfile();
            profile.setRelativeName(etRelativeName.getText().toString());
            profile.setRelativeRelation(etRelativeRelation.getText().toString());
            profile.setRelativePhone(etRelativePhone.getText().toString());

            com.example.healthbook.network.RetrofitClient.getInstance().getApiService().updateUserProfile(profile).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), "Đã lưu hồ sơ người thân lên Firebase!", android.widget.Toast.LENGTH_SHORT).show();
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), "Không thể lưu người thân: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
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
