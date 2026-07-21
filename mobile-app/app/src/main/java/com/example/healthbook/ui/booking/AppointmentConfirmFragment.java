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
        btnConfirm.setOnClickListener(v -> {
            Bundle args = getArguments() != null ? new Bundle(getArguments()) : new Bundle();
            android.widget.EditText edtHealthCondition = view.findViewById(R.id.edtHealthCondition);
            if (edtHealthCondition != null) {
                args.putString("healthCondition", edtHealthCondition.getText().toString());
            }
            Navigation.findNavController(v).navigate(R.id.paymentFragment, args);
        });

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

        android.widget.TextView tvPatientName = view.findViewById(R.id.tvConfirmPatientName);
        android.widget.TextView tvPatientPhone = view.findViewById(R.id.tvConfirmPatientPhone);
        android.widget.TextView tvPatientDobGender = view.findViewById(R.id.tvConfirmPatientDobGender);

        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        // Load profile from Firebase API
        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new retrofit2.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, retrofit2.Response<com.example.healthbook.data.models.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.healthbook.data.models.UserProfile profile = response.body();
                    String name = profile.getDisplayName() != null ? profile.getDisplayName() : (user != null ? user.getDisplayName() : "");
                    String phone = profile.getPhone() != null ? profile.getPhone() : "";
                    String dob = profile.getDob() != null ? profile.getDob() : "";
                    String gender = profile.getGender() != null ? profile.getGender() : "";

                    if (tvPatientName != null && !name.isEmpty()) tvPatientName.setText(name);
                    if (tvPatientPhone != null && !phone.isEmpty()) tvPatientPhone.setText(phone);
                    if (tvPatientDobGender != null && !dob.isEmpty()) tvPatientDobGender.setText(gender + " - " + dob);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, Throwable t) {
            }
        });

        return view;
    }
}
