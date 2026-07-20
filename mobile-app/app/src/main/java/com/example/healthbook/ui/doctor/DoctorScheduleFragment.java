package com.example.healthbook.ui.doctor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthbook.R;
import com.example.healthbook.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorScheduleFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvSelectedDateLabel;
    private GridLayout gridMorning, gridAfternoon;
    private View btnSaveSchedule;

    private String selectedDate = "";
    private final Set<String> selectedSlots = new HashSet<>();

    private final List<String> morningSlots = Arrays.asList(
            "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"
    );
    private final List<String> afternoonSlots = Arrays.asList(
            "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_schedule, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        tvSelectedDateLabel = view.findViewById(R.id.tvSelectedDateLabel);
        gridMorning = view.findViewById(R.id.gridMorning);
        gridAfternoon = view.findViewById(R.id.gridAfternoon);
        btnSaveSchedule = view.findViewById(R.id.btnSaveSchedule);

        // Default to today's date
        selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvSelectedDateLabel.setText("Lịch rảnh ngày: " + selectedDate);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
            tvSelectedDateLabel.setText("Lịch rảnh ngày: " + selectedDate);
            // In a real app we'd load existing schedule for this day from backend. For now, clear selections.
            selectedSlots.clear();
            refreshGrids();
        });

        refreshGrids();

        btnSaveSchedule.setOnClickListener(v -> saveSchedule());

        return view;
    }

    private void refreshGrids() {
        populateGrid(gridMorning, morningSlots);
        populateGrid(gridAfternoon, afternoonSlots);
    }

    private void populateGrid(GridLayout grid, List<String> slots) {
        grid.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        
        for (String slot : slots) {
            TextView tv = new TextView(getContext());
            tv.setText(slot);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding((int)(8*density), (int)(8*density), (int)(8*density), (int)(8*density));
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins((int)(4*density), (int)(4*density), (int)(4*density), (int)(4*density));
            tv.setLayoutParams(params);

            updateSlotStyle(tv, slot);

            tv.setOnClickListener(v -> {
                if (selectedSlots.contains(slot)) {
                    selectedSlots.remove(slot);
                } else {
                    selectedSlots.add(slot);
                }
                updateSlotStyle(tv, slot);
            });

            grid.addView(tv);
        }
    }

    private void updateSlotStyle(TextView tv, String slot) {
        if (selectedSlots.contains(slot)) {
            tv.setBackgroundResource(R.drawable.bg_rounded_primary);
            tv.setTextColor(Color.WHITE);
        } else {
            tv.setBackgroundResource(R.drawable.bg_border_rounded_grey);
            tv.setTextColor(Color.parseColor("#424242"));
        }
    }

    private void saveSchedule() {
        if (selectedSlots.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất một khung giờ rảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("date", selectedDate);
        body.put("timeSlots", new ArrayList<>(selectedSlots));

        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(getContext());
        progressDialog.setMessage("Đang lưu lịch làm việc...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitClient.getInstance().getApiService().updateDoctorSchedule(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Lịch làm việc đã được cập nhật thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Không thể lưu lịch làm việc. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}