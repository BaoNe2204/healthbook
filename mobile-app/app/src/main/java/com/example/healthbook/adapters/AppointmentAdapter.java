package com.example.healthbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Appointment;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<Appointment> appointments;

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        
        String[] dateParts = appointment.getDate().split("/");
        if (dateParts.length == 3) {
            holder.tvDay.setText(dateParts[0]);
            holder.tvMonth.setText("Tháng " + dateParts[1]);
        } else {
            holder.tvDay.setText(appointment.getDate());
            holder.tvMonth.setText("");
        }

        holder.tvDoctorName.setText(appointment.getDoctor().getName());
        holder.tvSpecialty.setText(appointment.getDoctor().getSpecialty() + " - " + appointment.getDoctor().getHospital());
        holder.tvTime.setText(appointment.getTime());
        holder.tvStatus.setText(appointment.getStatus());
        
        // Update color based on status
        if ("Sắp tới".equals(appointment.getStatus()) || "Đã duyệt".equals(appointment.getStatus())) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
        } else if ("Đã qua".equals(appointment.getStatus())) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#9E9E9E")); // Gray
        } else if ("Đã hủy".equals(appointment.getStatus())) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#F44336")); // Red
        } else {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800")); // Orange (Chờ duyệt)
        }

        // Review button
        if ("Đã qua".equals(appointment.getStatus())) {
            holder.btnReview.setVisibility(View.VISIBLE);
            holder.btnReview.setOnClickListener(v -> showReviewDialog(v.getContext(), appointment));
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putSerializable("appointment", appointment);
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.appointmentDetailFragment, bundle);
        });
    }

    private void showReviewDialog(android.content.Context context, Appointment appointment) {
        android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        android.widget.RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        android.widget.EditText etComment = dialog.findViewById(R.id.etReviewComment);
        android.widget.Button btnCancel = dialog.findViewById(R.id.btnCancelReview);
        android.widget.Button btnSubmit = dialog.findViewById(R.id.btnSubmitReview);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = etComment.getText().toString();

            com.example.healthbook.data.models.Review review = new com.example.healthbook.data.models.Review();
            review.setDoctor_id(appointment.getDoctor().getId());
            review.setAppointment_id(appointment.getId());
            review.setRating(rating);
            review.setComment(comment);

            com.example.healthbook.network.RetrofitClient.getInstance().getApiService().submitReview(review).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        android.widget.Toast.makeText(context, "Cảm ơn bạn đã đánh giá!", android.widget.Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        android.widget.Toast.makeText(context, "Lỗi gửi đánh giá", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    android.widget.Toast.makeText(context, "Lỗi mạng", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvMonth, tvDoctorName, tvSpecialty, tvTime, tvStatus, btnReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvApptDay);
            tvMonth = itemView.findViewById(R.id.tvApptMonth);
            tvDoctorName = itemView.findViewById(R.id.tvApptDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvApptSpecialty);
            tvTime = itemView.findViewById(R.id.tvApptTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}
