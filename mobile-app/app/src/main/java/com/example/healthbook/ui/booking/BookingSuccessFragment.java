package com.example.healthbook.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

import androidx.navigation.Navigation;

public class BookingSuccessFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_success, container, false);

        View btnViewAppointments = view.findViewById(R.id.btnViewAppointments);
        btnViewAppointments.setOnClickListener(v -> {
            // Xóa toàn bộ lịch sử các bước đặt lịch (để tránh lưu trạng thái thừa)
            Navigation.findNavController(v).popBackStack(R.id.navigation_home, false);
            // Chuyển tab qua Lịch hẹn
            if (getActivity() != null) {
                com.google.android.material.bottomnavigation.BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                if (navView != null) {
                    navView.setSelectedItemId(R.id.navigation_appointments);
                }
            }
        });

        View btnBookOther = view.findViewById(R.id.btnBookOther);
        btnBookOther.setOnClickListener(v -> {
            // Quay thẳng về Trang chủ
            Navigation.findNavController(v).popBackStack(R.id.navigation_home, false);
            if (getActivity() != null) {
                com.google.android.material.bottomnavigation.BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                if (navView != null) {
                    navView.setSelectedItemId(R.id.navigation_home);
                }
            }
        });

        return view;
    }
}
