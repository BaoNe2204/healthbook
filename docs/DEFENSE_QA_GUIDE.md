# TÀI LIỆU HƯỚNG DẪN TRẢ LỜI VẤN ĐÁP / BẢO VỆ ĐỒ ÁN
## CHỦ ĐỀ: KẾT NỐI DATABASE & FIREBASE TRONG HEALTHBOOK

Tài liệu này tổng hợp câu trả lời chuẩn mực, ngắn gọn và giàu tính kỹ thuật để bạn trả lời hội đồng/giáo viên khi bị hỏi: **"Dự án này kết nối Database/Firebase như thế nào?"**

---

## 🎯 CÂU TRẢ LỜI MẪU (BẢN TÓM TẮT DÀNH CHO BẢO VỆ 2 PHÚT)

> *"Thưa thầy/cô, hệ thống HealthBook của tụi em sử dụng **mô hình Client-Server kết hợp Firebase Cloud Services**. Việc kết nối dữ liệu được chia làm 3 kênh chính:*
> 
> *1. **Mobile App ➔ Firebase Auth**: App kết nối trực tiếp với Firebase Auth thông qua file cấu hình `google-services.json` để xác thực người dùng và nhận **ID Token (JWT)**.*
> *2. **Mobile App ➔ Node.js Backend**: App gửi yêu cầu HTTP REST API thông qua **Retrofit2**. Tụi em có viết một **OkHttp Interceptor** để tự động chèn Firebase ID Token vào Header `Authorization: Bearer <token>` của mọi Request.*
> *3. **Node.js Backend ➔ Cloud Firestore DB**: Server Node.js sử dụng **Firebase Admin SDK** kết hợp với file chìa khóa `serviceAccountKey.json` để xác minh Token và đọc/ghi dữ liệu trực tiếp trên Cloud Firestore.*
> 
> *Riêng tính năng **Chat Realtime**, Mobile App dùng trực tiếp Firebase Firestore SDK với hàm `addSnapshotListener` để nhận tin nhắn tức thì theo cơ chế Realtime mà không cần qua Server trung gian."*

---

## 🔍 CHI TIẾT KỸ THUẬT VÀ DẪN CHỨNG CODE (KHI GIÁO VIÊN HỎI SÂU)

### 1. Phía Android App kết nối với Backend & Firebase như thế nào?

* **Xác thực Firebase Auth**:
  * File cấu hình: `mobile-app/app/google-services.json`.
  * SDK gọi: `FirebaseAuth.getInstance().signInWithEmailAndPassword(...)`.
* **Cơ chế Token Interceptor (Điểm cộng kỹ thuật)**:
  * File: [RetrofitClient.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/network/RetrofitClient.java)
  * Code dẫn chứng:
    ```java
    // Tự động chặn mọi HTTP Request để chèn Token
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
        String token = Tasks.await(user.getIdToken(false)).getToken();
        requestBuilder.header("Authorization", "Bearer " + token);
    }
    ```
* **Địa chỉ IP kết nối**:
  * Chạy Máy ảo Emulator: `http://10.0.2.2:3000/api/` (10.0.2.2 là IP đặc biệt đại diện cho localhost máy tính trong máy ảo Android Studio).
  * Chạy Điện thoại thật: `http://<IP_Local_Wifi>:3000/api/`.

---

### 2. Phía Node.js Backend kết nối với Cloud Firestore DB như thế nào?

* **Khởi tạo Admin SDK**:
  * File: [firebase-config.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/firebase-config.js)
  * Chìa khóa xác thực: File `serviceAccountKey.json` (tải từ Firebase Console ➔ Project Settings ➔ Service accounts).
  * Code dẫn chứng:
    ```javascript
    const serviceAccount = require('./serviceAccountKey.json');
    initializeApp({ credential: cert(serviceAccount) });
    const db = getFirestore();
    ```
* **Middleware Giải mã Token**:
  * File: [auth.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/middleware/auth.js)
  * Server nhận Header `Authorization: Bearer <token>` ➔ Dùng `getAuth().verifyIdToken(token)` để giải mã ➔ Lấy ra `UID` chính xác của người dùng đang đăng nhập.
* **Đọc / Ghi dữ liệu NoSQL Firestore**:
  * File: [users.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/routes/users.js), [doctor.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/routes/doctor.js)...
  * Code dẫn chứng:
    ```javascript
    // Đọc thông tin user
    const doc = await db.collection('Users').doc(uid).get();
    // Cập nhật thông tin user
    await db.collection('Users').doc(uid).set(updateData, { merge: true });
    ```

---

### 3. Kênh Chat Realtime hoạt động kết nối như thế nào?

* **Không đi qua Node.js Server**: Chat cần tốc độ tức thì nên được tách riêng khỏi REST API.
* **Kênh kết nối Direct (Realtime Listener)**:
  * File: [ChatActivity.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/chat/ChatActivity.java)
  * Sử dụng Firestore SDK trực tiếp trên Android:
    ```java
    db.collection("Chats").document(chatId).collection("Messages")
      .orderBy("timestamp", Query.Direction.ASCENDING)
      .addSnapshotListener((value, error) -> {
          // Tự động lắng nghe dữ liệu thay đổi trên Cloud và cập nhật RecyclerView ngay lập tức
      });
    ```

---

## 📌 BẢNG TÓM TẮT TRA CỨU NHANH CHO VẤN ĐÁP

| Câu hỏi của Thầy / Cô | Trả lời nhanh | File Code chứng minh |
| :--- | :--- | :--- |
| **"Bảo mật API giữa App và Backend làm sao?"** | Dùng JWT Token do Firebase cấp. App dùng `OkHttp Interceptor` chèn `Bearer Token` vào Header, Server giải mã bằng `verifyIdToken`. | App: [RetrofitClient.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/network/RetrofitClient.java)<br>Server: [auth.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/middleware/auth.js) |
| **"Node.js kết nối Firestore bằng cái gì?"** | Dùng **Firebase Admin SDK** nạp chứng thư `serviceAccountKey.json`. | Server: [firebase-config.js](file:///c:/Users/thean/Desktop/healthbook/backend-api/firebase-config.js) |
| **"Database dùng loại gì?"** | Google Cloud Firestore - Cơ sở dữ liệu NoSQL dạng tài liệu (Document/Collection). | Cloud Firestore |
| **"Tại sao máy ảo Android gọi được Node.js ở localhost?"** | Vì Android Studio Emulator map địa chỉ IP `10.0.2.2` tới `127.0.0.1` (localhost) của máy tính host. | App: [RetrofitClient.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/network/RetrofitClient.java) |
| **"Chat Realtime có qua Server Node.js không?"** | Không. Chat kết nối thẳng từ Android App lên Firestore qua hàm `addSnapshotListener` để đạt tốc độ tức thì. | App: [ChatActivity.java](file:///c:/Users/thean/Desktop/healthbook/mobile-app/app/src/main/java/com/example/healthbook/ui/chat/ChatActivity.java) |
