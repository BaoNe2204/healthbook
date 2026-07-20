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
import android.widget.Toast;
import android.app.ProgressDialog;

import com.example.healthbook.data.models.Appointment;
import com.example.healthbook.data.models.Doctor;
import com.example.healthbook.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        View btnPay = view.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(v -> {
            if (getArguments() != null) {
                String date = getArguments().getString("bookingDate", "Hôm nay");
                String time = getArguments().getString("bookingTime", "08:00");
                String healthCondition = getArguments().getString("healthCondition", "Khám tổng quát");
                
                com.example.healthbook.data.models.Doctor doctor = null;
                if (getArguments().containsKey("doctor")) {
                    doctor = (com.example.healthbook.data.models.Doctor) getArguments().getSerializable("doctor");
                }
                if (doctor == null) {
                    doctor = com.example.healthbook.data.MockData.getDoctors().get(0); 
                }
                
                com.example.healthbook.data.models.Appointment newAppt = new com.example.healthbook.data.models.Appointment(
                    "A" + System.currentTimeMillis(),
                    doctor,
                    date,
                    time,
                    "Sắp tới",
                    healthCondition.isEmpty() ? "Khám tại bệnh viện" : healthCondition
                );
                com.example.healthbook.data.MockData.addAppointment(newAppt);
            }
            Navigation.findNavController(v).navigate(R.id.bookingSuccessFragment);
        });

        // Fetch details for saving
        String date = "";
        String time = "";
        String price = "300.000đ";
        
        if (getArguments() != null) {
            date = getArguments().getString("bookingDate", "");
            time = getArguments().getString("bookingTime", "");
            price = getArguments().getString("bookingPrice", "300.000đ");
            
            if (!date.isEmpty() && !time.isEmpty()) {
                android.widget.TextView tvPaymentTime = view.findViewById(R.id.tvPaymentTime);
                if (tvPaymentTime != null) {
                    tvPaymentTime.setText(time + " - " + date);
                }
            }
            
            android.widget.TextView tvTotalPrice = view.findViewById(R.id.tvPaymentTotalPrice);
            if (tvTotalPrice != null) {
                tvTotalPrice.setText(price);
            }
            
            if (btnPay instanceof android.widget.Button) {
                ((android.widget.Button) btnPay).setText("Thanh toán " + price);
            }
        }

        final String finalDate = date;
        final String finalTime = time;

        btnPay.setOnClickListener(v -> {
            // Retrieve patient info
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_profile", android.content.Context.MODE_PRIVATE);
            String name = prefs.getString("fullName", "");
            String phone = prefs.getString("phone", "");
            String dob = prefs.getString("dob", "");
            String gender = prefs.getString("gender", "");
            
            if (getArguments() != null) {
                if (getArguments().getString("patientName") != null) name = getArguments().getString("patientName");
                if (getArguments().getString("patientPhone") != null) phone = getArguments().getString("patientPhone");
                if (getArguments().getString("patientDob") != null) dob = getArguments().getString("patientDob");
                if (getArguments().getString("patientGender") != null) gender = getArguments().getString("patientGender");
            }
            
            if (name.isEmpty() && user != null && user.getDisplayName() != null) {
                name = user.getDisplayName();
            }
            if (phone.isEmpty() && user != null && user.getPhoneNumber() != null) {
                phone = user.getPhoneNumber();
            }

            Appointment appt = new Appointment();
            Doctor doctor = (Doctor) (getArguments() != null ? getArguments().getSerializable("doctor") : null);
            if (doctor != null) {
                appt.setDoctor(doctor);
            } else {
                Doctor dummyDoctor = new Doctor();
                dummyDoctor.setId("1");
                appt.setDoctor(dummyDoctor);
            }
            
            appt.setDate(finalDate);
            appt.setTime(finalTime);
            appt.setStatus("Sắp tới");
            appt.setType("Khám tại bệnh viện");
            appt.setPatient_name(name);
            appt.setPatient_phone(phone);
            appt.setPatient_dob(dob);
            appt.setPatient_gender(gender);

            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Đang xử lý thanh toán và đặt lịch...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            RetrofitClient.getInstance().getApiService().createAppointment(appt).enqueue(new Callback<Appointment>() {
                @Override
                public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        Navigation.findNavController(view).navigate(R.id.bookingSuccessFragment);
                    } else {
                        Toast.makeText(getContext(), "Không thể đặt lịch khám. Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Appointment> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        return view;
    }
}
