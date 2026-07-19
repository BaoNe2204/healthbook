package com.example.healthbook.ui.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Doctor;

public class DoctorProfileFragment extends Fragment {

    private Doctor doctor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doctor = (Doctor) getArguments().getSerializable("doctor");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        if (doctor != null) {
            TextView tvDoctorName = view.findViewById(R.id.tvDoctorName);
            TextView tvExperience = view.findViewById(R.id.tvExperience);
            TextView tvHospital = view.findViewById(R.id.tvHospital);
            TextView tvSpecialty1 = view.findViewById(R.id.tvSpecialty1);
            TextView tvSpecialty2 = view.findViewById(R.id.tvSpecialty2);
            TextView tvDescription = view.findViewById(R.id.tvDescription);
            TextView tvPriceInfo = view.findViewById(R.id.tvPriceInfo);
            ImageView ivDoctorAvatar = view.findViewById(R.id.ivDoctorAvatar);

            tvDoctorName.setText(doctor.getName());
            
            int experience = doctor.getExperience() > 0 ? doctor.getExperience() : 5;
            tvExperience.setText(experience + " năm kinh nghiệm");
            
            tvHospital.setText("Bác sĩ " + doctor.getSpecialty() + " - " + doctor.getHospital());
            
            // Tách chuyên khoa (Giả sử chuyên khoa chứa dấu phẩy, nếu không thì hiện cùng 1 cái)
            String[] specs = doctor.getSpecialty().split("[,\\-]");
            tvSpecialty1.setText(specs[0].trim());
            if (specs.length > 1) {
                tvSpecialty2.setText(specs[1].trim());
                tvSpecialty2.setVisibility(View.VISIBLE);
            } else {
                tvSpecialty2.setVisibility(View.GONE);
            }

            if (doctor.getDescription() != null && !doctor.getDescription().isEmpty()) {
                tvDescription.setText(doctor.getDescription());
            } else {
                tvDescription.setText("Bác sĩ " + doctor.getName() + " là một chuyên gia tận tâm với nghề.");
            }
            
            int price = doctor.getPrice() > 0 ? doctor.getPrice() : 300000;
            tvPriceInfo.setText("Phí tư vấn " + String.format("%,d", price).replace(',', '.') + "đ/ lượt");
            
            if (doctor.getImageResId() != 0) {
                try {
                    ivDoctorAvatar.setImageResource(doctor.getImageResId());
                } catch (Exception e) {
                    ivDoctorAvatar.setImageResource(R.mipmap.ic_launcher_round);
                }
            }

            // Update Accordion Text if present
            TextView tvAccordionIntro = view.findViewById(R.id.tvAccordionIntro);
            if (tvAccordionIntro != null) {
                tvAccordionIntro.setText(doctor.getDescription() != null && !doctor.getDescription().isEmpty() ? doctor.getDescription() : "Thạc sĩ, Bác sĩ " + doctor.getName() + " tốt nghiệp y khoa hạng ưu. Bác sĩ tiếp tục tu nghiệp chuyên sâu ở nước ngoài.");
            }

            TextView tvAccordionHospital = view.findViewById(R.id.tvAccordionHospital);
            if (tvAccordionHospital != null) {
                tvAccordionHospital.setText("• " + doctor.getHospital());
            }

            // Fake data based on ID
            TextView tvAddress = view.findViewById(R.id.tvAddress);
            TextView tvDescriptionNotice = view.findViewById(R.id.tvDescription);
            TextView tvWorkingHours = view.findViewById(R.id.tvWorkingHours);
            TextView tvAccordionSpecialties = view.findViewById(R.id.tvAccordionSpecialties);
            TextView tvAccordionEducation = view.findViewById(R.id.tvAccordionEducation);
            TextView tvAccordionExperience = view.findViewById(R.id.tvAccordionExperience);
            
            if (doctor.getId().equals("1")) { // TS.BS Nguyễn Minh Đức - Tim mạch
                if (tvAddress != null) tvAddress.setText("78 Giải Phóng, Phương Đình, Đống Đa, Hà Nội");
                if (tvDescriptionNotice != null) tvDescriptionNotice.setText("Phòng khám giáo sư thay đổi lịch: Nghỉ khám chiều Thứ 6.");
                if (tvWorkingHours != null) tvWorkingHours.setText("Thứ 2 - Thứ 6: 08:00 - 16:30\nThứ 7: 08:00 - 11:30");
                if (tvAccordionSpecialties != null) tvAccordionSpecialties.setText("• Khám các bệnh lý tim mạch\n• Tăng huyết áp\n• Suy tim\n• Nhồi máu cơ tim");
                if (tvAccordionEducation != null) tvAccordionEducation.setText("• 1990 - 1996: ĐH Y Hà Nội\n• 2005: Tiến sĩ Y khoa, Cộng hòa Pháp");
                if (tvAccordionExperience != null) tvAccordionExperience.setText("• 1996 - nay: Viện Tim mạch Quốc gia\n• Giảng viên ĐH Y Hà Nội");
            } else if (doctor.getId().equals("2")) { // BS.CKII Trần Thị Hằng - Nhi khoa
                if (tvAddress != null) tvAddress.setText("18/879 La Thành, Láng Thượng, Đống Đa, Hà Nội");
                if (tvDescriptionNotice != null) tvDescriptionNotice.setText("Vui lòng đến đúng giờ đặt lịch. Cần mang theo sổ tiêm chủng của bé (nếu có).");
                if (tvWorkingHours != null) tvWorkingHours.setText("Thứ 2, 4, 6: 17:30 - 20:00\nChủ nhật: 09:00 - 17:00");
                if (tvAccordionSpecialties != null) tvAccordionSpecialties.setText("• Khám sức khỏe tổng quát trẻ em\n• Khám hô hấp, tiêu hóa\n• Tư vấn dinh dưỡng");
                if (tvAccordionEducation != null) tvAccordionEducation.setText("• 1995 - 2001: ĐH Y Hà Nội\n• 2010: Bác sĩ CKII Nhi khoa");
                if (tvAccordionExperience != null) tvAccordionExperience.setText("• 2001 - nay: Bệnh viện Nhi Trung Ương\n• Nguyên Trưởng khoa Khám bệnh");
            } else if (doctor.getId().equals("3")) { // BS.CKI Lê Văn Thành - Cơ xương khớp
                if (tvAddress != null) tvAddress.setText("40 Tràng Thi, Hàng Bông, Hoàn Kiếm, Hà Nội");
                if (tvDescriptionNotice != null) tvDescriptionNotice.setText("Bác sĩ chuyên khám chữa bệnh cơ xương khớp cho người lớn. Trẻ em dưới 15 tuổi vui lòng chọn bác sĩ Nhi.");
                if (tvWorkingHours != null) tvWorkingHours.setText("Thứ 3, 5, 7: 16:00 - 19:30");
                if (tvAccordionSpecialties != null) tvAccordionSpecialties.setText("• Viêm khớp dạng thấp\n• Thoái hóa khớp\n• Gout\n• Thoát vị đĩa đệm");
                if (tvAccordionEducation != null) tvAccordionEducation.setText("• 1998 - 2004: ĐH Y Hà Nội\n• 2012: Bác sĩ CKI Cơ xương khớp");
                if (tvAccordionExperience != null) tvAccordionExperience.setText("• 2004 - 2015: BV Bạch Mai\n• 2015 - nay: Bệnh viện Việt Đức");
            } else { // BS.CKI Phạm Thị Mai - Da liễu
                if (tvAddress != null) tvAddress.setText("123 Phố Huế, Hai Bà Trưng, Hà Nội");
                if (tvDescriptionNotice != null) tvDescriptionNotice.setText("Vui lòng không trang điểm và bôi các loại mỹ phẩm khi đến khám da liễu.");
                if (tvWorkingHours != null) tvWorkingHours.setText("Thứ 2 - Thứ 7: 09:00 - 19:00");
                if (tvAccordionSpecialties != null) tvAccordionSpecialties.setText("• Trị mụn trứng cá chuyên sâu\n• Nám, tàn nhang\n• Viêm da cơ địa\n• Khám các bệnh về tóc và móng");
                if (tvAccordionEducation != null) tvAccordionEducation.setText("• 2002 - 2008: ĐH Y Dược TP.HCM\n• 2015: Bác sĩ CKI Da Liễu");
                if (tvAccordionExperience != null) tvAccordionExperience.setText("• 2008 - 2018: Bệnh viện Da liễu TW\n• 2018 - nay: Phòng khám tư nhân Mai Skin");
            }
        }

