package com.example.healthbook.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.MedicalRecordAdapter;
import com.example.healthbook.data.models.MedicalRecord;
import com.example.healthbook.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalHistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private MedicalRecordAdapter adapter;
    private List<MedicalRecord> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_history, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        rvHistory = view.findViewById(R.id.rvMedicalHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicalRecordAdapter(list);
        rvHistory.setAdapter(adapter);

        loadMedicalHistory();

        return view;
    }

    private void loadMedicalHistory() {
        RetrofitClient.getInstance().getApiService().getMedicalRecords().enqueue(new Callback<List<MedicalRecord>>() {
            @Override
            public void onResponse(Call<List<MedicalRecord>> call, Response<List<MedicalRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải lịch sử bệnh án.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MedicalRecord>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
