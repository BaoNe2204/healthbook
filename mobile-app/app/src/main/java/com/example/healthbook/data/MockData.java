package com.example.healthbook.data;

import com.example.healthbook.data.models.Appointment;
import com.example.healthbook.data.models.Doctor;
import com.example.healthbook.data.models.Specialty;
import com.example.healthbook.data.models.Hospital;
import java.util.ArrayList;
import java.util.List;

public class MockData {
    public static List<Doctor> getDoctors() {
        List<Doctor> list = new ArrayList<>();
        list.add(new Doctor("1", "TS.BS Nguyễn Minh Đức", "Tim mạch", "Bệnh viện Bạch Mai", 4.9, 1000, android.R.drawable.ic_menu_camera));
        list.add(new Doctor("2", "BS.CKII Trần Thị Hằng", "Nhi khoa", "Bệnh viện Nhi Trung Ương", 4.8, 800, android.R.drawable.ic_menu_camera));
        list.add(new Doctor("3", "BS.CKI Lê Văn Thành", "Cơ xương khớp", "Bệnh viện Việt Đức", 4.9, 500, android.R.drawable.ic_menu_camera));
        list.add(new Doctor("4", "BS.CKI Phạm Thị Mai", "Da liễu", "Phòng khám tư", 4.7, 300, android.R.drawable.ic_menu_camera));
        return list;
    }

    public static List<Appointment> getAppointments() {
        List<Appointment> list = new ArrayList<>();
        List<Doctor> doctors = getDoctors();
        list.add(new Appointment("A1", doctors.get(0), "15/05/2024", "08:30 - 09:00", "Sắp tới", "Khám tại bệnh viện"));
        list.add(new Appointment("A2", doctors.get(1), "05/05/2024", "14:00 - 14:30", "Đã qua", "Khám tại bệnh viện"));
        list.add(new Appointment("A3", doctors.get(2), "20/04/2024", "09:00 - 09:30", "Đã qua", "Khám online"));
        return list;
    }

    public static List<Specialty> getSpecialties() {
        List<Specialty> list = new ArrayList<>();
        list.add(new Specialty("1", "Tim mạch", android.R.drawable.ic_menu_view));
        list.add(new Specialty("2", "Nhi khoa", android.R.drawable.ic_menu_view));
        list.add(new Specialty("3", "Da liễu", android.R.drawable.ic_menu_view));
        list.add(new Specialty("4", "Sản phụ khoa", android.R.drawable.ic_menu_view));
        list.add(new Specialty("5", "Tai mũi họng", android.R.drawable.ic_menu_view));
        list.add(new Specialty("6", "Răng hàm mặt", android.R.drawable.ic_menu_view));
        list.add(new Specialty("7", "Thần kinh", android.R.drawable.ic_menu_view));
        list.add(new Specialty("8", "Mắt", android.R.drawable.ic_menu_view));
        return list;
    }

    public static List<Hospital> getHospitals() {
        List<Hospital> list = new ArrayList<>();
        list.add(new Hospital("1", "Bệnh viện Bạch Mai", "78 Giải Phóng, Phương Đình, Đống Đa, Hà Nội"));
        list.add(new Hospital("2", "Bệnh viện Việt Đức", "40 Tràng Thi, Hàng Bông, Hoàn Kiếm, Hà Nội"));
        list.add(new Hospital("3", "Bệnh viện Nhi Trung Ương", "18/879 La Thành, Láng Thượng, Đống Đa, Hà Nội"));
        return list;
    }
}
