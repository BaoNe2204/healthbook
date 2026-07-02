package com.example.healthbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Appointment;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<Appointment> appointments;

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        
        String[] dateParts = appointment.getDate().split("/");
        if (dateParts.length == 3) {
            holder.tvDay.setText(dateParts[0]);
            holder.tvMonth.setText("Tháng " + dateParts[1]);
        } else {
            holder.tvDay.setText(appointment.getDate());
            holder.tvMonth.setText("");
        }

        holder.tvDoctorName.setText(appointment.getDoctor().getName());
        holder.tvSpecialty.setText(appointment.getDoctor().getSpecialty() + " - " + appointment.getDoctor().getHospital());
        holder.tvTime.setText(appointment.getTime());
        holder.tvStatus.setText(appointment.getStatus());
        
        // Update color based on status
        if ("Sắp tới".equals(appointment.getStatus()) || "Đã duyệt".equals(appointment.getStatus())) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
        } else if ("Đã qua".equals(appointment.getStatus())) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#9E9E9E")); // Gray
        } else {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800")); // Orange (Chờ duyệt)
        }

        holder.itemView.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.appointmentDetailFragment);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvMonth, tvDoctorName, tvSpecialty, tvTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvApptDay);
            tvMonth = itemView.findViewById(R.id.tvApptMonth);
            tvDoctorName = itemView.findViewById(R.id.tvApptDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvApptSpecialty);
            tvTime = itemView.findViewById(R.id.tvApptTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
