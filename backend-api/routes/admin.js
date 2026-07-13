const express = require('express');
const router = express.Router();
const { db } = require('../firebase-config');
const { verifyToken, requireRole } = require('../middleware/auth');

// Tất cả các API trong file này đều yêu cầu quyền 'admin'
router.use(verifyToken, requireRole('admin'));

// GET /api/admin/dashboard
// Thống kê tổng quan
router.get('/dashboard', async (req, res) => {
    try {
        const usersSnapshot = await db.collection('users').count().get();
        const appointmentsSnapshot = await db.collection('appointments').count().get();
        
        // Count roles
        const doctorsSnapshot = await db.collection('users').where('role', '==', 'doctor').count().get();
        const patientsSnapshot = await db.collection('users').where('role', '==', 'patient').count().get();

        res.json({
            totalUsers: usersSnapshot.data().count,
            totalDoctors: doctorsSnapshot.data().count,
            totalPatients: patientsSnapshot.data().count,
            totalAppointments: appointmentsSnapshot.data().count
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET /api/admin/users
// Danh sách tất cả user
router.get('/users', async (req, res) => {
    try {
        const snapshot = await db.collection('users').get();
        const users = [];
        snapshot.forEach(doc => {
            const data = doc.data();
            users.push(data);
        });
        res.json(users);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/admin/users/:uid/ban
// Khóa hoặc mở khóa tài khoản
router.put('/users/:uid/ban', async (req, res) => {
    try {
        const { uid } = req.params;
        const { isBanned } = req.body;

        await db.collection('users').doc(uid).update({
            status: isBanned ? 'banned' : 'active'
        });

        res.json({ message: `User status updated to ${isBanned ? 'banned' : 'active'}` });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/admin/doctors/:uid/approve
// Duyệt hồ sơ bác sĩ (từ pending sang active)
router.put('/doctors/:uid/approve', async (req, res) => {
    try {
        const { uid } = req.params;
        const { approve } = req.body; // true or false

        const userRef = db.collection('users').doc(uid);
        const doc = await userRef.get();

        if (!doc.exists || doc.data().role !== 'doctor') {
            return res.status(404).json({ error: 'Doctor not found' });
        }

        if (approve) {
            await userRef.update({ status: 'active' });
            res.json({ message: 'Doctor approved successfully' });
        } else {
            await userRef.update({ status: 'rejected' });
            res.json({ message: 'Doctor application rejected' });
        }
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/admin/hospitals
// Thêm bệnh viện mới
router.post('/hospitals', async (req, res) => {
    try {
        const data = req.body;
        const docRef = db.collection('hospitals').doc();
        data.id = docRef.id;
        await docRef.set(data);
        res.status(201).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/admin/specialties
// Thêm chuyên khoa mới
router.post('/specialties', async (req, res) => {
    try {
        const data = req.body;
        const docRef = db.collection('specialties').doc();
        data.id = docRef.id;
        await docRef.set(data);
        res.status(201).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
