package com.example.healthbook.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthbook.R;
import com.example.healthbook.network.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {

    private TextView tvUsers, tvAppts, tvDoctors, tvPatients;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        tvUsers = view.findViewById(R.id.tvStatTotalUsers);
        tvAppts = view.findViewById(R.id.tvStatTotalAppts);
        tvDoctors = view.findViewById(R.id.tvStatTotalDoctors);
        tvPatients = view.findViewById(R.id.tvStatTotalPatients);

        loadDashboardStats();

        return view;
    }

    private void loadDashboardStats() {
        RetrofitClient.getInstance().getApiService().getAdminDashboard().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> stats = response.body();
                    
                    // Retrofit maps numbers to double values from JSON. Let's parse them safely.
                    tvUsers.setText(getStatVal(stats.get("totalUsers")));
                    tvAppts.setText(getStatVal(stats.get("totalAppointments")));
                    tvDoctors.setText(getStatVal(stats.get("totalDoctors")));
                    tvPatients.setText(getStatVal(stats.get("totalPatients")));
                } else {
                    Toast.makeText(getContext(), "Không thể tải số liệu thống kê.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getStatVal(Object val) {
        if (val == null) return "0";
        if (val instanceof Double) {
            return String.valueOf(((Double) val).intValue());
        }
        return String.valueOf(val);
    }
}
