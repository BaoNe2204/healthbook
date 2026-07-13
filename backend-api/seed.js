const { db } = require('./firebase-config');

if (!db) {
    console.error("Firebase DB is not initialized. Please ensure serviceAccountKey.json is present.");
    process.exit(1);
}

const mockDoctors = [
    { id: "1", name: "TS.BS Nguyễn Minh Đức", specialty: "Tim mạch", hospital: "Bệnh viện Bạch Mai", rating: 4.9, reviewCount: 1000, imageResId: 17301508 },
    { id: "2", name: "BS.CKII Trần Thị Hằng", specialty: "Nhi khoa", hospital: "Bệnh viện Nhi Trung Ương", rating: 4.8, reviewCount: 800, imageResId: 17301508 },
    { id: "3", name: "BS.CKI Lê Văn Thành", specialty: "Cơ xương khớp", hospital: "Bệnh viện Việt Đức", rating: 4.9, reviewCount: 500, imageResId: 17301508 },
    { id: "4", name: "BS.CKI Phạm Thị Mai", specialty: "Da liễu", hospital: "Phòng khám tư", rating: 4.7, reviewCount: 300, imageResId: 17301508 }
];

const mockSpecialties = [
    { id: "1", name: "Tim mạch", iconResId: 17301584 },
    { id: "2", name: "Nhi khoa", iconResId: 17301584 },
    { id: "3", name: "Da liễu", iconResId: 17301584 },
    { id: "4", name: "Sản phụ khoa", iconResId: 17301584 }
];

const mockHospitals = [
    { id: "1", name: "Bệnh viện Bạch Mai", address: "78 Giải Phóng, Phương Đình, Đống Đa, Hà Nội" },
    { id: "2", name: "Bệnh viện Việt Đức", address: "40 Tràng Thi, Hàng Bông, Hoàn Kiếm, Hà Nội" },
    { id: "3", name: "Bệnh viện Nhi Trung Ương", address: "18/879 La Thành, Láng Thượng, Đống Đa, Hà Nội" }
];

async function seedData() {
    try {
        console.log("Seeding Doctors...");
        for (const doc of mockDoctors) {
            await db.collection('doctors').doc(doc.id).set(doc);
        }
        
        console.log("Seeding Specialties...");
        for (const spec of mockSpecialties) {
            await db.collection('specialties').doc(spec.id).set(spec);
        }

        console.log("Seeding Hospitals...");
        for (const hosp of mockHospitals) {
            await db.collection('hospitals').doc(hosp.id).set(hosp);
        }
        
        console.log("Seeding Completed Successfully!");
        process.exit(0);
    } catch (error) {
        console.error("Error seeding data:", error);
        process.exit(1);
    }
}

seedData();
