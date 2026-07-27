package com.example.healthbook.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.data.ApiRepository;
import com.example.healthbook.data.models.Appointment;

import java.util.List;

public class ClinicAppointmentAdapter extends RecyclerView.Adapter<ClinicAppointmentAdapter.ViewHolder> {

    private List<Appointment> list;
    private Runnable refreshCallback;

    public ClinicAppointmentAdapter(List<Appointment> list, Runnable refreshCallback) {
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
        holder.tvApptDateTime.setText("📅 " + app.getDate() + " - 🕒 " + app.getTime() + "\n👨‍⚕️ Bác sĩ: " + app.getDoctorName());
        holder.tvPatientDetails.setText((app.getPatient_gender() != null ? app.getPatient_gender() : "") 
                + " - 🎂 " + (app.getPatient_dob() != null ? app.getPatient_dob() : ""));
        holder.tvApptStatus.setText(app.getStatus());

        // Update colors and actions visibility
        String status = app.getStatus();
        holder.btnCancel.setVisibility(View.VISIBLE); // Reset visibility for recycling

        if ("Sắp tới".equalsIgnoreCase(status) || "Chờ duyệt".equalsIgnoreCase(status)) {
            holder.tvApptStatus.setTextColor(Color.parseColor("#FF9800"));
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnApprove.setText("XÁC NHẬN");
            holder.btnCancel.setText("HỦY ĐƠN");
            holder.btnApprove.setOnClickListener(v -> updateStatus(context, app.getId(), "Đã duyệt"));
            holder.btnCancel.setOnClickListener(v -> updateStatus(context, app.getId(), "Đã hủy"));
        } else if ("Đã duyệt".equalsIgnoreCase(status)) {
            holder.tvApptStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnApprove.setText("HOÀN THÀNH");
            holder.btnApprove.setOnClickListener(v -> updateStatus(context, app.getId(), "Đã hoàn thành"));
            holder.btnCancel.setVisibility(View.GONE);
        } else if ("Yêu cầu hủy".equalsIgnoreCase(status)) {
            holder.tvApptStatus.setText(status + (app.getCancelReason() != null ? " (Lý do: " + app.getCancelReason() + ")" : ""));
            holder.tvApptStatus.setTextColor(Color.parseColor("#E91E63")); // Pink/Red indicating pending cancellation
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnApprove.setText("ĐỒNG Ý HỦY");
            holder.btnCancel.setText("TỪ CHỐI");
            holder.btnApprove.setOnClickListener(v -> updateStatus(context, app.getId(), "Đã hủy"));
            holder.btnCancel.setOnClickListener(v -> updateStatus(context, app.getId(), "Đã duyệt"));
        } else {
            holder.tvApptStatus.setTextColor(Color.parseColor("#9E9E9E"));
            holder.layoutActions.setVisibility(View.GONE);
        }
        
        holder.btnHistory.setVisibility(View.GONE);
        holder.btnExamine.setVisibility(View.GONE);
    }

    private void updateStatus(Context context, String appointmentId, String status) {
        new ApiRepository().updateClinicAppointmentStatus(appointmentId, status, new ApiRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(context, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                if (refreshCallback != null) refreshCallback.run();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Thao tác thất bại. Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
