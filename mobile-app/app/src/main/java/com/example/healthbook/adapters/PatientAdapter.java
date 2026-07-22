package com.example.healthbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Patient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private Context context;
    private List<Patient> patientList;
    private List<Patient> patientListFull;
    private OnPatientClickListener listener;

    public interface OnPatientClickListener {
        void onViewHistory(Patient patient);
        void onChat(Patient patient);
    }

    public PatientAdapter(Context context, List<Patient> patientList, OnPatientClickListener listener) {
        this.context = context;
        this.patientList = patientList;
        this.patientListFull = new ArrayList<>(patientList);
        this.listener = listener;
    }

    public void updateData(List<Patient> newList) {
        this.patientList = newList;
        this.patientListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        patientList.clear();
        if (text.isEmpty()) {
            patientList.addAll(patientListFull);
        } else {
            text = text.toLowerCase();
            for (Patient item : patientListFull) {
                if (item.getName() != null && item.getName().toLowerCase().contains(text) ||
                    item.getPhone() != null && item.getPhone().toLowerCase().contains(text)) {
                    patientList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        
        holder.tvName.setText(patient.getName());
        holder.tvPhone.setText(patient.getPhone() != null && !patient.getPhone().isEmpty() ? patient.getPhone() : "Chưa cập nhật SĐT");
        
        String gender = patient.getGender() != null ? patient.getGender() : "Khác";
        String dob = patient.getDob() != null ? patient.getDob() : "Chưa cập nhật";
        holder.tvInfo.setText(gender + " - " + dob);

        if (patient.getAvatarUrl() != null && !patient.getAvatarUrl().isEmpty()) {
            Glide.with(context)
                 .load(patient.getAvatarUrl())
                 .placeholder(R.drawable.avatar_placeholder)
                 .error(R.drawable.avatar_placeholder)
                 .circleCrop()
                 .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
        }

        holder.btnViewHistory.setOnClickListener(v -> {
            if (listener != null) listener.onViewHistory(patient);
        });

        holder.btnChat.setOnClickListener(v -> {
            if (listener != null) listener.onChat(patient);
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvInfo, tvPhone;
        MaterialButton btnViewHistory, btnChat;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivPatientAvatar);
            tvName = itemView.findViewById(R.id.tvPatientName);
            tvInfo = itemView.findViewById(R.id.tvPatientInfo);
            tvPhone = itemView.findViewById(R.id.tvPatientPhone);
            btnViewHistory = itemView.findViewById(R.id.btnViewHistory);
            btnChat = itemView.findViewById(R.id.btnChat);
        }
    }
}
