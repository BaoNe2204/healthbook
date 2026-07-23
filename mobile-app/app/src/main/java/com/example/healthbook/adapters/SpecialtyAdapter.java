package com.example.healthbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Specialty;
import java.util.List;

public class SpecialtyAdapter extends RecyclerView.Adapter<SpecialtyAdapter.ViewHolder> {
    private List<Specialty> specialties;

    public SpecialtyAdapter(List<Specialty> specialties) {
        this.specialties = specialties;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_specialty, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Specialty specialty = specialties.get(position);
        holder.tvName.setText(specialty.getName());

        String name = specialty.getName() != null ? specialty.getName().toLowerCase() : "";
        int iconRes = R.drawable.ic_specialty_heart; // Default
        
        if (name.contains("tim")) {
            iconRes = R.drawable.ic_specialty_heart;
        } else if (name.contains("nhi")) {
            iconRes = R.drawable.ic_specialty_child;
        } else if (name.contains("da")) {
            iconRes = R.drawable.ic_specialty_face;
        } else if (name.contains("sản")) {
            iconRes = R.drawable.ic_specialty_pregnant;
        } else if (name.contains("răng") || name.contains("nha")) {
            iconRes = R.drawable.ic_specialty_smile;
        } else if (name.contains("mũi") || name.contains("tai")) {
            iconRes = R.drawable.ic_specialty_ear;
        }

        holder.ivIcon.setImageResource(iconRes);
        
        holder.itemView.setOnClickListener(v -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putString("specialtyName", specialty.getName());
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.specialtyDetailFragment, args);
        });
    }

    @Override
    public int getItemCount() {
        return specialties.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSpecialtyName);
            ivIcon = itemView.findViewById(R.id.ivSpecialtyIcon);
        }
    }
}
