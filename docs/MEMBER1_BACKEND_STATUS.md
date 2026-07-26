# BÁO CÁO CHI TIẾT TRẠNG THÁI BACKEND - THÀNH VIÊN 1

Tài liệu này tổng hợp chi tiết trạng thái kết nối Backend API & Firestore Database cho tất cả các chức năng thuộc phạm vi của **THÀNH VIÊN 1** (Phân hệ Xác thực, Tài khoản & Hồ sơ cá nhân).

---

## 📊 TỔNG QUAN TRẠNG THÁI

```
  [==================== 100% ĐÃ HOÀN THÀNH BACKEND & DB ====================]
  - Đăng ký / Đăng nhập
  - Hồ sơ cá nhân (Họ tên, SĐT, Ngày sinh...)
  - Tải lên ảnh đại diện (Avatar Multer)
  - Bảo hiểm Y tế (BHYT điện tử)
  - Địa chỉ cá nhân (Khám tại nhà)
  - Thông tin người thân liên hệ (Đặt khám hộ)
  - Phương thức thanh toán & Lịch sử giao dịch
  - Cài đặt ứng dụng (Thông báo, Đổi mật khẩu Firebase, Chế độ tối)
  - Trung tâm hỗ trợ 24/7 (Gọi Hotline & FAQ)
  - Đánh giá ứng dụng (Gửi review lên Firestore)
  - Tự động sinh thông báo khi update
```

---

## 🟢 DANH SÁCH CHỨC NĂNG VÀ BACKEND CONNECTIVITY (100% CONNECTED)

Tất cả các chức năng của Thành viên 1 đã được kết nối với Firebase Auth và Cloud Firestore Database qua Node.js Express REST API backend.

### 1.1. Đăng ký tài khoản (Register)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [RegisterFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/auth/RegisterFragment.java)
* **Backend Endpoint**: `POST /api/users/register` (File: [routes/users.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/routes/users.js))
* **Dữ liệu Firestore**: Tạo mới tài liệu trong Collection `Users` với Document ID = `UID` của Firebase Auth, lưu `displayName`, `email`, `role = "PATIENT"`, `status = "active"`.

### 1.2. Đăng nhập & Phân quyền (Login & Authorization)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [LoginFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/auth/LoginFragment.java) & `MainActivity.java`
* **Backend Endpoint**: `GET /api/users/profile` với Middleware `verifyToken`.
* **Dữ liệu Firestore**: Đọc thông tin vai trò (`role: PATIENT / DOCTOR / ADMIN`) từ Collection `Users/{UID}`.

### 1.3. Hồ sơ cá nhân (Personal Profile)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [PersonalInfoFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/PersonalInfoFragment.java)
* **Backend Endpoint**: `GET /api/users/profile` & `PUT /api/users/profile`
* **Dữ liệu Firestore**: Đọc/Ghi trực tiếp tài liệu `Users/{UID}` bằng cơ chế `{ merge: true }`.

### 1.4. Tải lên ảnh đại diện (Upload Avatar)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [PersonalInfoFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/PersonalInfoFragment.java)
* **Backend Endpoint**: `POST /api/users/upload-avatar`
* **Lưu trữ Server**: Sử dụng `Multer` lưu file ảnh vật lý vào `backend-api/public/uploads/avatar-xxx.jpg` và cập nhật URL trong Firestore & Firebase Auth.

### 1.5. Thẻ Bảo hiểm Y tế (Health Insurance)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [HealthInsuranceFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/HealthInsuranceFragment.java)
* **Backend Endpoint**: `GET /api/users/profile` & `PUT /api/users/profile`
* **Dữ liệu Firestore**: Đọc/Ghi `insuranceCode`, `hospitalRegister`, `insuranceExpiry` trong `Users/{UID}`.

