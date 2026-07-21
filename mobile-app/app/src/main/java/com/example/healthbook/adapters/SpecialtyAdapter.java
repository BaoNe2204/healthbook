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

        int iconRes = android.R.drawable.ic_menu_compass;
        String name = specialty.getName() != null ? specialty.getName().toLowerCase() : "";
        if (name.contains("tim")) {
            iconRes = android.R.drawable.btn_star_big_on;
        } else if (name.contains("nhi")) {
            iconRes = android.R.drawable.ic_menu_myplaces;
        } else if (name.contains("da")) {
            iconRes = android.R.drawable.ic_menu_camera;
        } else if (name.contains("sản")) {
            iconRes = android.R.drawable.ic_menu_view;
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
