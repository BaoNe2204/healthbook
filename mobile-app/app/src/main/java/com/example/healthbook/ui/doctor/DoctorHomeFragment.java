package com.example.healthbook.ui.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.AppointmentAdapter;
import com.example.healthbook.data.models.Appointment;
import com.example.healthbook.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorHomeFragment extends Fragment {

    private TextView tvDoctorName;
    private TextView tvTodayCount, tvPendingCount;
    private RecyclerView rvAppointments;
    private TextView tvEmptyToday;
    private List<Appointment> todayAppointments = new ArrayList<>();
    private AppointmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);

        tvDoctorName = view.findViewById(R.id.tvDoctorHomeName);
        tvTodayCount = view.findViewById(R.id.tvTodayCount);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        rvAppointments = view.findViewById(R.id.rvTodayAppointments);
        tvEmptyToday = view.findViewById(R.id.tvEmptyToday);

        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AppointmentAdapter(todayAppointments);
        rvAppointments.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if (name != null && !name.isEmpty()) {
                tvDoctorName.setText("BS. " + name);
            }
        }

        loadTodayAppointments();

        return view;
    }

    private void loadTodayAppointments() {
        RetrofitClient.getInstance().getApiService().getDoctorAppointments().enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Appointment> list = response.body();
                    filterTodayAppointments(list);
                } else {
                    tvEmptyToday.setVisibility(View.VISIBLE);
                    tvEmptyToday.setText("Không thể lấy dữ liệu từ máy chủ.");
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                tvEmptyToday.setVisibility(View.VISIBLE);
                tvEmptyToday.setText("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void filterTodayAppointments(List<Appointment> all) {
        todayAppointments.clear();
        String todayStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        
        int pending = 0;
        for (Appointment app : all) {
            // Check status for counts
            if ("Chờ duyệt".equalsIgnoreCase(app.getStatus())) {
                pending++;
            }
            
            // Filter to today
            if (todayStr.equals(app.getDate())) {
                todayAppointments.add(app);
            }
        }

        tvTodayCount.setText(String.valueOf(todayAppointments.size()));
        tvPendingCount.setText(String.valueOf(pending));

        adapter.notifyDataSetChanged();

        if (todayAppointments.isEmpty()) {
            tvEmptyToday.setVisibility(View.VISIBLE);
            rvAppointments.setVisibility(View.GONE);
        } else {
            tvEmptyToday.setVisibility(View.GONE);
            rvAppointments.setVisibility(View.VISIBLE);
        }
    }
}
