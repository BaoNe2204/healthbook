package com.example.healthbook.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Clinic;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ClinicSearchAdapter extends RecyclerView.Adapter<ClinicSearchAdapter.ViewHolder> {
    private List<Clinic> clinics;

    public ClinicSearchAdapter(List<Clinic> clinics) {
        this.clinics = clinics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clinic_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clinic clinic = clinics.get(position);
        holder.tvName.setText(clinic.getName());
        holder.tvAddress.setText(clinic.getAddress());

        // Dummy specialties for UI
        holder.chipGroupSpecialties.removeAllViews();
        List<String> mockSpecialties = Arrays.asList("Đa khoa", "Sản - Phụ khoa", "Da liễu", "Nhi khoa", "Nội tiết");
        Random random = new Random(clinic.getName().hashCode());
        int numSpecialties = 1 + random.nextInt(3);
        Context context = holder.itemView.getContext();
        
        for (int i = 0; i < numSpecialties; i++) {
            TextView chip = new TextView(context);
            chip.setText(mockSpecialties.get(random.nextInt(mockSpecialties.size())));
            chip.setTextColor(Color.parseColor("#424242"));
            chip.setTextSize(12f);
            chip.setBackgroundResource(R.drawable.bg_rounded_white);
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F5F5F5")));
            
            int paddingH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
            int paddingV = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
            chip.setPadding(paddingH, paddingV, paddingH, paddingV);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()));
            chip.setLayoutParams(params);
            
            holder.chipGroupSpecialties.addView(chip);
        }

        View.OnClickListener clickListener = v -> {
            Bundle args = new Bundle();
            args.putSerializable("clinic", clinic);
            Navigation.findNavController(v).navigate(R.id.clinicProfileFragment, args);
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.btnBook.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return clinics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress;
        LinearLayout chipGroupSpecialties;
        View btnBook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHospitalName);
            tvAddress = itemView.findViewById(R.id.tvHospitalAddress);
            chipGroupSpecialties = itemView.findViewById(R.id.chipGroupSpecialties);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
