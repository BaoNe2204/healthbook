package com.example.healthbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
    private List<String> times;
    private int selectedPosition = 0;

    public TimeAdapter(List<String> times) {
        this.times = times;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTime.setText(times.get(position));
        
        Context context = holder.itemView.getContext();

        if (selectedPosition == position) {
            holder.tvTime.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_rounded_primary));
            holder.tvTime.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.tvTime.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_rounded_white));
            holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public String getSelectedTime() {
        if (selectedPosition >= 0 && selectedPosition < times.size()) {
            return times.get(selectedPosition);
        }
        return "";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
