# BẢNG PHÂN CHIA NHIỆM VỤ DỰ ÁN HEALTHBOOK (4 THÀNH VIÊN)

Dự án **HealthBook** được chia thành **4 phân hệ độc lập** tương ứng cho 4 thành viên. Mỗi thành viên chịu trách nhiệm xây dựng **cả Giao diện (Mobile App Android)** và **Backend API (Node.js & Firestore Database)** cho phân hệ được phân công.

---

## 📊 TỔNG QUAN PHÂN CHIA NHIỆM VỤ

| Thành viên | Phân hệ đảm nhận | Khối lượng Giao diện (Android App) | Khối lượng Backend (Node.js & Firestore) |
| :--- | :--- | :--- | :--- |
| **Thành viên 1** | **Xác thực, Tài khoản & Hồ sơ cá nhân** | Đăng ký, Đăng nhập, Hồ sơ cá nhân, BHYT, Địa chỉ, Người thân | API Auth Register, Profile CRUD, Avatar upload, Auth Middleware |
| **Thành viên 2** | **Tìm kiếm Bác sĩ & Quy trình Đặt lịch khám** | Trang chủ Bệnh nhân, Tìm kiếm Bác sĩ/Bệnh viện, Quy trình 5 bước Đặt khám | API Tìm kiếm Bác sĩ/Bệnh viện/Khoa, Lịch rảnh, Tạo Lịch khám |
| **Thành viên 3** | **Phân hệ Bác sĩ, Bệnh án & Chat Realtime** | Giao diện Bác sĩ, Ca khám, Lập bệnh án & kê đơn, Chat Realtime | API Bác sĩ duyệt lịch, Tạo bệnh án, Firestore Realtime Chat |
| **Thành viên 4** | **Tiêm chủng, Thông báo & Quản trị Admin** | Tiêm chủng vắc-xin, Quản lý lịch hẹn, Thông báo, Dashboard Admin | API Tiêm chủng, Thông báo, Admin Dashboard, Quản lý Users/Cơ sở y tế |

---

## 👤 THÀNH VIÊN 1: PHÂN HỆ XÁC THỰC, TÀI KHOẢN & HỒ SƠ CÁ NHÂN

### 1. Phạm vi Chức năng
* Đăng ký tài khoản mới, Đăng nhập hệ thống (Bệnh nhân / Bác sĩ / Admin), Đăng xuất.
* Quản lý Hồ sơ cá nhân: Cập nhật thông tin (Họ tên, SĐT, Ngày sinh, Giới tính, Chiều cao, Cân nặng).
* Quản lý Địa chỉ, Bảo hiểm y tế (BHYT) và Thông tin Người thân liên hệ khẩn cấp.
* Tải lên ảnh đại diện (Avatar).

### 2. Chi tiết Thư mục & Tệp tin phụ trách

#### 📱 Frontend (Mobile App - Java & Layout XML)
* **Java Source Files**:
  * [LoginFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/auth/LoginFragment.java) - Màn hình đăng nhập.
  * [RegisterFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/auth/RegisterFragment.java) - Màn hình đăng ký.
  * [AccountFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/AccountFragment.java) - Menu quản lý tài khoản chính.
  * [PersonalInfoFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/PersonalInfoFragment.java) - Chỉnh sửa thông tin cá nhân.
  * [AddressFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/AddressFragment.java) - Quản lý địa chỉ.
  * [HealthInsuranceFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/HealthInsuranceFragment.java) - Thông tin thẻ BHYT.
  * [RelativesFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/RelativesFragment.java) - Thông tin người thân.
  * [ProfileFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/profile/ProfileFragment.java) - Trang hồ sơ cá nhân tổng quan.

* **Layout XML Files**:
  * `fragment_login.xml`
  * `fragment_register.xml`
  * `fragment_account.xml`
  * `fragment_personal_info.xml`
  * `fragment_address.xml`
  * `fragment_health_insurance.xml`
  * `fragment_relatives.xml`
  * `fragment_profile.xml`

