package com.example.healthbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.data.models.Patient;
import com.example.healthbook.ui.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ClinicChatListAdapter extends RecyclerView.Adapter<ClinicChatListAdapter.ViewHolder> {

    private final Context context;
    private final List<Patient> patientList;

    public ClinicChatListAdapter(Context context, List<Patient> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_clinic_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        
        holder.tvPatientName.setText(patient.getName());
        holder.tvPatientPhone.setText(patient.getPhone() != null && !patient.getPhone().isEmpty() ? patient.getPhone() : "Chưa có SĐT");
        
        holder.itemView.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String patientId = patient.getId();
            // In Healthbook, chatId is minId_maxId
            String chatId = currentUserId.compareTo(patientId) < 0 
                    ? currentUserId + "_" + patientId 
                    : patientId + "_" + currentUserId;
                    
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("OTHER_USER_ID", patientId);
            intent.putExtra("OTHER_USER_NAME", patient.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvPatientPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientPhone = itemView.findViewById(R.id.tvPatientPhone);
        }
    }
}
