package com.example.healthbook.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvUserName = view.findViewById(R.id.tvUserName);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            tvUserName.setText(user.getDisplayName());
        }

        View.OnClickListener goToHistory = v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.medicalHistoryFragment);
        };

        view.findViewById(R.id.btnMedicalHistory).setOnClickListener(goToHistory);
        view.findViewById(R.id.btnPrescriptions).setOnClickListener(goToHistory);

        return view;
    }
}
