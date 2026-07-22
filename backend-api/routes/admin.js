const express = require('express');
const router = express.Router();
const { verifyToken, requireRole } = require('../middleware/auth');

// Tất cả các API trong file này đều yêu cầu quyền 'admin'
router.use(verifyToken, requireRole('admin'));

// GET /api/admin/dashboard
// Thống kê tổng quan
router.get('/dashboard', async (req, res) => {
    try {
        const db = req.db;
        
        // This is a naive count for small datasets. For large datasets, use Firebase aggregation queries.
        const usersSnap = await db.collection('Users').get();
        const appointmentsSnap = await db.collection('Appointments').get();
        
        let totalUsers = 0;
        let totalDoctors = 0;
        let totalPatients = 0;

        usersSnap.forEach(doc => {
            totalUsers++;
            const data = doc.data();
            if (data.role === 'DOCTOR') totalDoctors++;
            if (data.role === 'PATIENT') totalPatients++;
        });

        res.json({
            totalUsers,
            totalDoctors,
            totalPatients,
            totalAppointments: appointmentsSnap.size
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET /api/admin/users
// Danh sách tất cả user
router.get('/users', async (req, res) => {
    try {
        const db = req.db;
        const snapshot = await db.collection('Users').get();
        const users = [];
        const seen = new Set();
        snapshot.forEach(doc => {
            const data = doc.data();
            const role = data.role ? data.role.toUpperCase() : '';
            // For doctors, hospitals, clinics, avoid duplicates by name or email
            if (role === 'DOCTOR' || role === 'HOSPITAL' || role === 'CLINIC') {
                const key = data.displayName || data.email;
                if (key) {
                    if (seen.has(key)) return;
                    seen.add(key);
                }
            }
            users.push({ id: doc.id, ...data });
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
        const db = req.db;
        const { uid } = req.params;
        const { isBanned } = req.body;
        const statusVal = isBanned ? 'banned' : 'active';

        await db.collection('Users').doc(uid).update({ status: statusVal });

        res.json({ message: `User status updated to ${statusVal}` });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/admin/users/:uid/role
// Thay đổi vai trò người dùng (PATIENT / DOCTOR / ADMIN)
router.put('/users/:uid/role', async (req, res) => {
    try {
        const db = req.db;
        const { uid } = req.params;
        const { role } = req.body;

        if (!role) {
            return res.status(400).json({ error: 'Role is required' });
        }

        await db.collection('Users').doc(uid).update({ role: role.toUpperCase() });

        res.json({ message: `User role updated to ${role}` });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/admin/doctors/:uid/approve
// Duyệt hồ sơ bác sĩ (từ pending sang active)
router.put('/doctors/:uid/approve', async (req, res) => {
    try {
        const db = req.db;
        const { uid } = req.params;
        const { approve } = req.body; 
        const statusVal = approve ? 'active' : 'rejected';

        const docRef = db.collection('Users').doc(uid);
        const doc = await docRef.get();

        if (!doc.exists || doc.data().role !== 'DOCTOR') {
            return res.status(404).json({ error: 'Doctor user not found' });
        }

        await docRef.update({ status: statusVal });

        res.json({ message: `Doctor status updated to ${statusVal} successfully` });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/admin/hospitals
// Thêm bệnh viện mới
router.post('/hospitals', async (req, res) => {
    try {
        const db = req.db;
        const { name, address } = req.body;

        if (!name) {
            return res.status(400).json({ error: 'Hospital name is required' });
        }

        const data = { name, address: address || '' };
        const docRef = await db.collection('Hospitals').add(data);

        data.id = docRef.id;
        res.status(201).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// DELETE /api/admin/hospitals/:id
// Xóa bệnh viện
router.delete('/hospitals/:id', async (req, res) => {
    try {
        const db = req.db;
        const { id } = req.params;
        await db.collection('Hospitals').doc(id).delete();
        res.json({ message: 'Hospital deleted successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/admin/specialties
// Thêm chuyên khoa mới
router.post('/specialties', async (req, res) => {
    try {
        const db = req.db;
        const { name, description } = req.body;

        if (!name) {
            return res.status(400).json({ error: 'Specialty name is required' });
        }

        const data = { name, description: description || '' };
        const docRef = await db.collection('Specialties').add(data);

        data.id = docRef.id;
        res.status(201).json(data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// DELETE /api/admin/specialties/:id
// Xóa chuyên khoa
router.delete('/specialties/:id', async (req, res) => {
    try {
        const db = req.db;
        const { id } = req.params;
        await db.collection('Specialties').doc(id).delete();
        res.json({ message: 'Specialty deleted successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
