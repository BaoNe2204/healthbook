package com.example.healthbook.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;
import android.widget.TextView;
import android.widget.Toast;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        view.findViewById(R.id.btnPersonalInfo).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_personalInfo));
            
        view.findViewById(R.id.btnVipMember).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_vipMember));
            
        view.findViewById(R.id.btnRelatives).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_relatives));
            
        view.findViewById(R.id.btnAddress).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_address));
            
        view.findViewById(R.id.btnPaymentMethods).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_paymentMethods));
            
        view.findViewById(R.id.btnHealthInsurance).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_healthInsurance));
            
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_settings));
            
        view.findViewById(R.id.btnSupportCenter).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_supportCenter));
            
        view.findViewById(R.id.btnAppReview).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_account_to_appReview));
            
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvUserName.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty() ? user.getDisplayName() : "Người dùng HealthBook");
            tvUserEmail.setText(user.getEmail());
        }

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.loginFragment);
        });

        return view;
    }
}
