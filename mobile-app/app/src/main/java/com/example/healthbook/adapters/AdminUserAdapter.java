package com.example.healthbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.data.models.UserProfile;
import com.example.healthbook.network.RetrofitClient;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private List<UserProfile> list;

    public AdminUserAdapter(List<UserProfile> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfile user = list.get(position);
        Context context = holder.itemView.getContext();

        holder.tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Ẩn danh");
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText(user.getRole() != null ? user.getRole().toUpperCase() : "PATIENT");

        // Status "banned"
        boolean isBanned = "banned".equalsIgnoreCase(user.getStatus());
        
        // Remove listener temporarily during status setting to avoid infinite loops
        holder.switchBan.setOnCheckedChangeListener(null);
        holder.switchBan.setChecked(isBanned);

        holder.switchBan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Map<String, Boolean> body = new HashMap<>();
            body.put("isBanned", isChecked);

            RetrofitClient.getInstance().getApiService().banUser(user.getUid(), body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        user.setStatus(isChecked ? "banned" : "active");
                        Toast.makeText(context, isChecked ? "Đã khóa tài khoản!" : "Đã mở khóa tài khoản!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Thao tác thất bại. Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                        // revert checked state
                        holder.switchBan.setOnCheckedChangeListener(null);
                        holder.switchBan.setChecked(!isChecked);
                        holder.switchBan.setOnCheckedChangeListener((button, checked) -> holder.switchBan.setChecked(checked));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    // revert checked state
                    holder.switchBan.setOnCheckedChangeListener(null);
                    holder.switchBan.setChecked(!isChecked);
                    holder.switchBan.setOnCheckedChangeListener((button, checked) -> holder.switchBan.setChecked(checked));
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;
        SwitchMaterial switchBan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            switchBan = itemView.findViewById(R.id.switchBan);
        }
    }
}
