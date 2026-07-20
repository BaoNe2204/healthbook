package com.example.healthbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.data.models.MedicalRecord;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private List<MedicalRecord> list;

    public MedicalRecordAdapter(List<MedicalRecord> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicalRecord record = list.get(position);

        holder.tvDiagnosis.setText("Chẩn đoán: " + record.getDiagnosis());
        holder.tvDoctorInfo.setText("BS. " + record.getDoctorName() + " - " + record.getSpecialty());
        holder.tvHospital.setText(record.getHospital());
        holder.tvRecordDateTime.setText("📅 Ngày khám: " + record.getAppointmentDate() + " - 🕒 " + record.getAppointmentTime());
        holder.tvPrescription.setText(record.getPrescription() != null && !record.getPrescription().isEmpty() ? record.getPrescription() : "Không có thuốc kê");
        holder.tvNotes.setText("📝 Lời dặn: " + (record.getNotes() != null && !record.getNotes().isEmpty() ? record.getNotes() : "Nghỉ ngơi và tái khám nếu cần"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiagnosis, tvDoctorInfo, tvHospital, tvRecordDateTime, tvPrescription, tvNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            tvDoctorInfo = itemView.findViewById(R.id.tvDoctorInfo);
            tvHospital = itemView.findViewById(R.id.tvHospital);
            tvRecordDateTime = itemView.findViewById(R.id.tvRecordDateTime);
            tvPrescription = itemView.findViewById(R.id.tvPrescription);
            tvNotes = itemView.findViewById(R.id.tvNotes);
        }
    }
}
