package com.example.healthbook.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

public class PaymentMethodsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_methods, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        View btnMomo = view.findViewById(R.id.btnMethodMomo);
        if (btnMomo != null) {
            btnMomo.setOnClickListener(v -> 
                Toast.makeText(getContext(), "Ví MoMo hiện là phương thức mặc định", Toast.LENGTH_SHORT).show());
        }

        View btnZalopay = view.findViewById(R.id.btnMethodZalopay);
        if (btnZalopay != null) {
            btnZalopay.setOnClickListener(v -> 
                Toast.makeText(getContext(), "Đã chọn thanh toán ZaloPay khi đặt lịch", Toast.LENGTH_SHORT).show());
        }

        View btnBank = view.findViewById(R.id.btnMethodBank);
        if (btnBank != null) {
            btnBank.setOnClickListener(v -> 
                Toast.makeText(getContext(), "Đã chọn VietQR / Ngân hàng khi đặt lịch", Toast.LENGTH_SHORT).show());
        }

        return view;
    }
}
