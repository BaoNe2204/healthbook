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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.adapters.DateAdapter;
import com.example.healthbook.adapters.TimeAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.widget.TextView;
import com.example.healthbook.data.models.Doctor;

public class TimeSelectionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_selection, container, false);

        View btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        String priceStr = "300.000đ";
        if (getArguments() != null && getArguments().containsKey("doctor")) {
            Doctor doctor = (Doctor) getArguments().getSerializable("doctor");
            if (doctor != null) {
                int price = doctor.getPrice() > 0 ? doctor.getPrice() : 300000;
                priceStr = String.format("%,d", price).replace(',', '.') + "đ";
            }
        }

        TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        if (tvTotalPrice != null) {
            tvTotalPrice.setText(priceStr);
        }

        final String finalPriceStr = priceStr;
        View btnContinue = view.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            Bundle args = getArguments() != null ? new Bundle(getArguments()) : new Bundle();
            args.putString("bookingPrice", finalPriceStr);
            Navigation.findNavController(v).navigate(R.id.appointmentConfirmFragment, args);
        });

        RecyclerView rvDates = view.findViewById(R.id.rvDates);
        RecyclerView rvTimes = view.findViewById(R.id.rvTimes);

        List<String> days = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        
        java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("EEE", new java.util.Locale("vi", "VN"));
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        java.text.SimpleDateFormat fullDateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        final List<String> fullDates = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            days.add(i == 0 ? "Hôm nay" : dayFormat.format(calendar.getTime()));
            dates.add(dateFormat.format(calendar.getTime()));
            fullDates.add(fullDateFormat.format(calendar.getTime()));
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        final List<String> availableTimes = new ArrayList<>();
        final TimeAdapter timeAdapter = new TimeAdapter(availableTimes);
        rvTimes.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvTimes.setAdapter(timeAdapter);

        com.example.healthbook.data.models.Doctor doctor = (com.example.healthbook.data.models.Doctor) (getArguments() != null ? getArguments().getSerializable("doctor") : null);
        final String doctorId = doctor != null ? doctor.getId() : "1";

        DateAdapter dateAdapter = new DateAdapter(days, dates, new DateAdapter.OnDateSelectedListener() {
            @Override
            public void onDateSelected(String date, int position) {
                if (position >= 0 && position < fullDates.size()) {
                    loadSlots(doctorId, fullDates.get(position), availableTimes, timeAdapter);
                }
            }
        });
        rvDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDates.setAdapter(dateAdapter);

        // Load initially today's schedule
        if (!fullDates.isEmpty()) {
            loadSlots(doctorId, fullDates.get(0), availableTimes, timeAdapter);
        }

        btnContinue.setOnClickListener(v -> {
            Bundle args = new Bundle();
            if (getArguments() != null) {
                args.putAll(getArguments());
            }
            int pos = dateAdapter.getSelectedPosition();
            String selectedDateStr = (pos >= 0 && pos < fullDates.size()) ? fullDates.get(pos) : fullDateFormat.format(new java.util.Date());
            
            args.putString("bookingDate", selectedDateStr);
            args.putString("bookingTime", timeAdapter.getSelectedTime());
            
            if (doctor != null) {
                args.putString("bookingPrice", String.format("%,d", doctor.getPrice() > 0 ? doctor.getPrice() : 300000).replace(',', '.') + "đ");
            } else {
                args.putString("bookingPrice", "300.000đ");
            }
            
            Navigation.findNavController(v).navigate(R.id.appointmentConfirmFragment, args);
        });

        return view;
    }

    private void loadSlots(String doctorId, String date, List<String> availableTimes, TimeAdapter timeAdapter) {
        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getDoctorSchedule(doctorId, date).enqueue(new retrofit2.Callback<List<String>>() {
            @Override
            public void onResponse(retrofit2.Call<List<String>> call, retrofit2.Response<List<String>> response) {
                availableTimes.clear();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    availableTimes.addAll(response.body());
                } else {
                    // Fallback to mock slots if doctor has not configured schedule
                    availableTimes.addAll(Arrays.asList("08:00", "09:00", "10:00", "14:00", "15:00"));
                }
                timeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(retrofit2.Call<List<String>> call, Throwable t) {
                availableTimes.clear();
                availableTimes.addAll(Arrays.asList("08:00", "09:00", "10:00", "14:00", "15:00"));
                timeAdapter.notifyDataSetChanged();
            }
        });
    }
}
