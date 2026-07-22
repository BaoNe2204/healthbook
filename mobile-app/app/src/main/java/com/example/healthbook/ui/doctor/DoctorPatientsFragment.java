package com.example.healthbook.ui.doctor;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.PatientAdapter;
import com.example.healthbook.data.models.MedicalRecord;
import com.example.healthbook.data.models.Patient;
import com.example.healthbook.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorPatientsFragment extends Fragment {

    private RecyclerView rvPatients;
    private PatientAdapter adapter;
    private TextInputEditText etSearch;
    private TextView tvPatientCount, tvEmpty;
    private List<Patient> patientList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_patients, container, false);

        rvPatients = view.findViewById(R.id.rvPatients);
        etSearch = view.findViewById(R.id.etSearch);
        tvPatientCount = view.findViewById(R.id.tvPatientCount);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        rvPatients.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PatientAdapter(getContext(), patientList, new PatientAdapter.OnPatientClickListener() {
            @Override
            public void onViewHistory(Patient patient) {
                showPatientHistory(patient);
            }

            @Override
            public void onChat(Patient patient) {
                android.content.Intent intent = new android.content.Intent(getContext(), com.example.healthbook.ui.chat.ChatActivity.class);
                intent.putExtra("OTHER_USER_ID", patient.getId());
                intent.putExtra("OTHER_USER_NAME", patient.getName());
                startActivity(intent);
            }
        });
        rvPatients.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                updateEmptyState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadPatients();

        return view;
    }

    private void loadPatients() {
        RetrofitClient.getInstance().getApiService().getDoctorMyPatients().enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientList = response.body();
                    adapter.updateData(patientList);
                    tvPatientCount.setText("Tổng số: " + patientList.size() + " bệnh nhân");
                    updateEmptyState();
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách bệnh nhân", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Patient>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvPatients.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvPatients.setVisibility(View.VISIBLE);
        }
    }

    private void showPatientHistory(Patient patient) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_patient_history);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = dialog.findViewById(R.id.tvHistoryTitle);
        tvTitle.setText("Tiền sử bệnh: " + patient.getName());

        RecyclerView rvHistory = dialog.findViewById(R.id.rvPatientHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnClose = dialog.findViewById(R.id.btnCloseHistory);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        RetrofitClient.getInstance().getApiService().getPatientMedicalRecords(patient.getId()).enqueue(new Callback<List<MedicalRecord>>() {
            @Override
            public void onResponse(Call<List<MedicalRecord>> call, Response<List<MedicalRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MedicalRecord> records = response.body();
                    rvHistory.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_patient_history, parent, false);
                            return new RecyclerView.ViewHolder(view) {};
                        }
                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                            MedicalRecord record = records.get(position);
                            TextView tvDate = holder.itemView.findViewById(R.id.tvHistoryDate);
                            TextView tvDiagnosis = holder.itemView.findViewById(R.id.tvHistoryDiagnosis);
                            TextView tvPrescription = holder.itemView.findViewById(R.id.tvHistoryPrescription);
                            
                            try {
                                java.text.SimpleDateFormat inFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                                java.text.SimpleDateFormat outFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                                java.util.Date d = inFormat.parse(record.getCreated_at());
                                tvDate.setText("Khám ngày: " + outFormat.format(d));
                            } catch (Exception e) {
                                tvDate.setText("Khám ngày: " + record.getCreated_at());
                            }
                            
                            tvDiagnosis.setText("Chẩn đoán: " + record.getDiagnosis());
                            tvPrescription.setText("Đơn thuốc: " + record.getPrescription());
                        }
                        @Override
                        public int getItemCount() {
                            return records.size();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<MedicalRecord>> call, Throwable t) {}
        });

        dialog.show();
    }
}
