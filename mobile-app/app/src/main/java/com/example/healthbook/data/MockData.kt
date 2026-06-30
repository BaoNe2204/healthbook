package com.example.healthbook.data

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewCount: Int,
    val experienceYears: Int,
    val hospitalName: String,
    val hospitalAddress: String,
    val about: String,
    val imageUrl: String = ""
)

data class Appointment(
    val id: String,
    val doctor: Doctor,
    val date: String,
    val time: String,
    val status: AppointmentStatus,
    val location: String = doctor.hospitalAddress,
    val type: String = "Khám tại bệnh viện",
    val price: Long = 300000,
    val code: String = "#LJ240520930"
)

enum class AppointmentStatus {
    UPCOMING, COMPLETED, CANCELED
}

data class Patient(
    val name: String,
    val phone: String,
    val dob: String
)

data class Notification(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: NotificationType
)

enum class NotificationType {
    APPOINTMENT, PROMO, SYSTEM
}

object MockData {
    val doctors = listOf(
        Doctor(
            id = "1",
            name = "BS. Trần Thị Hương",
            specialty = "Nội tổng quát",
            rating = 4.9,
            reviewCount = 330,
            experienceYears = 15,
            hospitalName = "Bệnh viện Đa khoa Tâm Anh",
            hospitalAddress = "108 Hoàng Như Tiếp, Long Biên, Hà Nội",
            about = "Bác sĩ chuyên khám và điều trị các bệnh lý nội tổng quát: viêm nhiễm, tiểu đường, huyết áp, rối loạn tiêu hóa..."
        ),
        Doctor(
            id = "2",
            name = "BS. Lê Minh Hiếu",
            specialty = "Tim mạch",
            rating = 4.8,
            reviewCount = 110,
            experienceYears = 10,
            hospitalName = "Bệnh viện Bạch Mai",
            hospitalAddress = "78 Giải Phóng, Đống Đa, Hà Nội",
            about = "Bác sĩ chuyên khoa tim mạch."
        ),
        Doctor(
            id = "3",
            name = "BS. Phạm Thu Thủy",
            specialty = "Nhi khoa",
            rating = 4.9,
            reviewCount = 76,
            experienceYears = 8,
            hospitalName = "Bệnh viện Nhi Trung ương",
            hospitalAddress = "18/879 La Thành, Đống Đa, Hà Nội",
            about = "Bác sĩ chuyên khoa nhi."
        ),
        Doctor(
            id = "4",
            name = "BS. Nguyễn Hoàng Nam",
            specialty = "Cơ xương khớp",
            rating = 4.7,
            reviewCount = 64,
            experienceYears = 12,
            hospitalName = "Bệnh viện Việt Đức",
            hospitalAddress = "40 Tràng Thi, Hoàn Kiếm, Hà Nội",
            about = "Bác sĩ chuyên khoa cơ xương khớp."
        ),
        Doctor(
            id = "5",
            name = "BS. Đỗ Tuấn Anh",
            specialty = "Hô hấp",
            rating = 4.8,
            reviewCount = 35,
            experienceYears = 9,
            hospitalName = "Bệnh viện Phổi Trung ương",
            hospitalAddress = "463 Hoàng Hoa Thám, Ba Đình, Hà Nội",
            about = "Bác sĩ chuyên khoa hô hấp."
        )
    )

    val currentPatient = Patient(
        name = "Nguyễn Minh Anh",
        phone = "0123456789",
        dob = "22/01/1995"
    )

    val upcomingAppointment = Appointment(
        id = "1",
        doctor = doctors[0],
        date = "Thứ Hai, 20/05/2024",
        time = "09:30",
        status = AppointmentStatus.UPCOMING
    )
}