### 1.6. Địa chỉ liên hệ (Address)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [AddressFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/AddressFragment.java)
* **Backend Endpoint**: `GET /api/users/profile` & `PUT /api/users/profile`
* **Dữ liệu Firestore**: Lưu trường `address` phục vụ cho dịch vụ *Khám tại nhà*.

### 1.7. Hồ sơ người thân (Relatives)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [RelativesFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/RelativesFragment.java)
* **Backend Endpoint**: `GET /api/users/profile` & `PUT /api/users/profile`
* **Dữ liệu Firestore**: Lưu `relativeName`, `relativeRelation`, `relativePhone` phục vụ cho tính năng *Đặt khám cho người thân*.

### 1.8. Phương thức thanh toán (Payment Methods)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [PaymentMethodsFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/PaymentMethodsFragment.java)
* **Backend Endpoint**: `GET /api/appointments`
* **Dữ liệu Firestore**: Đọc dữ liệu lịch sử thanh toán & ca khám đã hoàn thành từ Collection `Appointments`.

### 1.9. Cài đặt ứng dụng (Settings)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [SettingsFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/SettingsFragment.java)
* **Backend/Auth Integration**: Tích hợp đổi mật khẩu bảo mật qua Firebase Auth `sendPasswordResetEmail` và thiết lập thông báo ứng dụng.

### 1.10. Trung tâm hỗ trợ (Support Center)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [SupportCenterFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/SupportCenterFragment.java)
* **Tích hợp**: Kích hoạt tổng đài 24/7 Hotline qua Android Dialer Intent (`tel:19001234`) và danh mục FAQ hướng dẫn.

### 1.11. Đánh giá ứng dụng (App Review)
* **Trạng thái Backend**: 🟢 **100% CONNECTED**
* **Frontend Component**: [AppReviewFragment.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/account/AppReviewFragment.java)
* **Backend Endpoint**: `POST /api/users/reviews`
* **Dữ liệu Firestore**: Lưu thông tin số sao đánh giá và bình luận góp ý của người dùng vào Collection `Reviews`.

---

## 📌 BẢNG TÓM TẮT TRA CỨU NHANH

| Chức năng | Phía App Android (Frontend) | Phía Server Node.js (Backend) | Đã có Backend & DB chưa? |
| :--- | :--- | :--- | :---: |
| **Đăng ký** | `RegisterFragment.java` | `POST /api/users/register` | 🟢 **ĐÃ CÓ (100%)** |
| **Đăng nhập** | `LoginFragment.java` | `verifyToken` + `GET /api/users/profile` | 🟢 **ĐÃ CÓ (100%)** |
| **Hồ sơ cá nhân** | `PersonalInfoFragment.java` | `GET/PUT /api/users/profile` | 🟢 **ĐÃ CÓ (100%)** |
| **Upload Avatar** | `PersonalInfoFragment.java` | `POST /api/users/upload-avatar` | 🟢 **ĐÃ CÓ (100%)** |
| **Thẻ BHYT** | `HealthInsuranceFragment.java` | `GET/PUT /api/users/profile` | 🟢 **ĐÃ CÓ (100%)** |
| **Địa chỉ** | `AddressFragment.java` | `GET/PUT /api/users/profile` | 🟢 **ĐÃ CÓ (100%)** |
| **Người thân** | `RelativesFragment.java` | `GET/PUT /api/users/profile` | 🟢 **ĐÃ CÓ (100%)** |
| **Thanh toán** | `PaymentMethodsFragment.java` | `GET /api/appointments` | 🟢 **ĐÃ CÓ (100%)** |
| **Cài đặt** | `SettingsFragment.java` | Firebase Auth Password Reset | 🟢 **ĐÃ CÓ (100%)** |
| **Hỗ trợ & FAQ** | `SupportCenterFragment.java` | Hotline Intent & FAQ | 🟢 **ĐÃ CÓ (100%)** |
| **Đánh giá App** | `AppReviewFragment.java` | `POST /api/users/reviews` | 🟢 **ĐÃ CÓ (100%)** |
