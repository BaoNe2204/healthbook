package com.example.healthbook.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.data.models.Appointment;
import com.example.healthbook.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder> {

    private List<Appointment> list;
    private Runnable refreshCallback;

    public DoctorAppointmentAdapter(List<Appointment> list, Runnable refreshCallback) {
        this.list = list;
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment app = list.get(position);
        Context context = holder.itemView.getContext();

        holder.tvPatientName.setText(app.getPatient_name() != null ? app.getPatient_name() : "Bệnh nhân ẩn danh");
        holder.tvApptDateTime.setText("📅 " + app.getDate() + " - 🕒 " + app.getTime());
        holder.tvPatientDetails.setText((app.getPatient_gender() != null ? app.getPatient_gender() : "") 
                + " - 🎂 " + (app.getPatient_dob() != null ? app.getPatient_dob() : ""));
        holder.tvApptStatus.setText(app.getStatus());

        // Update colors and actions visibility
        String status = app.getStatus();
        if ("Chờ duyệt".equalsIgnoreCase(status)) {
            holder.tvApptStatus.setTextColor(Color.parseColor("#FF9800"));
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnExamine.setVisibility(View.GONE);
        } else if ("Đã duyệt".equalsIgnoreCase(status) || "Sắp tới".equalsIgnoreCase(status)) {
            holder.tvApptStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.layoutActions.setVisibility(View.GONE);
            holder.btnExamine.setVisibility(View.VISIBLE);
        } else {
            holder.tvApptStatus.setTextColor(Color.parseColor("#9E9E9E"));
            holder.layoutActions.setVisibility(View.GONE);
            holder.btnExamine.setVisibility(View.GONE);
        }

        // Click handlers for status update
        holder.btnApprove.setOnClickListener(v -> updateStatus(context, app.getId(), "confirmed"));
        holder.btnCancel.setOnClickListener(v -> updateStatus(context, app.getId(), "cancelled"));

        // Click handler for examining/prescribing
        holder.btnExamine.setOnClickListener(v -> showExamineDialog(context, app));

        // Click handler for viewing patient history
        holder.btnHistory.setOnClickListener(v -> showHistoryDialog(context, app.getPatient_id(), app.getPatient_name()));
    }

    private void updateStatus(Context context, String appointmentId, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);

        RetrofitClient.getInstance().getApiService().updateAppointmentStatus(appointmentId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                    if (refreshCallback != null) refreshCallback.run();
                } else {
                    Toast.makeText(context, "Thao tác thất bại. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showExamineDialog(Context context, Appointment app) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_profile); // We borrow this dialog structure or inflate a simple layout
        
        // Since we want a custom layout for medical prescription, let's create a beautiful custom layout programmatically for simplicity and robust compiling
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48);
        layout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(context);
        title.setText("Bệnh án & Đơn thuốc");
        title.setTextSize(18f);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 24);
        layout.addView(title);

        EditText etDiagnosis = new EditText(context);
        etDiagnosis.setHint("Chẩn đoán bệnh (bắt buộc)");
        layout.addView(etDiagnosis);

        EditText etPrescription = new EditText(context);
        etPrescription.setHint("Đơn thuốc (ví dụ: Paracetamol 500mg, uống 2 lần/ngày)");
        etPrescription.setMinLines(2);
        layout.addView(etPrescription);

        EditText etNotes = new EditText(context);
        etNotes.setHint("Ghi chú/Lời dặn thêm");
        etNotes.setMinLines(2);
        layout.addView(etNotes);

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.END);
        buttons.setPadding(0, 24, 0, 0);

        Button btnCancel = new Button(context);
        btnCancel.setText("Hủy");
        btnCancel.setBackgroundColor(Color.TRANSPARENT);
        btnCancel.setTextColor(Color.GRAY);
        buttons.addView(btnCancel);

        Button btnSave = new Button(context);
        btnSave.setText("Hoàn thành");
        buttons.addView(btnSave);

        layout.addView(buttons);

        dialog.setContentView(layout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String diagnosis = etDiagnosis.getText().toString().trim();
            String prescription = etPrescription.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            if (diagnosis.isEmpty()) {
                etDiagnosis.setError("Vui lòng nhập chẩn đoán!");
                return;
            }

            Map<String, Object> body = new HashMap<>();
            body.put("patientId", app.getPatient_id() != null ? app.getPatient_id() : app.getId());
            body.put("appointmentId", app.getId());
            body.put("diagnosis", diagnosis);
            body.put("prescription", prescription);
            body.put("notes", notes);

            android.app.ProgressDialog progress = new android.app.ProgressDialog(context);
            progress.setMessage("Đang lưu hồ sơ bệnh án...");
            progress.setCancelable(false);
            progress.show();

            RetrofitClient.getInstance().getApiService().createMedicalRecord(body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    progress.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Kê đơn hoàn tất!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if (refreshCallback != null) refreshCallback.run();
                    } else {
                        Toast.makeText(context, "Không thể lưu bệnh án. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    progress.dismiss();
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showHistoryDialog(Context context, String patientId, String patientName) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_patient_history);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = dialog.findViewById(R.id.tvHistoryTitle);
        tvTitle.setText("Tiền sử bệnh: " + (patientName != null ? patientName : ""));

        RecyclerView rvHistory = dialog.findViewById(R.id.rvPatientHistory);
        rvHistory.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));

        Button btnClose = dialog.findViewById(R.id.btnCloseHistory);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        RetrofitClient.getInstance().getApiService().getPatientMedicalRecords(patientId).enqueue(new Callback<List<com.example.healthbook.data.models.MedicalRecord>>() {
            @Override
            public void onResponse(Call<List<com.example.healthbook.data.models.MedicalRecord>> call, Response<List<com.example.healthbook.data.models.MedicalRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.healthbook.data.models.MedicalRecord> records = response.body();
                    rvHistory.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(context).inflate(R.layout.item_patient_history, parent, false);
                            return new RecyclerView.ViewHolder(view) {};
                        }
                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                            com.example.healthbook.data.models.MedicalRecord record = records.get(position);
                            TextView tvDate = holder.itemView.findViewById(R.id.tvHistoryDate);
                            TextView tvDiagnosis = holder.itemView.findViewById(R.id.tvHistoryDiagnosis);
                            TextView tvPrescription = holder.itemView.findViewById(R.id.tvHistoryPrescription);
                            
                            // Try to format date
                            try {
                                java.text.SimpleDateFormat inFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                                java.text.SimpleDateFormat outFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                                java.util.Date d = inFormat.parse(record.getCreated_at());
                                tvDate.setText("Khám ngày: " + outFormat.format(d));
                            } catch (Exception e) {
                                tvDate.setText("Ngày khám: " + record.getCreated_at());
                            }
                            
                            tvDiagnosis.setText("Chẩn đoán: " + record.getDiagnosis());
                            tvPrescription.setText("Đơn thuốc: " + record.getPrescription());
                        }
                        @Override
                        public int getItemCount() { return records.size(); }
                    });
                    if (records.isEmpty()) {
                        Toast.makeText(context, "Bệnh nhân này chưa có tiền sử bệnh án nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<com.example.healthbook.data.models.MedicalRecord>> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvApptDateTime, tvPatientDetails, tvApptStatus;
        View layoutActions;
        Button btnCancel, btnApprove, btnExamine, btnHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvApptDateTime = itemView.findViewById(R.id.tvApptDateTime);
            tvPatientDetails = itemView.findViewById(R.id.tvPatientDetails);
            tvApptStatus = itemView.findViewById(R.id.tvApptStatus);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnExamine = itemView.findViewById(R.id.btnExamine);
            btnHistory = itemView.findViewById(R.id.btnHistory);
        }
    }
}