        // Setup Accordion logic
        setupAccordion(view, R.id.headerPrice, R.id.contentPrice, R.id.arrowPrice);
        setupAccordion(view, R.id.headerHours, R.id.contentHours, R.id.arrowHours);
        setupAccordion(view, R.id.headerIntro, R.id.contentIntro, R.id.arrowIntro);
        setupAccordion(view, R.id.headerSpecialties, R.id.contentSpecialties, R.id.arrowSpecialties);
        setupAccordion(view, R.id.headerWorkplace, R.id.contentWorkplace, R.id.arrowWorkplace);
        setupAccordion(view, R.id.headerEducation, R.id.contentEducation, R.id.arrowEducation);
        setupAccordion(view, R.id.headerExperience, R.id.contentExperience, R.id.arrowExperience);

        Button btnBook = view.findViewById(R.id.btnBook);
        btnBook.setOnClickListener(v -> {
            if (doctor != null) {
                Bundle args = new Bundle();
                args.putSerializable("doctor", doctor);
                Navigation.findNavController(v).navigate(R.id.timeSelectionFragment, args);
            }
        });

        View btnChat = view.findViewById(R.id.btnChat);
        if (btnChat != null) {
            btnChat.setOnClickListener(v -> {
                if (doctor != null) {
                    Bundle args = new Bundle();
                    args.putString("doctorName", doctor.getName());
                    Navigation.findNavController(v).navigate(R.id.chatFragment, args);
                }
            });
        }

        return view;
    }

    private void setupAccordion(View view, int headerId, int contentId, int arrowId) {
        View header = view.findViewById(headerId);
        View content = view.findViewById(contentId);
        ImageView arrow = view.findViewById(arrowId);

        if (header != null && content != null && arrow != null) {
            header.setOnClickListener(v -> {
                if (content.getVisibility() == View.VISIBLE) {
                    content.setVisibility(View.GONE);
                    arrow.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    content.setVisibility(View.VISIBLE);
                    arrow.setImageResource(android.R.drawable.arrow_up_float);
                }
            });
        }
    }
}
