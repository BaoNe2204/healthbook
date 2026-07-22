package com.example.healthbook.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.data.models.Vaccine;
import com.example.healthbook.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.VaccineViewHolder> {

    private List<Vaccine> vaccines;

    public VaccineAdapter(List<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    @NonNull
    @Override
    public VaccineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vaccine, parent, false);
        return new VaccineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineViewHolder holder, int position) {
        Vaccine vaccine = vaccines.get(position);
        holder.tvVaccineName.setText(vaccine.getName());
        holder.tvVaccineDisease.setText("Phòng bệnh: " + vaccine.getDisease());
        holder.tvVaccineAgeGroup.setText("Độ tuổi: " + vaccine.getAgeGroup());
        holder.tvVaccineDoses.setText("Số mũi: " + vaccine.getRequiredDoses());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvVaccinePrice.setText(format.format(vaccine.getPrice()));

        holder.btnBookVaccine.setOnClickListener(v -> {
            showBookingDialog(v.getContext(), vaccine);
        });
    }

    private void showBookingDialog(android.content.Context context, Vaccine vaccine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_book_vaccine, null);
        builder.setView(dialogView);

        TextView tvVaccineName = dialogView.findViewById(R.id.tvDialogVaccineName);
        tvVaccineName.setText(vaccine.getName());

        TextView tvSelectDate = dialogView.findViewById(R.id.tvSelectDate);
        TextView tvSelectTime = dialogView.findViewById(R.id.tvSelectTime);

        final String[] selectedDate = {""};
        final String[] selectedTime = {""};

        tvSelectDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                selectedDate[0] = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvSelectDate.setText(selectedDate[0]);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        tvSelectTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                selectedTime[0] = String.format("%02d:%02d", hourOfDay, minute);
                tvSelectTime.setText(selectedTime[0]);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnConfirmBooking).setOnClickListener(v -> {
            if (selectedDate[0].isEmpty() || selectedTime[0].isEmpty()) {
                Toast.makeText(context, "Vui lòng chọn ngày và giờ", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("vaccine_id", vaccine.getId());
            data.put("vaccine_name", vaccine.getName());
            data.put("price", vaccine.getPrice());
            data.put("appointment_date", selectedDate[0]);
            data.put("appointment_time", selectedTime[0]);

            RetrofitClient.getInstance().getApiService().createVaccineBooking(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Đặt lịch tiêm chủng thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Lỗi khi đặt lịch.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return vaccines.size();
    }

    public static class VaccineViewHolder extends RecyclerView.ViewHolder {
        TextView tvVaccineName, tvVaccineDisease, tvVaccineAgeGroup, tvVaccineDoses, tvVaccinePrice;
        View btnBookVaccine;

        public VaccineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVaccineName = itemView.findViewById(R.id.tvVaccineName);
            tvVaccineDisease = itemView.findViewById(R.id.tvVaccineDisease);
            tvVaccineAgeGroup = itemView.findViewById(R.id.tvVaccineAgeGroup);
            tvVaccineDoses = itemView.findViewById(R.id.tvVaccineDoses);
            tvVaccinePrice = itemView.findViewById(R.id.tvVaccinePrice);
            btnBookVaccine = itemView.findViewById(R.id.btnBookVaccine);
        }
    }
}
