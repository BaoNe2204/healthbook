const express = require('express');
const router = express.Router();
const { sql, poolPromise } = require('../db-config');
const { verifyToken } = require('../middleware/auth');

// GET /api/users/profile
// Lấy thông tin chi tiết user (bao gồm role)
router.get('/profile', verifyToken, async (req, res) => {
    try {
        const pool = await poolPromise;
        const uid = req.user.uid;
        
        const result = await pool.request()
            .input('id', sql.NVarChar, uid)
            .query('SELECT id AS uid, email, name AS displayName, phone, dob, gender, address, role, created_at AS createdAt FROM Users WHERE id = @id');
            
        if (result.recordset.length === 0) {
            return res.status(404).json({ error: 'User not found in database' });
        }
        res.json(result.recordset[0]);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/users/profile
// Cập nhật thông tin cá nhân
router.put('/profile', verifyToken, async (req, res) => {
    try {
        const pool = await poolPromise;
        const uid = req.user.uid;
        const { displayName, phone, dob, gender, address } = req.body;
        
        console.log(`[PUT /profile] User UID: ${uid}`);
        console.log(`[PUT /profile] Data received:`, req.body);
        
        const result = await pool.request()
            .input('id', sql.NVarChar, uid)
            .input('name', sql.NVarChar, displayName)
            .input('phone', sql.NVarChar, phone)
            .input('dob', sql.NVarChar, dob)
            .input('gender', sql.NVarChar, gender)
            .input('address', sql.NVarChar, address)
            .query(`
                UPDATE Users 
                SET name = @name, phone = @phone, dob = @dob, gender = @gender, address = @address
                WHERE id = @id
            `);
            
        console.log(`[PUT /profile] Rows affected:`, result.rowsAffected);
        
        if (result.rowsAffected[0] === 0) {
            return res.status(404).json({ error: 'User not found in database to update (UID mismatch)' });
        }
            
        res.json({ message: 'Profile updated successfully', rowsAffected: result.rowsAffected });
    } catch (error) {
        console.error(`[PUT /profile] Error:`, error);
        res.status(500).json({ error: error.message });
    }
});

// POST /api/users/register
// Gọi API này ngay sau khi App vừa đăng ký tài khoản Firebase Auth thành công
router.post('/register', verifyToken, async (req, res) => {
    try {
        const pool = await poolPromise;
        const uid = req.user.uid;
        const email = req.user.email;
        const { displayName, isDoctorRegistration } = req.body; 
        
        // Mặc định role là PATIENT. Nếu là bác sĩ, set DOCTOR (trạng thái chờ duyệt có thể thêm cột sau nếu cần)
        const assignedRole = isDoctorRegistration ? 'DOCTOR' : 'PATIENT';
        const name = displayName || email.split('@')[0];

        // Kiểm tra xem user đã tồn tại chưa
        const checkResult = await pool.request()
            .input('id', sql.NVarChar, uid)
            .query('SELECT id FROM Users WHERE id = @id');
            
        if (checkResult.recordset.length > 0) {
            return res.status(400).json({ error: 'User already registered in database' });
        }

        // Thêm vào SQL Server
        await pool.request()
            .input('id', sql.NVarChar, uid)
            .input('email', sql.NVarChar, email)
            .input('name', sql.NVarChar, name)
            .input('role', sql.NVarChar, assignedRole)
            .query(`
                INSERT INTO Users (id, email, name, role)
                VALUES (@id, @email, @name, @role)
            `);

        const userData = {
            uid: uid,
            email: email,
            displayName: name,
            role: assignedRole,
            createdAt: new Date().toISOString()
        };

        res.status(201).json(userData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
