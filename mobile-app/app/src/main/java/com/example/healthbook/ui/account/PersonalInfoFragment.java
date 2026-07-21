package com.example.healthbook.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.Toast;
import java.util.Calendar;

public class PersonalInfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        // Initialize views
        TextInputEditText etFullName = view.findViewById(R.id.etFullName);
        TextInputEditText etPhone = view.findViewById(R.id.etPhone);
        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
        TextInputEditText etDob = view.findViewById(R.id.etDob);
        AutoCompleteTextView etGender = view.findViewById(R.id.etGender);
        TextInputEditText etAddress = view.findViewById(R.id.etAddress);

        // Load current user data from Firebase Cloud
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null) etFullName.setText(user.getDisplayName());
        if (user != null && user.getEmail() != null) etEmail.setText(user.getEmail());

        // Fetch from API to get the latest data
        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new retrofit2.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, retrofit2.Response<com.example.healthbook.data.models.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.healthbook.data.models.UserProfile profile = response.body();
                    
                    if (profile.getDisplayName() != null) etFullName.setText(profile.getDisplayName());
                    if (profile.getPhone() != null) etPhone.setText(profile.getPhone());
                    if (profile.getDob() != null) etDob.setText(profile.getDob());
                    if (profile.getGender() != null) etGender.setText(profile.getGender());
                    if (profile.getAddress() != null) etAddress.setText(profile.getAddress());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, Throwable t) {
            }
        });

        // Setup DatePicker for DOB
        if (etDob != null) {
            etDob.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (datePicker, y, m, d) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", d, m + 1, y);
                    etDob.setText(formattedDate);
                }, year, month, day);
                datePickerDialog.show();
            });
        }

        // Setup Gender Dropdown
        if (etGender != null) {
            String[] genders = new String[]{"Nam", "Nữ", "Khác"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
            etGender.setAdapter(adapter);
        }
        
        // Setup Save Button
        View btnSave = view.findViewById(R.id.btnSave);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String name = etFullName.getText().toString();
                String phone = etPhone.getText().toString();
                String dob = etDob.getText().toString();
                String gender = etGender.getText().toString();
                String address = etAddress.getText().toString();

                com.example.healthbook.data.models.UserProfile profile = new com.example.healthbook.data.models.UserProfile();
                profile.setDisplayName(name);
                profile.setPhone(phone);
                profile.setDob(dob);
                profile.setGender(gender);
                profile.setAddress(address);

                com.example.healthbook.network.RetrofitClient.getInstance().getApiService().updateUserProfile(profile).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Lưu thông tin lên Firebase thành công!", Toast.LENGTH_SHORT).show();
                            }
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        } else {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Lỗi khi lưu lên máy chủ!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        }

        return view;
    }
}
