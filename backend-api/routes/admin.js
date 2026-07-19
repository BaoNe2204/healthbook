const express = require('express');
const router = express.Router();
const { sql, poolPromise } = require('../db-config');
const { verifyToken, requireRole } = require('../middleware/auth');

// Tất cả các API trong file này đều yêu cầu quyền 'admin'
router.use(verifyToken, requireRole('admin'));

// GET /api/admin/dashboard
// Thống kê tổng quan
router.get('/dashboard', async (req, res) => {
    try {
        const pool = await poolPromise;
        
        const totalUsersRes = await pool.request().query('SELECT COUNT(*) as count FROM Users');
        const totalAppointmentsRes = await pool.request().query('SELECT COUNT(*) as count FROM Appointments');
        const totalDoctorsRes = await pool.request().query("SELECT COUNT(*) as count FROM Users WHERE role = 'DOCTOR'");
        const totalPatientsRes = await pool.request().query("SELECT COUNT(*) as count FROM Users WHERE role = 'PATIENT'");

        res.json({
            totalUsers: totalUsersRes.recordset[0].count,
            totalDoctors: totalDoctorsRes.recordset[0].count,
            totalPatients: totalPatientsRes.recordset[0].count,
            totalAppointments: totalAppointmentsRes.recordset[0].count
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GET /api/admin/users
// Danh sách tất cả user
router.get('/users', async (req, res) => {
    try {
        const pool = await poolPromise;
        const result = await pool.request().query('SELECT * FROM Users');
        res.json(result.recordset);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/admin/users/:uid/ban
// Khóa hoặc mở khóa tài khoản
router.put('/users/:uid/ban', async (req, res) => {
    try {
        const pool = await poolPromise;
        const { uid } = req.params;
        const { isBanned } = req.body;
        const statusVal = isBanned ? 'banned' : 'active';

        await pool.request()
            .input('id', sql.NVarChar, uid)
            .input('status', sql.NVarChar, statusVal)
            .query('UPDATE Users SET status = @status WHERE id = @id');

        res.json({ message: `User status updated to ${statusVal}` });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/admin/doctors/:uid/approve
// Duyệt hồ sơ bác sĩ (từ pending sang active)
router.put('/doctors/:uid/approve', async (req, res) => {
    try {
        const pool = await poolPromise;
        const { uid } = req.params;
        const { approve } = req.body; // true or false
        const statusVal = approve ? 'active' : 'rejected';

        // Check if user exists and is a doctor
        const checkResult = await pool.request()
            .input('id', sql.NVarChar, uid)
            .query("SELECT role FROM Users WHERE id = @id");

        if (checkResult.recordset.length === 0 || checkResult.recordset[0].role !== 'DOCTOR') {
            return res.status(404).json({ error: 'Doctor user not found' });
        }

        await pool.request()
            .input('id', sql.NVarChar, uid)
            .input('status', sql.NVarChar, statusVal)
            .query('UPDATE Users SET status = @status WHERE id = @id');

        res.json({ message: `Doctor status updated to ${statusVal} successfully` });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/admin/hospitals
// Thêm bệnh viện mới
router.post('/hospitals', async (req, res) => {
    try {
        const pool = await poolPromise;
        const { name, address } = req.body;

        if (!name) {
            return res.status(400).json({ error: 'Hospital name is required' });
        }

        const result = await pool.request()
            .input('name', sql.NVarChar, name)
            .input('address', sql.NVarChar, address || '')
            .query(`
                INSERT INTO Hospitals (name, address)
                OUTPUT INSERTED.id
                VALUES (@name, @address)
            `);

        res.status(201).json({ id: result.recordset[0].id, name, address });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/admin/specialties
// Thêm chuyên khoa mới
router.post('/specialties', async (req, res) => {
    try {
        const pool = await poolPromise;
        const { name, description } = req.body;

        if (!name) {
            return res.status(400).json({ error: 'Specialty name is required' });
        }

        const result = await pool.request()
            .input('name', sql.NVarChar, name)
            .input('description', sql.NVarChar, description || '')
            .query(`
                INSERT INTO Specialties (name, description)
                OUTPUT INSERTED.id
                VALUES (@name, @description)
            `);

        res.status(201).json({ id: result.recordset[0].id, name, description });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
