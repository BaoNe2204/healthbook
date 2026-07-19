package com.example.healthbook.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthbook.R;
import com.example.healthbook.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHospitalsFragment extends Fragment {

    private TextInputEditText etHospitalName, etHospitalAddress;
    private TextInputEditText etSpecialtyName, etSpecialtyDesc;
    private View btnAddHospital, btnAddSpecialty;

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

        btnAddHospital.setOnClickListener(v -> addHospital());
        btnAddSpecialty.setOnClickListener(v -> addSpecialty());

        return view;
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

        android.app.ProgressDialog progress = new android.app.ProgressDialog(getContext());
        progress.setMessage("Đang lưu bệnh viện...");
        progress.show();

        RetrofitClient.getInstance().getApiService().createHospital(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progress.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã thêm bệnh viện thành công!", Toast.LENGTH_SHORT).show();
                    etHospitalName.setText("");
                    etHospitalAddress.setText("");
                } else {
                    Toast.makeText(getContext(), "Không thể thêm bệnh viện. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        android.app.ProgressDialog progress = new android.app.ProgressDialog(getContext());
        progress.setMessage("Đang lưu chuyên khoa...");
        progress.show();

        RetrofitClient.getInstance().getApiService().createSpecialty(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progress.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã thêm chuyên khoa thành công!", Toast.LENGTH_SHORT).show();
                    etSpecialtyName.setText("");
                    etSpecialtyDesc.setText("");
                } else {
                    Toast.makeText(getContext(), "Không thể thêm chuyên khoa. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}