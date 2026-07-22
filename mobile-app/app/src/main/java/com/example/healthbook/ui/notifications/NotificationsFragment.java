package com.example.healthbook.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;
import com.example.healthbook.data.models.NotificationItem;
import com.example.healthbook.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<NotificationItem> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new NotificationAdapter(list);
        rvNotifications.setAdapter(adapter);

        loadNotifications();
        
        return view;
    }

    private void loadNotifications() {
        RetrofitClient.getInstance().getApiService().getNotifications().enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(Call<List<NotificationItem>> call, Response<List<NotificationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Lỗi tải thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationItem>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        List<NotificationItem> items;

        NotificationAdapter(List<NotificationItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NotificationItem item = items.get(position);
            holder.tvTitle.setText(item.getTitle());
            holder.tvBody.setText(item.getBody());
            
            // Format time if needed, for now just show raw or a placeholder
            String timeStr = item.getCreated_at() != null ? item.getCreated_at().substring(0, 10) : "Vừa xong";
            holder.tvTime.setText(timeStr);
            
            int iconRes = android.R.drawable.ic_dialog_info;
            if ("REGISTER".equals(item.getType())) iconRes = android.R.drawable.ic_menu_myplaces;
            else if ("PROFILE_UPDATE".equals(item.getType())) iconRes = android.R.drawable.ic_menu_edit;
            else if ("APPOINTMENT_BOOKED".equals(item.getType())) iconRes = android.R.drawable.ic_menu_agenda;
            else if ("APPOINTMENT_APPROVED".equals(item.getType())) iconRes = android.R.drawable.ic_popup_reminder;
            
            holder.ivIcon.setImageResource(iconRes);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvTitle, tvBody, tvTime;

            ViewHolder(View v) {
                super(v);
                ivIcon = v.findViewById(R.id.ivNotifIcon);
                tvTitle = v.findViewById(R.id.tvNotifTitle);
                tvBody = v.findViewById(R.id.tvNotifBody);
                tvTime = v.findViewById(R.id.tvNotifTime);
            }
        }
    }
}
