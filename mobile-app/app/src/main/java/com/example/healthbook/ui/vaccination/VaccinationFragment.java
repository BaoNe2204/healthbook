package com.example.healthbook.ui.vaccination;

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
import com.example.healthbook.adapters.VaccineAdapter;
import com.example.healthbook.data.models.Vaccine;
import com.example.healthbook.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VaccinationFragment extends Fragment {

    private RecyclerView rvVaccines;
    private VaccineAdapter adapter;
    private List<Vaccine> vaccineList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccination, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        rvVaccines = view.findViewById(R.id.rvVaccines);
        rvVaccines.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VaccineAdapter(vaccineList);
        rvVaccines.setAdapter(adapter);

        loadVaccines();

        return view;
    }

    private void loadVaccines() {
        RetrofitClient.getInstance().getApiService().getVaccines().enqueue(new Callback<List<Vaccine>>() {
            @Override
            public void onResponse(Call<List<Vaccine>> call, Response<List<Vaccine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vaccineList.clear();
                    vaccineList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách vắc xin.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Vaccine>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
