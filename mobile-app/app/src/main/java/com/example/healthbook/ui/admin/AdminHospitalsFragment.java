package com.example.healthbook.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.AdminCategoryAdapter;
import com.example.healthbook.data.models.Hospital;
import com.example.healthbook.data.models.Specialty;
import com.example.healthbook.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHospitalsFragment extends Fragment {

    private TextInputEditText etHospitalName, etHospitalAddress;
    private TextInputEditText etSpecialtyName, etSpecialtyDesc;
    private View btnAddHospital, btnAddSpecialty;

    private RecyclerView rvHospitals, rvSpecialties;
    private AdminCategoryAdapter hospitalAdapter, specialtyAdapter;
    private final List<AdminCategoryAdapter.Item> hospitalList = new ArrayList<>();
    private final List<AdminCategoryAdapter.Item> specialtyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_hospitals, container, false);

        etHospitalName = view.findViewById(R.id.etHospitalName);
        etHospitalAddress = view.findViewById(R.id.etHospitalAddress);
        etSpecialtyName = view.findViewById(R.id.etSpecialtyName);
        etSpecialtyDesc = view.findViewById(R.id.etSpecialtyDesc);

        btnAddHospital = view.findViewById(R.id.btnAddHospital);
        btnAddSpecialty = view.findViewById(R.id.btnAddSpecialty);

        rvHospitals = view.findViewById(R.id.rvAdminHospitals);
        rvSpecialties = view.findViewById(R.id.rvAdminSpecialties);

        rvHospitals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSpecialties.setLayoutManager(new LinearLayoutManager(getContext()));

        hospitalAdapter = new AdminCategoryAdapter(hospitalList, item -> deleteHospital(item));
        specialtyAdapter = new AdminCategoryAdapter(specialtyList, item -> deleteSpecialty(item));

        rvHospitals.setAdapter(hospitalAdapter);
        rvSpecialties.setAdapter(specialtyAdapter);

        btnAddHospital.setOnClickListener(v -> addHospital());
        btnAddSpecialty.setOnClickListener(v -> addSpecialty());

        loadHospitals();
        loadSpecialties();

        return view;
    }

    private void loadHospitals() {
        RetrofitClient.getInstance().getApiService().getHospitals().enqueue(new Callback<List<Hospital>>() {
            @Override
            public void onResponse(Call<List<Hospital>> call, Response<List<Hospital>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hospitalList.clear();
                    for (Hospital h : response.body()) {
                        hospitalList.add(new AdminCategoryAdapter.Item(h.getId(), h.getName(), h.getAddress()));
                    }
                    hospitalAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Hospital>> call, Throwable t) {
            }
        });
    }

    private void loadSpecialties() {
        RetrofitClient.getInstance().getApiService().getSpecialties().enqueue(new Callback<List<Specialty>>() {
            @Override
            public void onResponse(Call<List<Specialty>> call, Response<List<Specialty>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    specialtyList.clear();
                    for (Specialty s : response.body()) {
                        specialtyList.add(new AdminCategoryAdapter.Item(s.getId(), s.getName(), s.getDescription()));
                    }
                    specialtyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Specialty>> call, Throwable t) {
            }
        });
    }

    private void addHospital() {
        String name = etHospitalName.getText().toString().trim();
        String address = etHospitalAddress.getText().toString().trim();

        if (name.isEmpty()) {
            etHospitalName.setError("Vui lòng nhập tên bệnh viện");
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("address", address);

        RetrofitClient.getInstance().getApiService().createHospital(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã thêm bệnh viện thành công!", Toast.LENGTH_SHORT).show();
                    etHospitalName.setText("");
                    etHospitalAddress.setText("");
                    loadHospitals();
                } else {
                    Toast.makeText(getContext(), "Không thể thêm bệnh viện. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteHospital(AdminCategoryAdapter.Item item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bệnh viện \"" + item.title + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    RetrofitClient.getInstance().getApiService().deleteHospital(item.id).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Đã xóa bệnh viện thành công!", Toast.LENGTH_SHORT).show();
                                loadHospitals();
                            } else {
                                Toast.makeText(getContext(), "Không thể xóa. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addSpecialty() {
        String name = etSpecialtyName.getText().toString().trim();
        String desc = etSpecialtyDesc.getText().toString().trim();

        if (name.isEmpty()) {
            etSpecialtyName.setError("Vui lòng nhập tên chuyên khoa");
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("description", desc);

        RetrofitClient.getInstance().getApiService().createSpecialty(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã thêm chuyên khoa thành công!", Toast.LENGTH_SHORT).show();
                    etSpecialtyName.setText("");
                    etSpecialtyDesc.setText("");
                    loadSpecialties();
                } else {
                    Toast.makeText(getContext(), "Không thể thêm chuyên khoa. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSpecialty(AdminCategoryAdapter.Item item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa chuyên khoa \"" + item.title + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    RetrofitClient.getInstance().getApiService().deleteSpecialty(item.id).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Đã xóa chuyên khoa thành công!", Toast.LENGTH_SHORT).show();
                                loadSpecialties();
                            } else {
                                Toast.makeText(getContext(), "Không thể xóa. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}