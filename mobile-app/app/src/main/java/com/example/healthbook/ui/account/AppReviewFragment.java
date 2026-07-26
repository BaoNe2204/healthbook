package com.example.healthbook.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Review;
import com.example.healthbook.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppReviewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_review, container, false);
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        RatingBar rbAppRating = view.findViewById(R.id.rbAppRating);
        TextView tvRatingLabel = view.findViewById(R.id.tvRatingLabel);
        TextInputEditText etAppReviewComment = view.findViewById(R.id.etAppReviewComment);

        if (rbAppRating != null && tvRatingLabel != null) {
            rbAppRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                int r = Math.round(rating);
                switch (r) {
                    case 1: tvRatingLabel.setText("Chưa hài lòng (1/5)"); break;
                    case 2: tvRatingLabel.setText("Tạm ổn (2/5)"); break;
                    case 3: tvRatingLabel.setText("Khá tốt (3/5)"); break;
                    case 4: tvRatingLabel.setText("Hài lòng (4/5)"); break;
                    case 5: default: tvRatingLabel.setText("Rất hài lòng (5/5)"); break;
                }
            });
        }

        View btnSubmit = view.findViewById(R.id.btnSubmitAppReview);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                float rating = rbAppRating != null ? rbAppRating.getRating() : 5;
                String comment = etAppReviewComment != null && etAppReviewComment.getText() != null ? 
                        etAppReviewComment.getText().toString().trim() : "";

                Review review = new Review();
                review.setRating(rating);
                review.setComment(comment.isEmpty() ? "Đánh giá chất lượng ứng dụng HealthBook" : comment);

                RetrofitClient.getInstance().getApiService().submitReview(review).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Cảm ơn bạn đã gửi đánh giá cho HealthBook!", Toast.LENGTH_LONG).show();
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Đã ghi nhận đánh giá của bạn!", Toast.LENGTH_SHORT).show();
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                });
            });
        }

        return view;
    }
}
