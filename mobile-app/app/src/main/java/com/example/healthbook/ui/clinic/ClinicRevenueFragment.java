package com.example.healthbook.ui.clinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.ClinicAppointmentAdapter;
import com.example.healthbook.data.models.Appointment;
import com.example.healthbook.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicRevenueFragment extends Fragment {

    private TextView tvTotalRevenue, tvSuccessCount, tvCancelledCount;
    private RecyclerView rvRecentAppointments;
    private ClinicAppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clinic_revenue, container, false);

        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvSuccessCount = view.findViewById(R.id.tvSuccessCount);
        tvCancelledCount = view.findViewById(R.id.tvCancelledCount);
        rvRecentAppointments = view.findViewById(R.id.rvRecentAppointments);

        rvRecentAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClinicAppointmentAdapter(appointmentList, () -> fetchRevenueData());
        rvRecentAppointments.setAdapter(adapter);

        fetchRevenueData();

        return view;
    }

    private void fetchRevenueData() {
        RetrofitClient.getInstance().getApiService().getClinicRevenue().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();
                    
                    double totalRevenue = (Double) data.get("total_revenue");
                    double successCount = (Double) data.get("successful_appointments");
                    double cancelledCount = (Double) data.get("cancelled_appointments");
                    
                    tvTotalRevenue.setText(String.format("%,.0f", totalRevenue).replace(',', '.') + "đ");
                    tvSuccessCount.setText(String.valueOf((int) successCount));
                    tvCancelledCount.setText(String.valueOf((int) cancelledCount));
                    
                    Object recentObj = data.get("recent_appointments");
                    if (recentObj != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(recentObj);
                        List<Appointment> recent = gson.fromJson(json, new TypeToken<List<Appointment>>(){}.getType());
                        appointmentList.clear();
                        appointmentList.addAll(recent);
                        adapter.notifyDataSetChanged();
                    }
                    
                    Object monthlyObj = data.get("monthly_revenue");
                    if (monthlyObj != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(monthlyObj);
                        Map<String, Double> monthly = gson.fromJson(json, new TypeToken<Map<String, Double>>(){}.getType());
                        setupBarChart(monthly);
                    }
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Không thể lấy dữ liệu doanh thu", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupBarChart(Map<String, Double> monthlyRevenue) {
        com.github.mikephil.charting.charts.BarChart barChart = getView().findViewById(R.id.barChartRevenue);
        if (barChart == null) return;

        List<com.github.mikephil.charting.data.BarEntry> entries = new ArrayList<>();
        
        // Month 1 to 12
        for (int i = 1; i <= 12; i++) {
            Double value = monthlyRevenue.get(String.valueOf(i));
            float val = value != null ? value.floatValue() : 0f;
            entries.add(new com.github.mikephil.charting.data.BarEntry(i, val));
        }

        com.github.mikephil.charting.data.BarDataSet dataSet = new com.github.mikephil.charting.data.BarDataSet(entries, "Doanh thu");
        dataSet.setColor(android.graphics.Color.parseColor("#009688"));
        dataSet.setValueTextColor(android.graphics.Color.BLACK);
        dataSet.setValueTextSize(10f);

        com.github.mikephil.charting.data.BarData barData = new com.github.mikephil.charting.data.BarData(dataSet);
        barChart.setData(barData);

        com.github.mikephil.charting.components.XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12);
        
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
