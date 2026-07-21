package com.example.healthbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Doctor;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {
    private List<Doctor> doctors;
    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onBookClick(Doctor doctor);
    }

    public DoctorAdapter(List<Doctor> doctors, OnDoctorClickListener listener) {
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.tvName.setText(doctor.getName());
        holder.tvSpecialty.setText(doctor.getSpecialty());
        holder.tvHospital.setText(doctor.getHospital());
        holder.tvRating.setText(String.valueOf(doctor.getRating()));
        holder.tvReviews.setText(doctor.getReviewCount() + "+ lượt khám");

        if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(doctor.getImageUrl())
                    .circleCrop()
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(holder.ivAvatar);
        }

        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(doctor);
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty, tvHospital, tvRating, tvReviews;
        ImageView ivAvatar;
        View btnBook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvHospital = itemView.findViewById(R.id.tvDoctorHospital);
            tvRating = itemView.findViewById(R.id.tvDoctorRating);
            tvReviews = itemView.findViewById(R.id.tvDoctorReviews);
            ivAvatar = itemView.findViewById(R.id.ivDoctorAvatar);
            btnBook = itemView.findViewById(R.id.btnBookDoctor);
        }
    }
}
