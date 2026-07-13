package com.example.healthbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private List<String> days;
    private List<String> dates;
    private int selectedPosition = 0;

    public DateAdapter(List<String> days, List<String> dates) {
        this.days = days;
        this.dates = dates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDayOfWeek.setText(days.get(position));
        holder.tvDate.setText(dates.get(position));
        
        Context context = holder.itemView.getContext();

        if (selectedPosition == position) {
            holder.rootView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_rounded_primary));
            holder.tvDayOfWeek.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.tvDate.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.rootView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_rounded_white));
            holder.tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            int newPos = holder.getAdapterPosition();
            if (newPos != RecyclerView.NO_POSITION) {
                selectedPosition = newPos;
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        TextView tvDayOfWeek, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
