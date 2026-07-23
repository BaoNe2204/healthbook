package com.example.healthbook.ui.appointments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.healthbook.R;

public class AppointmentDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_detail, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        
        View btnBookDoctor = view.findViewById(R.id.btnBookDoctor);
        if (btnBookDoctor != null) {
            btnBookDoctor.setVisibility(View.GONE);
        }

        View btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(getContext())
                .setTitle("Hủy lịch hẹn")
                .setMessage("Bạn có chắc chắn muốn hủy lịch hẹn này không?")
                .setPositiveButton("Hủy lịch", (dialog, which) -> {
                    if (getArguments() != null && getArguments().containsKey("appointment")) {
                        com.example.healthbook.data.models.Appointment appt = (com.example.healthbook.data.models.Appointment) getArguments().getSerializable("appointment");
                        if (appt != null) {
                            com.example.healthbook.data.ApiRepository repo = new com.example.healthbook.data.ApiRepository();
                            repo.cancelAppointment(appt.getId(), new com.example.healthbook.data.ApiRepository.Callback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    if (getContext() != null) {
                                        android.widget.Toast.makeText(getContext(), "Đã hủy lịch hẹn thành công!", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                    Navigation.findNavController(v).popBackStack();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    if (getContext() != null) {
                                        android.widget.Toast.makeText(getContext(), "Lỗi khi hủy lịch hẹn!", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            return; // Wait for API response before navigating back
                        }
                    }
                    Navigation.findNavController(v).popBackStack();
                })
                .setNegativeButton("Không", null)
                .show();
        });

        if (getArguments() != null && getArguments().containsKey("appointment")) {
            com.example.healthbook.data.models.Appointment appointment = (com.example.healthbook.data.models.Appointment) getArguments().getSerializable("appointment");
            if (appointment != null) {
                // Set Status
                android.widget.TextView tvStatus = view.findViewById(R.id.tvStatus);
                if (tvStatus != null) {
                    tvStatus.setText(appointment.getStatus());
                    if ("Sắp tới".equals(appointment.getStatus()) || "Đã duyệt".equals(appointment.getStatus())) {
                        tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
                    } else if ("Đã qua".equals(appointment.getStatus())) {
                        tvStatus.setTextColor(android.graphics.Color.parseColor("#9E9E9E")); // Gray
                    } else if ("Đã hủy".equals(appointment.getStatus())) {
                        tvStatus.setTextColor(android.graphics.Color.parseColor("#F44336")); // Red
                    } else {
                        tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800")); // Orange
                    }
                }
                
                if ("Đã hủy".equals(appointment.getStatus()) || "Đã qua".equals(appointment.getStatus())) {
                    btnCancel.setVisibility(View.GONE);
                } else {
                    btnCancel.setVisibility(View.VISIBLE);
                }
                
                // Doctor info
                if (appointment.getDoctor() != null) {
                    android.widget.TextView tvDoctorName = view.findViewById(R.id.tvDoctorName);
                    if (tvDoctorName != null) tvDoctorName.setText(appointment.getDoctor().getName());
                    android.widget.TextView tvDoctorSpecialty = view.findViewById(R.id.tvDoctorSpecialty);
                    if (tvDoctorSpecialty != null) tvDoctorSpecialty.setText(appointment.getDoctor().getSpecialty());
                    android.widget.TextView tvDoctorHospital = view.findViewById(R.id.tvDoctorHospital);
                    if (tvDoctorHospital != null) tvDoctorHospital.setText(appointment.getDoctor().getHospital());
                    
                    android.widget.TextView tvConfirmHospital = view.findViewById(R.id.tvConfirmHospital);
                    if (tvConfirmHospital != null) tvConfirmHospital.setText(appointment.getDoctor().getHospital());
                    
                    int price = appointment.getDoctor().getPrice() > 0 ? appointment.getDoctor().getPrice() : 300000;
                    android.widget.TextView tvConfirmPrice = view.findViewById(R.id.tvConfirmPrice);
                    if (tvConfirmPrice != null) tvConfirmPrice.setText(String.format("%,d", price).replace(',', '.') + "đ");
                }
                
                // Time
                android.widget.TextView tvConfirmTime = view.findViewById(R.id.tvConfirmTime);
                if (tvConfirmTime != null) {
                    tvConfirmTime.setText(appointment.getTime() + " - " + appointment.getDate());
                }
                
                // Health condition (reason)
                android.widget.TextView tvConfirmHealthCondition = view.findViewById(R.id.tvConfirmHealthCondition);
                if (tvConfirmHealthCondition != null) {
                    tvConfirmHealthCondition.setText(appointment.getType());
                }
                
                // Patient Info (from SharedPreferences for now)
                android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_profile", android.content.Context.MODE_PRIVATE);
                String name = prefs.getString("fullName", "");
                String phone = prefs.getString("phone", "");
                String dob = prefs.getString("dob", "");
                String gender = prefs.getString("gender", "");
                
                com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                if (name.isEmpty() && user != null && user.getDisplayName() != null) name = user.getDisplayName();
                if (phone.isEmpty() && user != null && user.getPhoneNumber() != null) phone = user.getPhoneNumber();
                
                android.widget.TextView tvConfirmPatientName = view.findViewById(R.id.tvConfirmPatientName);
                if (tvConfirmPatientName != null && !name.isEmpty()) tvConfirmPatientName.setText(name);
                
                android.widget.TextView tvConfirmPatientDobGender = view.findViewById(R.id.tvConfirmPatientDobGender);
                if (tvConfirmPatientDobGender != null && !dob.isEmpty() && !gender.isEmpty()) tvConfirmPatientDobGender.setText(gender + " - " + dob);
                
                android.widget.TextView tvConfirmPatientPhone = view.findViewById(R.id.tvConfirmPatientPhone);
                if (tvConfirmPatientPhone != null && !phone.isEmpty()) tvConfirmPatientPhone.setText(phone);
            }
        }

        return view;
    }
}
