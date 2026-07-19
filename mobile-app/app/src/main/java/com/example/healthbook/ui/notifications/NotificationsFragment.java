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

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<NotifItem> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        
        loadMockNotifications();
        
        adapter = new NotificationAdapter(list);
        rvNotifications.setAdapter(adapter);

        return view;
    }

    private void loadMockNotifications() {
        list.clear();
        list.add(new NotifItem("Đăng ký thành công", "Chào mừng bạn đến với HealthBook! Tài khoản của bạn đã được khởi tạo thành công.", "Vừa xong", android.R.drawable.ic_dialog_info));
        list.add(new NotifItem("Đặt lịch khám thành công", "Yêu cầu đặt lịch khám với TS.BS Nguyễn Văn A lúc 08:30 ngày 27/05/2026 đã được gửi. Đang chờ bác sĩ xác nhận.", "15 phút trước", android.R.drawable.ic_popup_reminder));
        list.add(new NotifItem("Cập nhật hồ sơ", "Thông tin hồ sơ cá nhân của bạn đã được cập nhật thành công.", "1 giờ trước", android.R.drawable.ic_menu_myplaces));
        list.add(new NotifItem("Lịch khám được duyệt", "Bác sĩ CKII Trần Thị Hằng đã xác nhận lịch khám cho bé vào lúc 14:00 ngày 28/05/2026.", " Hôm qua", android.R.drawable.ic_dialog_info));
    }

    private static class NotifItem {
        String title;
        String body;
        String time;
        int iconRes;

        NotifItem(String title, String body, String time, int iconRes) {
            this.title = title;
            this.body = body;
            this.time = time;
            this.iconRes = iconRes;
        }
    }

    private static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        List<NotifItem> items;

        NotificationAdapter(List<NotifItem> items) {
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
            NotifItem item = items.get(position);
            holder.tvTitle.setText(item.title);
            holder.tvBody.setText(item.body);
            holder.tvTime.setText(item.time);
            holder.ivIcon.setImageResource(item.iconRes);
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
