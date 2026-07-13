const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
require('dotenv').config();

let db = null;
try {
    const serviceAccount = require('./serviceAccountKey.json');
    initializeApp({
        credential: cert(serviceAccount)
    });
    db = getFirestore();
    console.log("✅ Firebase Admin initialized successfully.");
} catch (error) {
    console.error("❌ LỖI NGHIÊM TRỌNG: Không thể khởi tạo Firebase Admin!");
    console.error("Lý do:", error.message);
    console.error("👉 BẠN CHƯA CÓ FILE serviceAccountKey.json HOẶC FILE NÀY BỊ SAI.");
    console.error("👉 Vui lòng lên Firebase Console -> Project Settings -> Service Accounts -> Generate New Private Key.");
    console.error("👉 Tải file về, đổi tên thành 'serviceAccountKey.json' và đặt vào thư mục 'backend-api'.");
}

module.exports = { db };
