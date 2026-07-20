const { initializeApp, cert } = require('firebase-admin/app');
const { getAuth } = require('firebase-admin/auth');
const serviceAccount = require('./serviceAccountKey.json');
initializeApp({ credential: cert(serviceAccount) });

async function seed() {
    try {
        const auth = getAuth();
        let adminUid = null;
        try {
            const admin = await auth.createUser({ email: 'admin@healthbook.com', password: 'password123', displayName: 'Admin' });
            adminUid = admin.uid;
        } catch (e) {
            if(e.code === 'auth/email-already-exists') {
                const user = await auth.getUserByEmail('admin@healthbook.com');
                adminUid = user.uid;
                await auth.updateUser(adminUid, { password: 'password123' });
            } else throw e;
        }

        let docUid = null;
        try {
            const doc = await auth.createUser({ email: 'doctor@healthbook.com', password: 'password123', displayName: 'Dr. John' });
            docUid = doc.uid;
        } catch (e) {
            if(e.code === 'auth/email-already-exists') {
                const user = await auth.getUserByEmail('doctor@healthbook.com');
                docUid = user.uid;
                await auth.updateUser(docUid, { password: 'password123' });
            } else throw e;
        }

        console.log('✅ Accounts created in Firebase Auth:');
        console.log('ADMIN: admin@healthbook.com / password123');
        console.log('DOCTOR: doctor@healthbook.com / password123');
        console.log('\n❌ SQL Server is not automatically reachable from this script. Please run the following SQL queries in your SQL Server database to grant them roles:');
        console.log(`\nINSERT INTO Users (id, email, displayName, role) VALUES ('${adminUid}', 'admin@healthbook.com', 'Admin', 'ADMIN');\nINSERT INTO Users (id, email, displayName, role) VALUES ('${docUid}', 'doctor@healthbook.com', 'Dr. John', 'DOCTOR');`);
        process.exit(0);
    } catch(e) {
        console.error(e);
        process.exit(1);
    }
}
seed();
