package com.example.healthbook.ui.account;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        SwitchMaterial switchAppointment = view.findViewById(R.id.switchAppointmentReminders);
        if (switchAppointment != null) {
            switchAppointment.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String status = isChecked ? "Đã bật nhắc lịch khám" : "Đã tắt nhắc lịch khám";
                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
            });
        }

        SwitchMaterial switchMedicine = view.findViewById(R.id.switchMedicineAlerts);
        if (switchMedicine != null) {
            switchMedicine.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String status = isChecked ? "Đã bật nhắc uống thuốc" : "Đã tắt nhắc uống thuốc";
                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
            });
        }

        SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
        if (switchDarkMode != null) {
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });
        }

        View btnChangePassword = view.findViewById(R.id.btnChangePassword);
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        }

        return view;
    }

    private void showChangePasswordDialog() {
        if (getContext() == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Đổi mật khẩu");
        builder.setMessage("Chúng tôi sẽ gửi email đặt lại mật khẩu đến địa chỉ:\n" + user.getEmail());

        builder.setPositiveButton("GỬI EMAIL", (dialog, which) -> {
            FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã gửi email hướng dẫn đổi mật khẩu!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Không thể gửi email: " + (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("HỦY", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
