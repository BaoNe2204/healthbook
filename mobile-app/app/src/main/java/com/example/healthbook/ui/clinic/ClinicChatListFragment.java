package com.example.healthbook.ui.clinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.ClinicChatListAdapter;
import com.example.healthbook.data.models.Patient;
import com.example.healthbook.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicChatListFragment extends Fragment {

    private RecyclerView rvPatients;
    private ClinicChatListAdapter adapter;
    private List<Patient> patientList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clinic_chat_list, container, false);

        rvPatients = view.findViewById(R.id.rvPatients);
        rvPatients.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClinicChatListAdapter(getContext(), patientList);
        rvPatients.setAdapter(adapter);

        fetchPatients();

        return view;
    }

    private void fetchPatients() {
        RetrofitClient.getInstance().getApiService().getClinicPatients().enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientList.clear();
                    patientList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Không thể tải danh sách bệnh nhân", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Patient>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
