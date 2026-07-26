const { getAuth } = require('firebase-admin/auth');
const { db } = require('./firebase-config');

const clinics = [
    { email: 'tamanh@healthbook.com', name: 'Phòng khám Đa khoa Tâm Anh' },
    { email: 'medlatec@healthbook.com', name: 'Phòng khám Medlatec' },
    { email: 'thucuc@healthbook.com', name: 'Phòng khám Thu Cúc' }
];

async function createClinics() {
    console.log('Bắt đầu tạo tài khoản cho các phòng khám...');
    
    for (const clinic of clinics) {
        try {
            // Check if user already exists
            let userRecord;
            try {
                userRecord = await getAuth().getUserByEmail(clinic.email);
                console.log(`Tài khoản ${clinic.email} đã tồn tại.`);
            } catch (err) {
                if (err.code === 'auth/user-not-found') {
                    // Create user
                    userRecord = await getAuth().createUser({
                        email: clinic.email,
                        password: 'password123',
                        displayName: clinic.name,
                    });
                    console.log(`Đã tạo tài khoản Auth cho ${clinic.email}`);
                } else {
                    throw err;
                }
            }
            
            // Upsert in Firestore Users collection
            await db.collection('Users').doc(userRecord.uid).set({
                email: clinic.email,
                role: 'clinic',
                clinic_name: clinic.name,
                created_at: new Date().toISOString()
            });
            console.log(`Đã cập nhật Firestore cho ${clinic.name} (${clinic.email})`);
        } catch (error) {
            console.error(`Lỗi khi tạo tài khoản cho ${clinic.email}:`, error);
        }
    }
    console.log('Hoàn thành!');
    process.exit(0);
}

createClinics();
