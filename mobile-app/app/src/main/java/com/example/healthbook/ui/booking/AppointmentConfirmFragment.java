package com.example.healthbook.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

import androidx.navigation.Navigation;
import android.widget.ImageView;

public class AppointmentConfirmFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_confirm, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        View btnConfirm = view.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.paymentFragment, getArguments()));

        if (getArguments() != null) {
            String date = getArguments().getString("bookingDate", "");
            String time = getArguments().getString("bookingTime", "");
            String price = getArguments().getString("bookingPrice", "300.000đ");
            
            if (!date.isEmpty() && !time.isEmpty()) {
                android.widget.TextView tvConfirmTime = view.findViewById(R.id.tvConfirmTime);
                if (tvConfirmTime != null) {
                    tvConfirmTime.setText(time + " - " + date);
                }
            }
            
            android.widget.TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
            if (tvTotalPrice != null) {
                tvTotalPrice.setText(price);
            }
        }

        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_profile", android.content.Context.MODE_PRIVATE);
        String name = prefs.getString("fullName", "");
        String phone = prefs.getString("phone", "");
        String dob = prefs.getString("dob", "");
        String gender = prefs.getString("gender", "");
        
        // Override with custom profile if provided
        if (getArguments() != null) {
            if (getArguments().getString("patientName") != null) name = getArguments().getString("patientName");
            if (getArguments().getString("patientPhone") != null) phone = getArguments().getString("patientPhone");
            if (getArguments().getString("patientDob") != null) dob = getArguments().getString("patientDob");
            if (getArguments().getString("patientGender") != null) gender = getArguments().getString("patientGender");
        }

        android.widget.TextView tvPatientName = view.findViewById(R.id.tvConfirmPatientName);
        if (tvPatientName != null) {
            if (!name.isEmpty()) tvPatientName.setText(name);
            else if (user != null && user.getDisplayName() != null) tvPatientName.setText(user.getDisplayName());
        }
        
        android.widget.TextView tvPatientPhone = view.findViewById(R.id.tvConfirmPatientPhone);
        if (tvPatientPhone != null) {
            if (!phone.isEmpty()) tvPatientPhone.setText(phone);
            else if (user != null && user.getPhoneNumber() != null) tvPatientPhone.setText(user.getPhoneNumber());
        }

        android.widget.TextView tvPatientDobGender = view.findViewById(R.id.tvConfirmPatientDobGender);
        if (tvPatientDobGender != null && !dob.isEmpty() && !gender.isEmpty()) {
            tvPatientDobGender.setText(gender + " - " + dob);
        }

        return view;
    }
}
