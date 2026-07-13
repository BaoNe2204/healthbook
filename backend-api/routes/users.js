const express = require('express');
const router = express.Router();
const { db } = require('../firebase-config');
const { verifyToken } = require('../middleware/auth');

// GET /api/users/profile
// Lấy thông tin chi tiết user (bao gồm role)
router.get('/profile', verifyToken, async (req, res) => {
    try {
        const uid = req.user.uid;
        const userDoc = await db.collection('users').doc(uid).get();
        if (!userDoc.exists) {
            return res.status(404).json({ error: 'User not found in database' });
        }
        res.json(userDoc.data());
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/users/register
// Gọi API này ngay sau khi App vừa đăng ký tài khoản Firebase Auth thành công
router.post('/register', verifyToken, async (req, res) => {
    try {
        const uid = req.user.uid;
        const email = req.user.email;
        const { displayName, isDoctorRegistration } = req.body; 
        
        // Mặc định role là patient. Nếu là bác sĩ đăng ký, role vẫn là doctor nhưng status là pending
        const assignedRole = isDoctorRegistration ? 'doctor' : 'patient';
        const status = isDoctorRegistration ? 'pending' : 'active';

        const userDoc = await db.collection('users').doc(uid).get();
        if (userDoc.exists) {
            return res.status(400).json({ error: 'User already registered in database' });
        }

        const userData = {
            uid: uid,
            email: email,
            displayName: displayName || email.split('@')[0],
            role: assignedRole,
            status: status,
            createdAt: new Date().toISOString()
        };

        await db.collection('users').doc(uid).set(userData);
        res.status(201).json(userData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
