package com.example.healthbook.ui.clinic;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthbook.R;
import com.example.healthbook.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicScheduleFragment extends Fragment {

    private Button btnSelectDate, btnSaveSchedule;
    private GridLayout gridTimeSlots;
    private String selectedDateStr = "";
    
    private final List<String> allTimeSlots = Arrays.asList(
            "07:30-08:00", "08:00-08:30", "08:30-09:00", "09:00-09:30", 
            "09:30-10:00", "10:00-10:30", "10:30-11:00", "11:00-11:30",
            "13:30-14:00", "14:00-14:30", "14:30-15:00", "15:00-15:30", 
            "15:30-16:00", "16:00-16:30", "16:30-17:00", "17:00-17:30"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clinic_schedule, container, false);
        
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSaveSchedule = view.findViewById(R.id.btnSaveSchedule);
        gridTimeSlots = view.findViewById(R.id.gridTimeSlots);
        
        // Default to today
        Calendar calendar = Calendar.getInstance();
        updateDateLabel(calendar);

        btnSelectDate.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                updateDateLabel(calendar);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSaveSchedule.setOnClickListener(v -> saveSchedule());

        return view;
    }
    
    private void updateDateLabel(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDateStr = sdf.format(calendar.getTime());
        btnSelectDate.setText(selectedDateStr);
        
        fetchScheduleForDate(selectedDateStr);
    }
    
    private void fetchScheduleForDate(String date) {
        gridTimeSlots.removeAllViews(); // Clear previous
        
        // Populate check boxes
        for (String slot : allTimeSlots) {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(slot);
            cb.setTag(slot);
            // Default layout params for GridLayout
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            cb.setLayoutParams(params);
            
            gridTimeSlots.addView(cb);
        }
        
        // Fetch from API
        RetrofitClient.getInstance().getApiService().getClinicScheduleAdmin(date).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> busySlots = response.body();
                    for (int i = 0; i < gridTimeSlots.getChildCount(); i++) {
                        View child = gridTimeSlots.getChildAt(i);
                        if (child instanceof CheckBox) {
                            CheckBox cb = (CheckBox) child;
                            if (busySlots.contains(cb.getTag().toString())) {
                                cb.setChecked(true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi tải lịch: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveSchedule() {
        if (selectedDateStr.isEmpty()) return;
        
        List<String> busySlots = new ArrayList<>();
        for (int i = 0; i < gridTimeSlots.getChildCount(); i++) {
            View child = gridTimeSlots.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox cb = (CheckBox) child;
                if (cb.isChecked()) {
                    busySlots.add(cb.getTag().toString());
                }
            }
        }
        
        Map<String, Object> body = new HashMap<>();
        body.put("date", selectedDateStr);
        body.put("busy_slots", busySlots);
        
        RetrofitClient.getInstance().getApiService().updateClinicSchedule(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Cập nhật lịch thành công!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Lỗi cập nhật lịch!", Toast.LENGTH_SHORT).show();
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
    }
}
