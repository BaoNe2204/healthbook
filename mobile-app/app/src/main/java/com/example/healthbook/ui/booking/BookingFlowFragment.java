package com.example.healthbook.ui.booking;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.healthbook.R;

import java.util.Arrays;
import java.util.List;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import android.app.DatePickerDialog;

public class BookingFlowFragment extends Fragment {

    private int currentStep = 1;
    private final String[] stepTitles = {
            "Chọn dịch vụ",
            "Chọn Chuyên khoa",
            "Chọn Ngày Khám",
            "Chọn Giờ Khám",
            "Chọn Bệnh Nhân"
    };

    private TextView tvStepTitle;
    private ViewFlipper viewFlipper;
    private LinearLayout breadcrumbContainer;
    private HorizontalScrollView breadcrumbScroll;
    private View bottomBar;

    private String selectedDate = "";
    private String selectedTime = "";
    private String selectedPrice = "300.000đ"; // Default
    private java.util.Calendar currentCalendar = java.util.Calendar.getInstance();
    
    // Override profile details (for new patient)
    private String overrideName = null;
    private String overridePhone = null;
    private String overrideDob = null;
    private String overrideGender = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_flow, container, false);

        tvStepTitle = view.findViewById(R.id.tvStepTitle);
        viewFlipper = view.findViewById(R.id.viewFlipper);
        breadcrumbContainer = view.findViewById(R.id.breadcrumbContainer);
        breadcrumbScroll = view.findViewById(R.id.breadcrumbScroll);
        bottomBar = view.findViewById(R.id.bottomBar);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> {
            if (currentStep > 1) {
                prevStep();
            } else {
                Navigation.findNavController(v).popBackStack();
            }
        });

        // Step 1 interactions
        view.findViewById(R.id.btnOptSpecialty).setOnClickListener(v -> {
            populateSpecialties(view.findViewById(R.id.listSpecialtiesStep2), 1);
            nextStep();
        });
        
        View btnOptPackage = view.findViewById(R.id.btnOptPackage);
        if (btnOptPackage != null) {
            btnOptPackage.setOnClickListener(v -> {
                populateSpecialties(view.findViewById(R.id.listSpecialtiesStep2), 2);
                nextStep();
            });
        }

        // Default to specialties initially just in case
        populateSpecialties(view.findViewById(R.id.listSpecialtiesStep2), 1);

        // Step 3 interactions
        updateCalendarView(view);
        
        View btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        if (btnPrevMonth != null) {
            btnPrevMonth.setOnClickListener(v -> {
                currentCalendar.add(java.util.Calendar.MONTH, -1);
                updateCalendarView(view);
            });
        }
        View btnNextMonth = view.findViewById(R.id.btnNextMonth);
        if (btnNextMonth != null) {
            btnNextMonth.setOnClickListener(v -> {
                currentCalendar.add(java.util.Calendar.MONTH, 1);
                updateCalendarView(view);
            });
        }

        // Step 4 interactions
        populateTimes(view.findViewById(R.id.gridTimeMorning), Arrays.asList("07:30-08:00", "08:00-08:30", "08:30-09:00", "09:00-09:30", "09:30-10:00", "10:00-10:30", "10:30-11:00", "11:00-11:30", "11:30-12:00"));
        populateTimes(view.findViewById(R.id.gridTimeAfternoon), Arrays.asList("13:00-13:30", "13:30-14:00", "14:00-14:30", "14:30-15:00", "15:00-15:30", "15:30-16:00", "16:00-16:30", "16:30-17:00"));

        // Load Patient info from Firebase & SharedPreferences
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_profile", android.content.Context.MODE_PRIVATE);
        String name = prefs.getString("fullName", "");
        String phone = prefs.getString("phone", "");
        String dob = prefs.getString("dob", "");
        String gender = prefs.getString("gender", "");

        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        
        TextView tvPatientName = view.findViewById(R.id.tvPatientName);
        if (tvPatientName != null) {
            if (!name.isEmpty()) tvPatientName.setText(name);
            else if (user != null && user.getDisplayName() != null) tvPatientName.setText(user.getDisplayName());
        }

        TextView tvPatientPhone = view.findViewById(R.id.tvPatientPhone);
        if (tvPatientPhone != null) {
            if (!phone.isEmpty()) tvPatientPhone.setText("📞 " + phone);
            else if (user != null && user.getPhoneNumber() != null) tvPatientPhone.setText("📞 " + user.getPhoneNumber());
        }

        TextView tvPatientDobGender = view.findViewById(R.id.tvPatientDobGender);
        if (tvPatientDobGender != null && !dob.isEmpty() && !gender.isEmpty()) {
            tvPatientDobGender.setText("🎂 " + dob + " - " + gender);
        }

        // Step 5 interactions
        view.findViewById(R.id.btnCreateNewProfile).setOnClickListener(v -> showCreateProfileDialog(view));

        View.OnClickListener proceedAction = v -> {
            Bundle args = new Bundle();
            args.putString("bookingDate", selectedDate);
            args.putString("bookingTime", selectedTime);
            args.putString("bookingPrice", selectedPrice);
            if (overrideName != null) {
                args.putString("patientName", overrideName);
                args.putString("patientPhone", overridePhone);
                args.putString("patientDob", overrideDob);
                args.putString("patientGender", overrideGender);
            }
            Navigation.findNavController(v).navigate(R.id.appointmentConfirmFragment, args);
        };

        view.findViewById(R.id.btnContinue).setOnClickListener(proceedAction);
        
        // Make the "Chọn" button on the patient card also work for better UX
        View btnSelectPatient = view.findViewById(R.id.btnSelectPatient);
        if (btnSelectPatient != null) {
            btnSelectPatient.setOnClickListener(proceedAction);
        }
        
        View btnViewPatientDetails = view.findViewById(R.id.btnViewPatientDetails);
        if (btnViewPatientDetails != null) {
            btnViewPatientDetails.setOnClickListener(v -> {
                String dName = overrideName != null ? overrideName : name;
                String dPhone = overridePhone != null ? overridePhone : phone;
                String dDob = overrideDob != null ? overrideDob : dob;
                String dGender = overrideGender != null ? overrideGender : gender;
                if (dName.isEmpty() && user != null && user.getDisplayName() != null) dName = user.getDisplayName();
                if (dPhone.isEmpty() && user != null && user.getPhoneNumber() != null) dPhone = user.getPhoneNumber();
                
                new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Chi tiết hồ sơ")
                    .setMessage("Họ và tên: " + dName + "\n\n" +
                                "Số điện thoại: " + dPhone + "\n\n" +
                                "Ngày sinh: " + dDob + "\n\n" +
                                "Giới tính: " + dGender)
                    .setPositiveButton("Đóng", null)
                    .show();
            });
        }

        updateUI();

        return view;
    }

    private void nextStep() {
        if (currentStep < 5) {
            currentStep++;
            viewFlipper.showNext();
            updateUI();
        }
    }

    private void prevStep() {
        if (currentStep > 1) {
            currentStep--;
            viewFlipper.showPrevious();
            updateUI();
        }
    }

    private void updateUI() {
        tvStepTitle.setText("Chọn " + stepTitles[currentStep - 1]);
        bottomBar.setVisibility(currentStep == 5 ? View.VISIBLE : View.GONE);
        updateBreadcrumb();
    }

    private void updateBreadcrumb() {
        breadcrumbContainer.removeAllViews();
        for (int i = 1; i <= 5; i++) {
            // Number Circle
            TextView tvNum = new TextView(getContext());
            tvNum.setText(String.valueOf(i));
            tvNum.setGravity(Gravity.CENTER);
            tvNum.setTextSize(12f);
            tvNum.setTypeface(null, Typeface.BOLD);
            
            LinearLayout.LayoutParams numParams = new LinearLayout.LayoutParams(60, 60);
            tvNum.setLayoutParams(numParams);

            if (i == currentStep) {
                tvNum.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
                tvNum.setTextColor(Color.WHITE);
            } else if (i < currentStep) {
                tvNum.setBackgroundColor(Color.parseColor("#1976D2")); // Blue
                tvNum.setTextColor(Color.WHITE);
            } else {
                tvNum.setBackgroundColor(Color.parseColor("#E0E0E0")); // Grey
                tvNum.setTextColor(Color.parseColor("#757575"));
            }

            // Title
            TextView tvTitle = new TextView(getContext());
            tvTitle.setText(stepTitles[i - 1]);
            tvTitle.setTextSize(13f);
            tvTitle.setPadding(16, 0, 16, 0);
            
            if (i == currentStep) {
                tvTitle.setTextColor(Color.parseColor("#212121"));
                tvTitle.setTypeface(null, Typeface.BOLD);
            } else {
                tvTitle.setTextColor(Color.parseColor("#757575"));
            }
            
            // Container for Number and Title to make them clickable together
            LinearLayout stepItem = new LinearLayout(getContext());
            stepItem.setOrientation(LinearLayout.HORIZONTAL);
            stepItem.setGravity(Gravity.CENTER_VERTICAL);
            stepItem.addView(tvNum);
            stepItem.addView(tvTitle);
            
            // Allow clicking to go back to previous steps
            if (i < currentStep) {
                final int targetStep = i;
                stepItem.setOnClickListener(v -> jumpToStep(targetStep));
                // Add a small visual cue that it's clickable (ripple effect would be nice, but alpha is simpler)
                stepItem.setAlpha(0.8f);
            }

            breadcrumbContainer.addView(stepItem);

            // Arrow separator if not last
            if (i < 5) {
                TextView tvArrow = new TextView(getContext());
                tvArrow.setText(">");
                tvArrow.setTextColor(Color.parseColor("#BDBDBD"));
                tvArrow.setPadding(8, 0, 16, 0);
                breadcrumbContainer.addView(tvArrow);
            }
        }
    }
    
    private void jumpToStep(int targetStep) {
        if (targetStep > 0 && targetStep <= 5 && targetStep != currentStep) {
            currentStep = targetStep;
            viewFlipper.setDisplayedChild(currentStep - 1);
            updateUI();
        }
    }

    private void populateSpecialties(LinearLayout container, int type) {
        container.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        
        if (type == 1) { // Specialties
            List<String> items = Arrays.asList("Sản - Phụ khoa", "Nhi khoa", "Nội tổng quát - Bác sĩ gia đình", "Ngoại tổng quát", "Tai mũi họng", "Chuẩn đoán hình ảnh");
            for (String item : items) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding((int)(16 * density), (int)(16 * density), (int)(16 * density), (int)(16 * density));
                row.setBackgroundColor(Color.WHITE);
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, (int)(8 * density));
                row.setLayoutParams(params);

                TextView tv = new TextView(getContext());
                tv.setText(item);
                tv.setTextColor(Color.parseColor("#212121"));
                tv.setTextSize(16f);
                tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                row.addView(tv);

                TextView btn = new TextView(getContext());
                btn.setText("Chọn");
                btn.setTextColor(Color.parseColor("#1976D2")); // Blue
                btn.setTextSize(14f);
                btn.setTypeface(null, Typeface.BOLD);
                row.addView(btn);

                row.setOnClickListener(v -> {
                    selectedPrice = "300.000đ"; // Default for specialty
                    nextStep();
                });
                container.addView(row);
            }
        } else { // Packages
            List<String> items = Arrays.asList("Gói khám tổng quát cho nam (Gói tiêu chuẩn)", "Gói khám tổng quát cho nam (Gói nâng cao)", "Gói khám tổng quát cho nam (Gói chuyên sâu)", "Gói khám tổng quát cho nữ (Gói tiêu chuẩn)", "Gói khám tổng quát cho nữ (Gói nâng cao)");
            List<String> prices = Arrays.asList("1.651.000đ", "3.289.000đ", "5.184.000đ", "1.651.000đ", "3.295.000đ");
            
            for (int i = 0; i < items.size(); i++) {
                final String pkgPrice = prices.get(i);
                android.widget.RelativeLayout row = new android.widget.RelativeLayout(getContext());
                row.setBackgroundResource(R.drawable.bg_border_rounded_white_elevation);
                row.setPadding((int)(16 * density), (int)(16 * density), (int)(16 * density), (int)(16 * density));
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 0, 0, (int)(12 * density));
                row.setLayoutParams(rowParams);

                TextView tvPrice = new TextView(getContext());
                tvPrice.setId(View.generateViewId());
                tvPrice.setText(pkgPrice);
                tvPrice.setTextColor(Color.parseColor("#4CAF50")); // Green
                tvPrice.setTextSize(15f);
                tvPrice.setTypeface(null, Typeface.BOLD);
                android.widget.RelativeLayout.LayoutParams priceParams = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                priceParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
                priceParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);
                tvPrice.setLayoutParams(priceParams);
                row.addView(tvPrice);

                TextView tvTitle = new TextView(getContext());
                tvTitle.setText(items.get(i));
                tvTitle.setTextColor(Color.parseColor("#212121"));
                tvTitle.setTextSize(15f);
                android.widget.RelativeLayout.LayoutParams titleParams = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                titleParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_START);
                titleParams.addRule(android.widget.RelativeLayout.START_OF, tvPrice.getId());
                titleParams.setMarginEnd((int)(16 * density));
                tvTitle.setLayoutParams(titleParams);
                row.addView(tvTitle);

                TextView btn = new TextView(getContext());
                btn.setText("Chọn");
                btn.setTextColor(Color.parseColor("#1976D2"));
                btn.setTextSize(14f);
                btn.setTypeface(null, Typeface.BOLD);
                android.widget.RelativeLayout.LayoutParams btnParams = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btnParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
                btnParams.addRule(android.widget.RelativeLayout.BELOW, tvPrice.getId());
                btnParams.topMargin = (int)(24 * density);
                btn.setLayoutParams(btnParams);
                row.addView(btn);

                row.setOnClickListener(v -> {
                    selectedPrice = pkgPrice;
                    nextStep();
                });
                container.addView(row);
            }
        }
    }

    private void updateCalendarView(View view) {
        TextView tvMonthYear = view.findViewById(R.id.tvMonthYear);
        if (tvMonthYear != null) {
            String monthYear = String.format("Tháng %02d / %d", currentCalendar.get(java.util.Calendar.MONTH) + 1, currentCalendar.get(java.util.Calendar.YEAR));
            tvMonthYear.setText(monthYear);
        }
        android.widget.GridLayout gridCalendar = view.findViewById(R.id.gridCalendar);
        if (gridCalendar != null) {
            populateCalendar(gridCalendar);
        }
    }

    private void populateCalendar(android.widget.GridLayout grid) {
        grid.removeAllViews();
        
        java.util.Calendar cal = (java.util.Calendar) currentCalendar.clone();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int emptyCells = (dayOfWeek == java.util.Calendar.SUNDAY) ? 6 : dayOfWeek - 2;
        
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        float density = getResources().getDisplayMetrics().density;
        
        // Add empty cells
        for (int i = 0; i < emptyCells; i++) {
            TextView tv = new TextView(getContext());
            android.widget.GridLayout.LayoutParams params = new android.widget.GridLayout.LayoutParams();
            params.width = 0;
            params.height = (int) (48 * density);
            params.columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f);
            params.setMargins((int)(4 * density), (int)(4 * density), (int)(4 * density), (int)(4 * density));
            tv.setLayoutParams(params);
            grid.addView(tv);
        }
        
        java.util.Calendar today = java.util.Calendar.getInstance();
        
        for (int i = 1; i <= daysInMonth; i++) {
            TextView tv = new TextView(getContext());
            tv.setText(String.valueOf(i));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(14f);
            
            android.widget.GridLayout.LayoutParams params = new android.widget.GridLayout.LayoutParams();
            params.width = 0;
            params.height = (int) (48 * density);
            params.columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f);
            params.setMargins((int)(4 * density), (int)(4 * density), (int)(4 * density), (int)(4 * density));
            tv.setLayoutParams(params);

            boolean isPast = currentCalendar.get(java.util.Calendar.YEAR) < today.get(java.util.Calendar.YEAR) ||
                (currentCalendar.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) && currentCalendar.get(java.util.Calendar.MONTH) < today.get(java.util.Calendar.MONTH)) ||
                (currentCalendar.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) && currentCalendar.get(java.util.Calendar.MONTH) == today.get(java.util.Calendar.MONTH) && i < today.get(java.util.Calendar.DAY_OF_MONTH));

            if (isPast) {
                tv.setTextColor(Color.parseColor("#BDBDBD"));
                tv.setBackgroundColor(Color.parseColor("#F5F5F5"));
            } else {
                tv.setTextColor(Color.parseColor("#212121"));
                tv.setBackgroundColor(Color.parseColor("#F5F5F5"));
                tv.setOnClickListener(v -> {
                    selectedDate = String.format("%02d/%02d/%d", Integer.parseInt(tv.getText().toString()), currentCalendar.get(java.util.Calendar.MONTH) + 1, currentCalendar.get(java.util.Calendar.YEAR));
                    
                    for (int j = 0; j < grid.getChildCount(); j++) {
                        View child = grid.getChildAt(j);
                        if (child instanceof TextView && !((TextView) child).getText().toString().isEmpty()) {
                            TextView childTv = (TextView) child;
                            int day = Integer.parseInt(childTv.getText().toString());
                            boolean past = currentCalendar.get(java.util.Calendar.YEAR) < today.get(java.util.Calendar.YEAR) ||
                                (currentCalendar.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) && currentCalendar.get(java.util.Calendar.MONTH) < today.get(java.util.Calendar.MONTH)) ||
                                (currentCalendar.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) && currentCalendar.get(java.util.Calendar.MONTH) == today.get(java.util.Calendar.MONTH) && day < today.get(java.util.Calendar.DAY_OF_MONTH));
                            
                            childTv.setBackgroundColor(Color.parseColor("#F5F5F5"));
                            childTv.setTextColor(past ? Color.parseColor("#BDBDBD") : Color.parseColor("#212121"));
                        }
                    }
                    tv.setBackgroundColor(Color.parseColor("#212121"));
                    tv.setTextColor(Color.WHITE);
                    nextStep();
                });
            }

            grid.addView(tv);
        }
    }

    private void populateTimes(GridLayout grid, List<String> times) {
        for (String time : times) {
            TextView tv = new TextView(getContext());
            tv.setText(time);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(13f);
            tv.setTextColor(Color.parseColor("#424242"));
            tv.setBackgroundResource(R.drawable.bg_border_rounded_grey);
            
            float density = getResources().getDisplayMetrics().density;
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = (int) (40 * density); // 40dp height
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins((int)(4 * density), (int)(4 * density), (int)(4 * density), (int)(4 * density));
            tv.setLayoutParams(params);

            tv.setOnClickListener(v -> {
                selectedTime = time.split("-")[0]; // Just take start time
                nextStep();
            });
            grid.addView(tv);
        }
    }

    private void showCreateProfileDialog(View rootView) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_profile);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        TextInputEditText etName = dialog.findViewById(R.id.etProfileName);
        TextInputEditText etPhone = dialog.findViewById(R.id.etProfilePhone);
        TextInputEditText etDob = dialog.findViewById(R.id.etProfileDob);
        AutoCompleteTextView etGender = dialog.findViewById(R.id.etProfileGender);
        
        String[] genders = new String[]{"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, genders);
        etGender.setAdapter(adapter);
        
        etDob.setOnClickListener(v -> {
            java.util.Calendar c = java.util.Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                etDob.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }, c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH), c.get(java.util.Calendar.DAY_OF_MONTH)).show();
        });
        
        dialog.findViewById(R.id.btnCancelProfile).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            overrideName = etName.getText().toString();
            overridePhone = etPhone.getText().toString();
            overrideDob = etDob.getText().toString();
            overrideGender = etGender.getText().toString();
            
            // Render it in Step 5 UI dynamically
            LinearLayout container = rootView.findViewById(R.id.patientProfilesContainer);
            View newProfileCard = LayoutInflater.from(getContext()).inflate(R.layout.fragment_booking_flow, null, false);
            // We just borrow the UI from the active state, wait actually let's just update the existing card to show this for now to keep it simple
            TextView tvPatientName = rootView.findViewById(R.id.tvPatientName);
            TextView tvPatientPhone = rootView.findViewById(R.id.tvPatientPhone);
            TextView tvPatientDobGender = rootView.findViewById(R.id.tvPatientDobGender);
            TextView patientTag = rootView.findViewById(R.id.patientTag);
            
            tvPatientName.setText(overrideName);
            tvPatientPhone.setText("📞 " + overridePhone);
            tvPatientDobGender.setText("🎂 " + overrideDob + " - " + overrideGender);
            patientTag.setText("Người thân");
            
            dialog.dismiss();
        });
        
        dialog.show();
    }
}
