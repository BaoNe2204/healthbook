package com.example.healthbook.ui.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.healthbook.R;
import com.example.healthbook.data.models.Clinic;
import java.util.Arrays;
import java.util.List;

public class ClinicProfileFragment extends Fragment {

    private Clinic clinic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clinic_profile, container, false);

        if (getArguments() != null) {
            clinic = (Clinic) getArguments().getSerializable("clinic");
        }

        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        // Set basic info
        if (clinic != null) {
            TextView tvTopTitle = view.findViewById(R.id.tvTopTitle);
            TextView tvClinicName = view.findViewById(R.id.tvClinicName);
            TextView tvClinicAddress = view.findViewById(R.id.tvClinicAddress);
            
            tvTopTitle.setText(clinic.getName());
            tvClinicName.setText(clinic.getName());
            tvClinicAddress.setText(clinic.getAddress());
        }

        // Setup accordions
        setupAccordion(view.findViewById(R.id.headerIntro), view.findViewById(R.id.contentIntro), view.findViewById(R.id.arrowIntro));
        setupAccordion(view.findViewById(R.id.headerSpecialties), view.findViewById(R.id.contentSpecialties), view.findViewById(R.id.arrowSpecialties));
        setupAccordion(view.findViewById(R.id.headerServices), view.findViewById(R.id.contentServices), view.findViewById(R.id.arrowServices));

        // Setup dynamic mock data based on clinic ID
        List<String> specialties = Arrays.asList("Đa khoa", "Sản - Phụ khoa", "Nhi khoa", "Nội tổng hợp", "Tai - Mũi - Họng", "Chẩn đoán hình ảnh");
        List<String> services = Arrays.asList("Sản - Phụ khoa", "Nhi khoa", "Nội tổng quát - Bác sĩ gia đình", "Ngoại tổng quát", "Tai mũi họng", "Xét nghiệm", "Tiêm chủng");
        String intro = "Phòng Khám Đa Khoa SIM Medical Center\n\n• 09/2017: Thành lập dự án phòng khám.\n• 07/2019: Phòng khám nhận giấy phép hoạt động.\n• 08/2019: Khai trương Phòng khám SIM Medical Center.\n• Chuyên Khoa: Nội Khoa, Ngoại khoa, Sản - Phụ Khoa, Nhi Khoa, Bác sĩ gia đình, Tai mũi họng, Xét nghiệm, Chẩn đoán hình ảnh, Tiêm chủng.\n\nTầm nhìn và sứ mệnh:\n• Trở thành một trung tâm y tế - một điểm đến chăm sóc sức khỏe chuyên nghiệp,...";

        if (clinic != null) {
            switch (clinic.getId()) {
                case "2":
                    intro = "Phòng khám Đa khoa Vietlife\n\n• Hơn 10 năm kinh nghiệm trong lĩnh vực y tế tư nhân.\n• Cơ sở vật chất hiện đại, tiên tiến nhập khẩu.\n• Đội ngũ bác sĩ chuyên khoa sâu từ các bệnh viện Trung ương.\n\nSứ mệnh:\n• Mang đến sức khỏe và sự an tâm cho mọi gia đình bằng chất lượng dịch vụ y tế hàng đầu.";
                    specialties = Arrays.asList("Cơ xương khớp", "Thần kinh", "Tiêu hóa", "Tim mạch", "Chẩn đoán hình ảnh");
                    services = Arrays.asList("Khám cơ xương khớp", "Chụp cộng hưởng từ (MRI)", "Khám tiêu hóa", "Nội soi dạ dày", "Khám tim mạch");
                    break;
                case "3":
                    intro = "Phòng khám Da liễu Thẩm mỹ Mai Skin\n\n• Tiên phong trong điều trị các vấn đề về da liễu.\n• Đội ngũ bác sĩ da liễu uy tín, tay nghề cao.\n• Ứng dụng công nghệ laser và ánh sáng thế hệ mới nhất.\n\nCam kết:\n• Hiệu quả rõ rệt, an toàn tuyệt đối, chuẩn y khoa.";
                    specialties = Arrays.asList("Da liễu thẩm mỹ", "Điều trị mụn", "Điều trị sẹo", "Trẻ hóa da", "Laser thẩm mỹ");
                    services = Arrays.asList("Khám và tư vấn da", "Lấy nhân mụn chuẩn y khoa", "Trị sẹo rỗ công nghệ cao", "Laser trị nám, tàn nhang", "Peel da sinh học");
                    break;
            }
        }

        TextView tvIntroContent = view.findViewById(R.id.tvIntroContent);
        if (tvIntroContent != null) {
            tvIntroContent.setText(intro);
        }

        // Populate lists
        populateList(view.findViewById(R.id.listSpecialties), specialties);
        populateList(view.findViewById(R.id.listServices), services);

        // Back to top
        NestedScrollView scrollView = view.findViewById(R.id.scrollView);
        view.findViewById(R.id.btnBackToTop).setOnClickListener(v -> {
            scrollView.smoothScrollTo(0, 0);
        });

        // Book button
        view.findViewById(R.id.btnBook).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.bookingFlowFragment);
        });

        return view;
    }

    private void setupAccordion(View header, View content, ImageView arrow) {
        header.setOnClickListener(v -> {
            boolean isVisible = content.getVisibility() == View.VISIBLE;
            content.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            arrow.setRotation(isVisible ? 0 : 180);
        });
    }

    private void populateList(LinearLayout container, List<String> items) {
        for (int i = 0; i < items.size(); i++) {
            TextView tv = new TextView(getContext());
            tv.setText(items.get(i));
            tv.setTextColor(android.graphics.Color.parseColor("#424242"));
            tv.setTextSize(14f);
            tv.setPadding(48, 36, 48, 36); // Left, Top, Right, Bottom

            if (i % 2 == 0) {
                tv.setBackgroundColor(android.graphics.Color.WHITE);
            } else {
                tv.setBackgroundColor(android.graphics.Color.parseColor("#F9F9F9"));
            }
            container.addView(tv);
        }
    }
}
