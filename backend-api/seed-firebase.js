const { db } = require('./firebase-config');

const doctors = [
    { name: "TS.BS Nguyễn Minh Đức", specialty: "Tim mạch", hospital: "Bệnh viện Bạch Mai", rating: 4.9, reviewCount: 1000, imageUrl: "https://images.unsplash.com/photo-1622253692010-333f2da6031d?q=80&w=400&auto=format&fit=crop", experience: 15, price: 500000, description: "Bác sĩ có nhiều năm kinh nghiệm." },
    { name: "BS.CKII Trần Thị Hằng", specialty: "Nhi khoa", hospital: "Bệnh viện Nhi Trung Ương", rating: 4.8, reviewCount: 800, imageUrl: "https://images.unsplash.com/photo-1594824813566-88855ce78905?q=80&w=400&auto=format&fit=crop", experience: 10, price: 300000, description: "Bác sĩ tận tâm với trẻ nhỏ." },
    { name: "BS.CKI Lê Văn Thành", specialty: "Cơ xương khớp", hospital: "Bệnh viện Việt Đức", rating: 4.9, reviewCount: 500, imageUrl: "https://images.unsplash.com/photo-1537368910025-700350fe46c7?q=80&w=400&auto=format&fit=crop", experience: 8, price: 250000, description: "Chuyên gia về các bệnh cơ xương khớp." },
    { name: "BS.CKI Phạm Thị Mai", specialty: "Da liễu", hospital: "Phòng khám tư", rating: 4.7, reviewCount: 300, imageUrl: "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?q=80&w=400&auto=format&fit=crop", experience: 5, price: 200000, description: "Điều trị dứt điểm các bệnh da liễu." }
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
    { name: "Bệnh viện Bạch Mai", address: "78 Giải Phóng, Phương Đình, Đống Đa, Hà Nội", imageUrl: "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?q=80&w=600&auto=format&fit=crop" },
    { name: "Bệnh viện Việt Đức", address: "40 Tràng Thi, Hàng Bông, Hoàn Kiếm, Hà Nội", imageUrl: "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?q=80&w=600&auto=format&fit=crop" },
    { name: "Bệnh viện Nhi Trung Ương", address: "18/879 La Thành, Láng Thượng, Đống Đa, Hà Nội", imageUrl: "https://images.unsplash.com/photo-1516549655169-df83a0774514?q=80&w=600&auto=format&fit=crop" }
];

const clinics = [
    { name: "Phòng khám Đa khoa Tâm Anh", address: "108 Hoàng Như Tiếp, Bồ Đề, Long Biên, Hà Nội", rating: 4.8, type: "Đa khoa", imageUrl: "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?q=80&w=600&auto=format&fit=crop" },
    { name: "Phòng khám Medlatec", address: "42 Nghĩa Dũng, Phúc Xá, Ba Đình, Hà Nội", rating: 4.7, type: "Đa khoa", imageUrl: "https://images.unsplash.com/photo-1587351021759-3e566b6af7cc?q=80&w=600&auto=format&fit=crop" },
    { name: "Phòng khám Thu Cúc", address: "286 Thụy Khuê, Tây Hồ, Hà Nội", rating: 4.9, type: "Đa khoa", imageUrl: "https://images.unsplash.com/photo-1516549655169-df83a0774514?q=80&w=600&auto=format&fit=crop" }
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
        
        for (const clinic of clinics) {
            await db.collection('Clinics').add(clinic);
        }
        console.log("✅ Đã thêm Clinics!");

        console.log("🎉 HOÀN TẤT ĐỔ DỮ LIỆU!");
        process.exit(0);
    } catch (e) {
        console.error("Lỗi:", e);
        process.exit(1);
    }
}

seedData();
