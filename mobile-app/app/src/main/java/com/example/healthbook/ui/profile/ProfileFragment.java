package com.example.healthbook.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.widget.EditText;
import android.text.InputType;
import android.widget.Toast;
import com.example.healthbook.data.models.UserProfile;
import com.example.healthbook.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    
    private TextView tvUserName, tvUserInfo, tvWeight, tvHeight, tvBmi, tvBmiStatus;
    private UserProfile currentUserProfile;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserInfo = view.findViewById(R.id.tvUserInfo);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvBmi = view.findViewById(R.id.tvBmi);
        tvBmiStatus = view.findViewById(R.id.tvBmiStatus);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            tvUserName.setText(user.getDisplayName());
        }

        View.OnClickListener goToHistory = v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.medicalHistoryFragment);
        };

        view.findViewById(R.id.btnMedicalHistory).setOnClickListener(goToHistory);
        view.findViewById(R.id.btnPrescriptions).setOnClickListener(goToHistory);
        
        View btnTestResults = view.findViewById(R.id.btnTestResults);
        if (btnTestResults != null) {
            btnTestResults.setOnClickListener(v -> {
                android.widget.Toast.makeText(getContext(), "Tính năng Kết quả xét nghiệm đang được phát triển!", android.widget.Toast.LENGTH_SHORT).show();
            });
        }
        
        View btnVaccinationHistory = view.findViewById(R.id.btnVaccinationHistory);
        if (btnVaccinationHistory != null) {
            btnVaccinationHistory.setOnClickListener(v -> {
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.vaccinationHistoryFragment);
            });
        }
        
        loadUserProfile();
        
        View btnEditMetrics = view.findViewById(R.id.btnEditMetrics);
        if (btnEditMetrics != null) {
            btnEditMetrics.setOnClickListener(v -> showEditMetricsDialog());
        }

        return view;
    }
    
    private void loadUserProfile() {
        RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (getContext() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    currentUserProfile = response.body();
                    if (currentUserProfile.getDisplayName() != null) {
                        tvUserName.setText(currentUserProfile.getDisplayName());
                    }
                    String dob = currentUserProfile.getDob() != null && !currentUserProfile.getDob().isEmpty() ? currentUserProfile.getDob() : "--";
                    String gender = currentUserProfile.getGender() != null && !currentUserProfile.getGender().isEmpty() ? currentUserProfile.getGender() : "--";
                    if (tvUserInfo != null) {
                        tvUserInfo.setText(dob + " - " + gender);
                    }
                    if (currentUserProfile.getAvatarUrl() != null && !currentUserProfile.getAvatarUrl().isEmpty()) {
                        if (getView() != null && getContext() != null) {
                            android.widget.ImageView ivAvatarProfile = getView().findViewById(R.id.ivAvatarProfile);
                            if (ivAvatarProfile != null) {
                                com.bumptech.glide.Glide.with(getContext()).load(currentUserProfile.getAvatarUrl()).into(ivAvatarProfile);
                            }
                        }
                    }
                    updateMetricsUI();
                } else {
                    if (currentUserProfile == null) {
                        currentUserProfile = new UserProfile();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể tải thông tin: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (currentUserProfile == null) {
                    currentUserProfile = new UserProfile();
                }
            }
        });
    }
    
    private void updateMetricsUI() {
        if (currentUserProfile == null) return;
        
        Double weight = currentUserProfile.getWeight();
        Double height = currentUserProfile.getHeight();
        
        if (weight != null) tvWeight.setText(String.format(java.util.Locale.US, "%.1f kg", weight));
        else tvWeight.setText("-- kg");
        
        if (height != null) tvHeight.setText(String.format(java.util.Locale.US, "%.1f cm", height));
        else tvHeight.setText("-- cm");
        
        if (weight != null && height != null && height > 0) {
            double heightM = height / 100.0;
            double bmi = weight / (heightM * heightM);
            tvBmi.setText(String.format(java.util.Locale.US, "%.1f", bmi));
            
            // Asian BMI categories
            if (bmi < 18.5) {
                tvBmiStatus.setText("Thiếu cân");
                tvBmiStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"));
            } else if (bmi < 23) {
                tvBmiStatus.setText("Bình thường");
                tvBmiStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
            } else if (bmi < 25) {
                tvBmiStatus.setText("Thừa cân");
                tvBmiStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"));
            } else {
                tvBmiStatus.setText("Béo phì");
                tvBmiStatus.setTextColor(android.graphics.Color.parseColor("#F44336"));
            }
        } else {
            tvBmi.setText("--");
            tvBmiStatus.setText("Chưa rõ");
            tvBmiStatus.setTextColor(android.graphics.Color.GRAY);
        }
    }
    
    private void showEditMetricsDialog() {
        if (getContext() == null) return;
        
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        final EditText etWeight = new EditText(getContext());
        etWeight.setHint("Cân nặng (kg)");
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (currentUserProfile != null && currentUserProfile.getWeight() != null) {
            etWeight.setText(String.valueOf(currentUserProfile.getWeight()));
        }
        layout.addView(etWeight);
        
        final EditText etHeight = new EditText(getContext());
        etHeight.setHint("Chiều cao (cm)");
        etHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (currentUserProfile != null && currentUserProfile.getHeight() != null) {
            etHeight.setText(String.valueOf(currentUserProfile.getHeight()));
        }
        layout.addView(etHeight);
        
        new AlertDialog.Builder(getContext())
            .setTitle("Cập nhật chỉ số sức khỏe")
            .setView(layout)
            .setPositiveButton("Lưu", (dialog, which) -> {
                try {
                    Double weight = null;
                    if (!etWeight.getText().toString().isEmpty()) {
                        weight = Double.parseDouble(etWeight.getText().toString());
                    }
                    Double height = null;
                    if (!etHeight.getText().toString().isEmpty()) {
                        height = Double.parseDouble(etHeight.getText().toString());
                    }
                    
                    if (currentUserProfile == null) {
                        currentUserProfile = new UserProfile();
                    }
                    currentUserProfile.setWeight(weight);
                    currentUserProfile.setHeight(height);
                    updateMetricsUI();
                    
                    RetrofitClient.getInstance().getApiService().updateUserProfile(currentUserProfile).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (getContext() != null) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Lỗi khi lưu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}
