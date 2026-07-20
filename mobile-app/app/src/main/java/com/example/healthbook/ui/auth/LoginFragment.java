package com.example.healthbook.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.healthbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    private android.app.ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        TextView tvRegister = view.findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);

            progressDialog = new android.app.ProgressDialog(getContext());
            progressDialog.setMessage("Đang đăng nhập...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            
                            // Tạm thời hardcode cho 2 tài khoản test để bạn xem giao diện (bỏ qua SQL lỗi)
                            if (email.equals("admin@healthbook.com")) {
                                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                                if (getActivity() instanceof com.example.healthbook.MainActivity) {
                                    ((com.example.healthbook.MainActivity) getActivity()).setupNavigationForRole("admin");
                                }
                            } else if (email.equals("doctor@healthbook.com")) {
                                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                                if (getActivity() instanceof com.example.healthbook.MainActivity) {
                                    ((com.example.healthbook.MainActivity) getActivity()).setupNavigationForRole("doctor");
                                }
                            } else {
                                fetchRoleAndNavigate();
                            }
                        } else {
                            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                            btnLogin.setEnabled(true);
                            Toast.makeText(getContext(), "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvRegister.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.registerFragment);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void fetchRoleAndNavigate() {
        new com.example.healthbook.data.ApiRepository().getUserProfile(new com.example.healthbook.data.ApiRepository.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onSuccess(com.example.healthbook.data.models.UserProfile result) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                if (getActivity() instanceof com.example.healthbook.MainActivity) {
                    ((com.example.healthbook.MainActivity) getActivity()).setupNavigationForRole(result.getRole());
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                // Default to patient if error
                if (getActivity() instanceof com.example.healthbook.MainActivity) {
                    ((com.example.healthbook.MainActivity) getActivity()).setupNavigationForRole("patient");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // Already logged in, fetch role and go to correct home
            fetchRoleAndNavigate();
        }
    }
}