#### ⚙️ Backend API & Firestore Database
* **Node.js Routes & Middleware**:
  * [auth.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/middleware/auth.js) - Middleware giải mã JWT Token (`verifyToken`) & Kiểm tra role (`requireRole`).
  * [users.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/routes/users.js) - Các API endpoint:
    * `POST /api/users/register` - Đăng ký thông tin user vào DB.
    * `GET /api/users/profile` - Lấy thông tin hồ sơ.
    * `PUT /api/users/profile` - Cập nhật thông tin cá nhân/BHYT/Địa chỉ.
    * `POST /api/users/upload-avatar` - Tải lên ảnh đại diện dùng Multer.
* **Firestore Collection**: `Users`

---

## 👤 THÀNH VIÊN 2: PHÂN HỆ TÌM KIẾM BÁC SĨ & QUY TRÌNH ĐẶT LỊCH KHÁM

### 1. Phạm vi Chức năng
* Trang chủ Bệnh nhân (`HomeFragment`): Hiển thị Banner, Danh mục Chuyên khoa, Bác sĩ nổi bật, Bệnh viện/Phòng khám.
* Tìm kiếm & Lọc Bác sĩ theo Tên, Chuyên khoa, Bệnh viện, Giá khám.
* Xem chi tiết Chuyên khoa & Chi tiết Bác sĩ.
* Quy trình Đặt lịch khám 5 bước:
  1. Chọn Bác sĩ & Xem thông tin.
  2. Chọn Ngày khám & Khung giờ rảnh.
  3. Xác nhận Thông tin bệnh nhân.
  4. Thanh toán (Tiền mặt / Chuyển khoản).
  5. Đặt khám Thành công.

### 2. Chi tiết Thư mục & Tệp tin phụ trách

#### 📱 Frontend (Mobile App - Java & Layout XML)
* **Java Source Files**:
  * [HomeFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/home/HomeFragment.java) - Trang chủ bệnh nhân.
  * [DoctorSearchFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/booking/DoctorSearchFragment.java) - Tìm kiếm & lọc bác sĩ.
  * [BookingFlowFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/booking/BookingFlowFragment.java) - Điều phối luồng đặt lịch.
  * [TimeSelectionFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/booking/TimeSelectionFragment.java) - Chọn ngày & khung giờ.
  * [AppointmentConfirmFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/booking/AppointmentConfirmFragment.java) - Xác nhận thông tin đặt khám.
  * [PaymentFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/booking/PaymentFragment.java) - Màn hình chọn phương thức thanh toán.
  * [BookingSuccessFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/booking/BookingSuccessFragment.java) - Màn hình thông báo đặt lịch thành công.
  * [OnlineConsultationFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/booking/OnlineConsultationFragment.java) - Màn hình đăng ký khám online.
  * [SpecialtyDetailFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/specialty/SpecialtyDetailFragment.java) - Màn hình chi tiết chuyên khoa.

* **Adapters & Models**:
  * [DoctorAdapter.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/adapters/DoctorAdapter.java)
  * [HospitalAdapter.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/adapters/HospitalAdapter.java)
  * `SpecialtyAdapter.java`, `ClinicAdapter.java`

* **Layout XML Files**:
  * `fragment_home.xml`, `fragment_doctor_search.xml`, `fragment_booking_flow.xml`, `fragment_time_selection.xml`, `fragment_appointment_confirm.xml`, `fragment_payment.xml`, `fragment_booking_success.xml`, `fragment_online_consultation.xml`, `fragment_specialty_detail.xml`, `item_doctor_search.xml`.

#### ⚙️ Backend API & Firestore Database
* **Node.js API (`server.js`)**:
  * `GET /api/doctors` - Lấy danh sách bác sĩ.
  * `GET /api/specialties` - Lấy danh sách chuyên khoa.
  * `GET /api/hospitals` - Lấy danh sách bệnh viện.
  * `GET /api/clinics` - Lấy danh sách phòng khám.
  * `GET /api/doctors/:id/schedule` - Truy vấn các khung giờ trống của bác sĩ theo ngày.
  * `POST /api/appointments` - Đặt lịch hẹn mới.
* **Firestore Collections**: `Doctors`, `Specialties`, `Hospitals`, `Clinics`, `Schedules`, `Appointments`.

---

## 👤 THÀNH VIÊN 3: PHÂN HỆ BÁC SĨ, BỆNH ÁN & CHAT REALTIME

