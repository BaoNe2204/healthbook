package com.example.healthbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Clinic;
import java.util.List;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ViewHolder> {
    private List<Clinic> clinics;

    public ClinicAdapter(List<Clinic> clinics) {
        this.clinics = clinics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clinic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clinic clinic = clinics.get(position);
        holder.tvName.setText(clinic.getName());
        holder.tvAddress.setText(clinic.getAddress());

        // Set a random beautiful clinic image
        int[] images = {R.drawable.clinic_img_1, R.drawable.clinic_img_2, R.drawable.clinic_img_3};
        int randomImage = images[Math.abs(clinic.getName().hashCode()) % images.length];
        holder.ivHospitalImage.setImageResource(randomImage);

        holder.itemView.setOnClickListener(v -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putSerializable("clinic", clinic);
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.clinicProfileFragment, args);
        });
    }

    @Override
    public int getItemCount() {
        return clinics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress;
        android.widget.ImageView ivHospitalImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHospitalName);
            tvAddress = itemView.findViewById(R.id.tvHospitalAddress);
            ivHospitalImage = itemView.findViewById(R.id.ivHospitalImage);
        }
    }
}
