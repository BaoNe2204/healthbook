package com.example.healthbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.data.models.VaccineBooking;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class VaccineBookingAdapter extends RecyclerView.Adapter<VaccineBookingAdapter.ViewHolder> {
    private List<VaccineBooking> bookings;

    public VaccineBookingAdapter(List<VaccineBooking> bookings) {
        this.bookings = bookings;
    }

    public void updateData(List<VaccineBooking> newBookings) {
        this.bookings.clear();
        this.bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vaccination_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VaccineBooking booking = bookings.get(position);
        holder.tvVaccineName.setText(booking.getVaccine_name());
        holder.tvStatus.setText(booking.getStatus());
        holder.tvDate.setText(booking.getAppointment_date());
        holder.tvTime.setText(booking.getAppointment_time());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(format.format(booking.getPrice()));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVaccineName, tvStatus, tvDate, tvTime, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVaccineName = itemView.findViewById(R.id.tvVaccineName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