### 1. Phạm vi Chức năng
* Giao diện Bác sĩ: Trang chủ bác sĩ (`DoctorHomeFragment`), Quản lý Lịch làm việc/Khung giờ rảnh (`DoctorScheduleFragment`).
* Quản lý Ca khám: Xem danh sách bệnh nhân đăng ký, Chấp nhận (Duyệt) hoặc Từ chối (Hủy) ca khám.
* Quản lý Bệnh nhân & Bệnh án: Lập Bệnh án sau khi khám (nhập Chẩn đoán, Kê đơn thuốc, Ghi chú), xem Lịch sử khám bệnh của từng bệnh nhân (`MedicalHistoryFragment`).
* Tư vấn & Chat Realtime: Trò chuyện trực tuyến giữa Bệnh nhân và Bác sĩ sử dụng Firebase Firestore Realtime.

### 2. Chi tiết Thư mục & Tệp tin phụ trách

#### 📱 Frontend (Mobile App - Java & Layout XML)
* **Java Source Files**:
  * [DoctorHomeFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/doctor/DoctorHomeFragment.java) - Bảng điều khiển bác sĩ.
  * `DoctorScheduleFragment.java` - Đăng ký khung giờ khám.
  * `DoctorAppointmentsFragment.java` - Quản lý danh sách ca khám.
  * [DoctorPatientsFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/doctor/DoctorPatientsFragment.java) - Danh sách bệnh nhân phụ trách.
  * [DoctorProfileFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/doctor/DoctorProfileFragment.java) - Hồ sơ bác sĩ & chỉnh sửa giá khám.
  * `ClinicProfileFragment.java` - Chi tiết phòng khám.
  * `MedicalHistoryFragment.java` - Lịch sử bệnh án.
  * [ChatActivity.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/chat/ChatActivity.java) - Khung chat trực tuyến.

* **Adapters & Models**:
  * [ChatAdapter.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/adapters/ChatAdapter.java)
  * [PatientAdapter.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/adapters/PatientAdapter.java)
  * `DoctorAppointmentAdapter.java`

* **Layout XML Files**:
  * `fragment_doctor_home.xml`, `fragment_doctor_schedule.xml`, `fragment_doctor_appointments.xml`, `fragment_doctor_patients.xml`, `fragment_doctor_profile.xml`, `fragment_medical_history.xml`, `activity_chat.xml`, `dialog_patient_history.xml`, `dialog_review.xml`, `item_patient.xml`.

#### ⚙️ Backend API & Firestore Database
* **Node.js Routes (`backend-api/routes/doctor.js`)**:
  * `GET /api/doctor/appointments` - Danh sách ca khám của bác sĩ.
  * `PUT /api/doctor/appointments/:id/status` - Cập nhật trạng thái ca khám (Đã duyệt / Đã hủy).
  * `POST /api/doctor/medical-records` - Tạo mới bệnh án & đơn thuốc.
  * `POST /api/doctor/schedule` - Cập nhật lịch rảnh.
  * `GET /api/doctor/my-patients` - Danh sách bệnh nhân đã từng khám.
  * `GET /api/doctor/reviews` - Lấy đánh giá của bệnh nhân.
* **Node.js API (`server.js`)**:
  * `GET /api/users/medical-records` - Bệnh nhân xem lịch sử bệnh án.
* **Firestore Collections**: `Appointments`, `MedicalRecords`, `Chats` (Sub-collection `Messages`), `Reviews`.

---

## 👤 THÀNH VIÊN 4: PHÂN HỆ TIÊM CHỦNG, THÔNG BÁO & QUẢN TRỊ ADMIN

### 1. Phạm vi Chức năng
* Tiêm chủng Vắc-xin: Đặt lịch tiêm chủng vắc-xin (`VaccinationFragment`), Quản lý Lịch sử tiêm chủng (`VaccinationHistoryFragment`).
* Quản lý Lịch hẹn bệnh nhân: Xem danh sách tất cả lịch hẹn đã đặt (`AppointmentsFragment`, `AppointmentDetailFragment`).
* Trung tâm Thông báo: Nhận các thông báo hệ thống về lịch hẹn, duyệt lịch, cập nhật hồ sơ (`NotificationsFragment`).
* Bảng điều khiển Quản trị viên (Admin Panel):
  * Thống kê tổng quan (`AdminDashboardFragment`).
  * Quản lý Người dùng: Xem danh sách, Khóa/Mở khóa tài khoản, Đổi quyền User/Doctor/Admin, Duyệt hồ sơ bác sĩ (`AdminUsersFragment`).
  * Quản lý Cơ sở y tế & Chuyên khoa: Thêm/Xóa Bệnh viện, Phòng khám, Chuyên khoa (`AdminHospitalsFragment`).

