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
import android.widget.ImageView;

public class PaymentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        View btnPay = view.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.bookingSuccessFragment));

        if (getArguments() != null) {
            String date = getArguments().getString("bookingDate", "");
            String time = getArguments().getString("bookingTime", "");
            String price = getArguments().getString("bookingPrice", "300.000đ");
            
            if (!date.isEmpty() && !time.isEmpty()) {
                android.widget.TextView tvPaymentTime = view.findViewById(R.id.tvPaymentTime);
                if (tvPaymentTime != null) {
                    tvPaymentTime.setText(time + " - " + date);
                }
            }
            
            android.widget.TextView tvTotalPrice = view.findViewById(R.id.tvPaymentTotalPrice);
            if (tvTotalPrice != null) {
                tvTotalPrice.setText(price);
            }
            
            if (btnPay instanceof android.widget.Button) {
                ((android.widget.Button) btnPay).setText("Thanh toán " + price);
            }
        }

        return view;
    }
}
