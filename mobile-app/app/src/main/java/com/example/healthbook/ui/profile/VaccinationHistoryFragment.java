package com.example.healthbook.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.VaccineBookingAdapter;
import com.example.healthbook.data.models.VaccineBooking;
import com.example.healthbook.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VaccinationHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private TextView tvEmpty;
    private VaccineBookingAdapter adapter;
    private List<VaccineBooking> bookings = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccination_history, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        rvHistory = view.findViewById(R.id.rvHistory);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VaccineBookingAdapter(bookings);
        rvHistory.setAdapter(adapter);

        loadVaccinationHistory();

        return view;
    }

    private void loadVaccinationHistory() {
        RetrofitClient.getInstance().getApiService().getVaccineBookings().enqueue(new Callback<List<VaccineBooking>>() {
            @Override
            public void onResponse(Call<List<VaccineBooking>> call, Response<List<VaccineBooking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookings = response.body();
                    adapter.updateData(bookings);

                    if (bookings.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvHistory.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvHistory.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải lịch sử tiêm chủng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VaccineBooking>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
