const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middleware/auth');

// Lấy tham chiếu tới Firestore từ req.db (đã được cấu hình ở server.js)

// GET /api/users/profile
// Lấy thông tin chi tiết user
router.get('/profile', verifyToken, async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        
        const docRef = db.collection('Users').doc(uid);
        const doc = await docRef.get();
        
        if (!doc.exists) {
            return res.status(404).json({ error: 'User not found in database' });
        }
        const data = doc.data();
        data.uid = uid; // Ensure uid is included
        res.json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/users/profile
// Cập nhật thông tin cá nhân
router.put('/profile', verifyToken, async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        const { displayName, phone, dob, gender, address } = req.body;
        
        console.log(`[PUT /profile] User UID: ${uid}`);
        console.log(`[PUT /profile] Data received:`, req.body);
        
        const docRef = db.collection('Users').doc(uid);
        const doc = await docRef.get();
        
        if (!doc.exists) {
             return res.status(404).json({ error: 'User not found in database to update (UID mismatch)' });
        }
        
        await docRef.update({
            displayName: displayName || doc.data().displayName,
            phone: phone || doc.data().phone,
            dob: dob || doc.data().dob,
            gender: gender || doc.data().gender,
            address: address || doc.data().address
        });
            
        res.json({ message: 'Profile updated successfully' });
    } catch (error) {
        console.error(`[PUT /profile] Error:`, error);
        res.status(500).json({ error: error.message });
    }
});

// POST /api/users/register
// Gọi API này ngay sau khi App vừa đăng ký tài khoản Firebase Auth thành công
router.post('/register', verifyToken, async (req, res) => {
    try {
        const db = req.db;
        const uid = req.user.uid;
        const email = req.user.email;
        const { displayName, isDoctorRegistration } = req.body; 
        
        const assignedRole = isDoctorRegistration ? 'DOCTOR' : 'PATIENT';
        const name = displayName || email.split('@')[0];

        const docRef = db.collection('Users').doc(uid);
        const doc = await docRef.get();
        
        if (doc.exists) {
            return res.status(400).json({ error: 'User already registered in database' });
        }

        const userData = {
            email: email,
            displayName: name,
            role: assignedRole,
            createdAt: new Date().toISOString()
        };

        await docRef.set(userData);

        if (isDoctorRegistration) {
            try {
                // Thêm một bản ghi vào collection Doctors
                await db.collection('Doctors').add({
                    name: name,
                    user_id: uid,
                    rating: 5.0,
                    reviewCount: 0,
                    imageResId: 0
                });
            } catch (err) {
                console.error("Error creating Doctor profile link:", err);
            }
        }

        userData.uid = uid;
        res.status(201).json(userData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
