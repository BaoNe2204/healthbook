const { initializeApp, cert } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
const { getAuth } = require('firebase-admin/auth');
const serviceAccount = require('./serviceAccountKey.json');

initializeApp({
    credential: cert(serviceAccount)
});

const db = getFirestore();
const auth = getAuth();

const testAccounts = [
    {
        email: 'admin@healthbook.com',
        password: 'password123',
        displayName: 'Quản trị viên',
        role: 'ADMIN'
    },
    {
        email: 'bs.duc@healthbook.com',
        password: 'password123',
        displayName: 'TS.BS Nguyễn Minh Đức',
        role: 'DOCTOR'
    },
    {
        email: 'bs.hang@healthbook.com',
        password: 'password123',
        displayName: 'BS.CKII Trần Thị Hằng',
        role: 'DOCTOR'
    },
    {
        email: 'bs.thanh@healthbook.com',
        password: 'password123',
        displayName: 'BS.CKI Lê Văn Thành',
        role: 'DOCTOR'
    },
    {
        email: 'bs.mai@healthbook.com',
        password: 'password123',
        displayName: 'BS.CKI Phạm Thị Mai',
        role: 'DOCTOR'
    }
];

async function createAccounts() {
    console.log("Bắt đầu tạo tài khoản test...");
    try {
        for (const account of testAccounts) {
            let userRecord;
            try {
                // Thử tìm user xem đã tồn tại chưa
                userRecord = await auth.getUserByEmail(account.email);
                console.log(`Tài khoản ${account.email} đã tồn tại, đang cập nhật password...`);
                await auth.updateUser(userRecord.uid, { password: account.password });
            } catch (error) {
                if (error.code === 'auth/user-not-found') {
                    // Chưa tồn tại -> Tạo mới
                    console.log(`Đang tạo tài khoản ${account.email}...`);
                    userRecord = await auth.createUser({
                        email: account.email,
                        password: account.password,
                        displayName: account.displayName
                    });
                } else {
                    throw error;
                }
            }

            const uid = userRecord.uid;

            // Lưu vào bảng Users
            await db.collection('Users').doc(uid).set({
                displayName: account.displayName,
                email: account.email,
                role: account.role,
                phone: '0123456789'
            }, { merge: true });

            // Nếu là Bác sĩ, tìm document trong bảng Doctors và gắn user_id
            if (account.role === 'DOCTOR') {
                const snapshot = await db.collection('Doctors')
                    .where('name', '==', account.displayName)
                    .get();
                
                if (!snapshot.empty) {
                    const docId = snapshot.docs[0].id;
                    await db.collection('Doctors').doc(docId).update({
                        user_id: uid
                    });
                    console.log(`✅ Đã liên kết tài khoản ${account.email} với bác sĩ trong DB!`);
                } else {
                    console.log(`⚠️ Không tìm thấy bác sĩ tên "${account.displayName}" trong DB để liên kết.`);
                }
            } else {
                console.log(`✅ Đã thiết lập quyền Admin cho ${account.email}!`);
            }
        }
        console.log("🎉 Hoàn tất tạo tài khoản!");
        process.exit(0);
    } catch (e) {
        console.error("Lỗi:", e);
        process.exit(1);
    }
}

createAccounts();