### 2. Chi tiết Thư mục & Tệp tin phụ trách

#### 📱 Frontend (Mobile App - Java & Layout XML)
* **Java Source Files**:
  * [VaccinationFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/vaccination/VaccinationFragment.java) - Danh mục & Đặt lịch vắc-xin.
  * [VaccinationHistoryFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/profile/VaccinationHistoryFragment.java) - Lịch sử tiêm chủng.
  * `AppointmentsFragment.java` - Danh sách lịch hẹn bệnh nhân.
  * `AppointmentDetailFragment.java` - Chi tiết lịch hẹn.
  * [NotificationsFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/notifications/NotificationsFragment.java) - Trung tâm thông báo.
  * `AdminDashboardFragment.java` - Dashboard thống kê Admin.
  * `AdminUsersFragment.java` - Quản lý tài khoản Admin.
  * `AdminHospitalsFragment.java` - Quản lý cơ sở y tế Admin.

* **Adapters & Models**:
  * [VaccineAdapter.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/adapters/VaccineAdapter.java)
  * [VaccineBookingAdapter.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/adapters/VaccineBookingAdapter.java)
  * [AdminCategoryAdapter.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/adapters/AdminCategoryAdapter.java)
  * `AppointmentAdapter.java`, `NotificationAdapter.java`, `AdminUserAdapter.java`

* **Layout XML Files**:
  * `fragment_vaccination.xml`, `fragment_vaccination_history.xml`, `fragment_appointments.xml`, `fragment_appointment_detail.xml`, `fragment_notifications.xml`, `fragment_admin_dashboard.xml`, `fragment_admin_users.xml`, `fragment_admin_hospitals.xml`, `dialog_book_vaccine.xml`, `item_vaccine.xml`, `item_vaccination_history.xml`, `item_admin_category.xml`.

#### ⚙️ Backend API & Firestore Database
* **Node.js Routes (`backend-api/routes/admin.js`)**:
  * `GET /api/admin/dashboard` - Thống kê tổng số User/Doctor/Patient/Appointment.
  * `GET /api/admin/users` - Lấy danh sách toàn bộ người dùng.
  * `PUT /api/admin/users/:uid/ban` - Khóa/Mở tài khoản.
  * `PUT /api/admin/users/:uid/role` - Đổi role người dùng.
  * `POST/DELETE /api/admin/hospitals` - Thêm/Xóa bệnh viện.
  * `POST/DELETE /api/admin/specialties` - Thêm/Xóa chuyên khoa.
* **Node.js API (`server.js`)**:
  * `GET /api/vaccines` - Lấy danh mục vắc-xin.
  * `POST /api/vaccine-bookings` & `GET /api/vaccine-bookings` - Đặt & xem lịch tiêm vắc-xin.
  * `GET /api/users/notifications` - Lấy thông báo của người dùng.
* **Firestore Collections**: `Vaccines`, `VaccineBookings`, `Notifications`, `Hospitals`, `Specialties`, `Users`.

---

## 🛠️ HƯỚNG DẪN PHỐI HỢP KHI LÀM VIỆC NHÓM

1. **Khởi tạo nhánh Git (Git Branching)**:
   * `feature/member1-auth-profile`
   * `feature/member2-doctor-booking`
   * `feature/member3-doctor-records-chat`
   * `feature/member4-vaccine-admin`
2. **Khai báo API chung trong Mobile App**:
   * Khi tạo API Backend mới, hãy cập nhật interface [ApiService.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/network/ApiService.java) để toàn đội cùng sử dụng chung Retrofit client.
3. **Khai báo Navigation chung**:
   * Kiểm tra và giữ đúng ID màn hình trong [nav_graph.xml](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/res/navigation/nav_graph.xml) để tránh xung đột khi merge code.
