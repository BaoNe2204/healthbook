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

        // Fetch the latest profile from API to ensure we have the updated display name
        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getUserProfile().enqueue(new retrofit2.Callback<com.example.healthbook.data.models.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, retrofit2.Response<com.example.healthbook.data.models.UserProfile> response) {
                if (getContext() != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getDisplayName() != null && !response.body().getDisplayName().isEmpty()) {
                        tvUserName.setText(response.body().getDisplayName());
                    }
                    if (response.body().getAvatarUrl() != null && !response.body().getAvatarUrl().isEmpty()) {
                        android.widget.ImageView ivAvatar = view.findViewById(R.id.ivAvatar);
                        if (ivAvatar != null) {
                            com.bumptech.glide.Glide.with(getContext()).load(response.body().getAvatarUrl()).into(ivAvatar);
                        }
                    }
                    
                    if ("DOCTOR".equalsIgnoreCase(response.body().getRole())) {
                        View btnFeeSetup = view.findViewById(R.id.btnFeeSetup);
                        View dividerFee = view.findViewById(R.id.dividerFee);
                        View btnMyReviews = view.findViewById(R.id.btnMyReviews);
                        View dividerReviews = view.findViewById(R.id.dividerReviews);
                        
                        if (btnFeeSetup != null) btnFeeSetup.setVisibility(View.VISIBLE);
                        if (dividerFee != null) dividerFee.setVisibility(View.VISIBLE);
                        if (btnMyReviews != null) btnMyReviews.setVisibility(View.VISIBLE);
                        if (dividerReviews != null) dividerReviews.setVisibility(View.VISIBLE);
                        
                        if (btnFeeSetup != null) {
                            btnFeeSetup.setOnClickListener(v -> showFeeSetupDialog());
                        }
                        if (btnMyReviews != null) {
                            btnMyReviews.setOnClickListener(v -> showMyReviewsDialog());
                        }
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.example.healthbook.data.models.UserProfile> call, Throwable t) {
                // Ignore failure, we already have the default Firebase auth info
            }
        });

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.loginFragment);
        });

        return view;
    }

    private void showFeeSetupDialog() {
        if (getContext() == null) return;
        android.app.Dialog dialog = new android.app.Dialog(getContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48);
        layout.setBackgroundColor(android.graphics.Color.WHITE);

        TextView title = new TextView(getContext());
        title.setText("Thiết lập giá khám");
        title.setTextSize(18f);
        title.setTextColor(android.graphics.Color.BLACK);
        title.setPadding(0, 0, 0, 24);
        layout.addView(title);

        android.widget.EditText etFee = new android.widget.EditText(getContext());
        etFee.setHint("Nhập giá khám (VD: 300000)");
        etFee.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etFee);

        android.widget.Button btnSave = new android.widget.Button(getContext());
        btnSave.setText("LƯU TRẠNG THÁI"); // or "LƯU"
        btnSave.setText("LƯU");
        btnSave.setOnClickListener(v -> {
            String feeStr = etFee.getText().toString();
            if (!feeStr.isEmpty()) {
                try {
                    int fee = Integer.parseInt(feeStr);
                    java.util.Map<String, Integer> body = new java.util.HashMap<>();
                    body.put("fee", fee);
                    com.example.healthbook.network.RetrofitClient.getInstance().getApiService().updateConsultationFee(body).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Cập nhật giá khám thành công!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Giá trị không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        layout.addView(btnSave);
        dialog.setContentView(layout);
        dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void showMyReviewsDialog() {
        if (getContext() == null) return;
        android.app.Dialog dialog = new android.app.Dialog(getContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_doctor_reviews);
        dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        androidx.recyclerview.widget.RecyclerView rvReviews = dialog.findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        
        android.widget.Button btnClose = dialog.findViewById(R.id.btnCloseReviews);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        com.example.healthbook.network.RetrofitClient.getInstance().getApiService().getDoctorReviews().enqueue(new retrofit2.Callback<java.util.List<com.example.healthbook.data.models.Review>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.example.healthbook.data.models.Review>> call, retrofit2.Response<java.util.List<com.example.healthbook.data.models.Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<com.example.healthbook.data.models.Review> reviews = response.body();
                    rvReviews.setAdapter(new androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
                        @NonNull
                        @Override
                        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
                            android.view.View view = android.view.LayoutInflater.from(getContext()).inflate(R.layout.item_doctor_review, parent, false);
                            return new androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {};
                        }
                        @Override
                        public void onBindViewHolder(@NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {
                            com.example.healthbook.data.models.Review review = reviews.get(position);
                            TextView tvDate = holder.itemView.findViewById(R.id.tvReviewDate);
                            android.widget.RatingBar rbRating = holder.itemView.findViewById(R.id.rbReviewRating);
                            TextView tvComment = holder.itemView.findViewById(R.id.tvReviewComment);
                            
                            // Try to format date
                            try {
                                java.text.SimpleDateFormat inFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                                java.text.SimpleDateFormat outFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                                java.util.Date d = inFormat.parse(review.getCreated_at());
                                tvDate.setText(outFormat.format(d));
                            } catch (Exception e) {
                                tvDate.setText(review.getCreated_at());
                            }
                            
                            rbRating.setRating(review.getRating());
                            tvComment.setText(review.getComment());
                        }
                        @Override
                        public int getItemCount() { return reviews.size(); }
                    });
                    
                    if (reviews.isEmpty()) {
                        Toast.makeText(getContext(), "Chưa có đánh giá nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.example.healthbook.data.models.Review>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
