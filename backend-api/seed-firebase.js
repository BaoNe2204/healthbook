const { db } = require('./firebase-config');

const doctors = [
    { name: "TS.BS Nguyễn Minh Đức", specialty: "Tim mạch", hospital: "Bệnh viện Bạch Mai", rating: 4.9, reviewCount: 1000, imageResId: 0, experience: 15, price: 500000, description: "Bác sĩ có nhiều năm kinh nghiệm." },
    { name: "BS.CKII Trần Thị Hằng", specialty: "Nhi khoa", hospital: "Bệnh viện Nhi Trung Ương", rating: 4.8, reviewCount: 800, imageResId: 0, experience: 10, price: 300000, description: "Bác sĩ tận tâm với trẻ nhỏ." },
    { name: "BS.CKI Lê Văn Thành", specialty: "Cơ xương khớp", hospital: "Bệnh viện Việt Đức", rating: 4.9, reviewCount: 500, imageResId: 0, experience: 8, price: 250000, description: "Chuyên gia về các bệnh cơ xương khớp." },
    { name: "BS.CKI Phạm Thị Mai", specialty: "Da liễu", hospital: "Phòng khám tư", rating: 4.7, reviewCount: 300, imageResId: 0, experience: 5, price: 200000, description: "Điều trị dứt điểm các bệnh da liễu." }
];

const specialties = [
    { name: "Tim mạch", iconResId: 0 },
    { name: "Nhi khoa", iconResId: 0 },
    { name: "Da liễu", iconResId: 0 },
    { name: "Sản phụ khoa", iconResId: 0 },
    { name: "Tai mũi họng", iconResId: 0 },
    { name: "Răng hàm mặt", iconResId: 0 }
];

const hospitals = [
    { name: "Bệnh viện Bạch Mai", address: "78 Giải Phóng, Phương Đình, Đống Đa, Hà Nội", imageResId: 0 },
    { name: "Bệnh viện Việt Đức", address: "40 Tràng Thi, Hàng Bông, Hoàn Kiếm, Hà Nội", imageResId: 0 },
    { name: "Bệnh viện Nhi Trung Ương", address: "18/879 La Thành, Láng Thượng, Đống Đa, Hà Nội", imageResId: 0 }
];

async function seedData() {
    console.log("Đang đẩy dữ liệu lên Firebase...");
    try {
        for (const doc of doctors) {
            await db.collection('Doctors').add(doc);
        }
        console.log("✅ Đã thêm Doctors!");

        for (const spec of specialties) {
            await db.collection('Specialties').add(spec);
        }
        console.log("✅ Đã thêm Specialties!");

        for (const hosp of hospitals) {
            await db.collection('Hospitals').add(hosp);
        }
        console.log("✅ Đã thêm Hospitals!");

        console.log("🎉 HOÀN TẤT ĐỔ DỮ LIỆU!");
        process.exit(0);
    } catch (e) {
        console.error("Lỗi:", e);
        process.exit(1);
    }
}

seedData();
