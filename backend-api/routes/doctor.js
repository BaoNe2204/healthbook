const express = require('express');
const router = express.Router();
const { sql, poolPromise } = require('../db-config');
const { verifyToken, requireRole } = require('../middleware/auth');

// Tất cả các API trong file này đều yêu cầu quyền 'doctor'
router.use(verifyToken, requireRole('doctor'));

// Helper to get Doctor auto-increment ID from Firebase user UID
async function getDoctorIdFromUid(uid) {
    const pool = await poolPromise;
    const result = await pool.request()
        .input('user_id', sql.NVarChar, uid)
        .query('SELECT id FROM Doctors WHERE user_id = @user_id');
    if (result.recordset.length > 0) {
        return result.recordset[0].id;
    }
    return null;
}

// GET /api/doctor/appointments
// Lấy danh sách lịch hẹn của bác sĩ này
router.get('/appointments', async (req, res) => {
    try {
        const pool = await poolPromise;
        const doctorId = await getDoctorIdFromUid(req.user.uid);
        
        if (!doctorId) {
            return res.json([]); // Return empty list if this doctor user is not linked in Doctors table
        }

        const date = req.query.date;
        let queryStr = `
            SELECT a.*, u.name as patientName, u.phone as patientPhone, u.email as patientEmail 
            FROM Appointments a
            LEFT JOIN Users u ON a.patient_id = u.id
            WHERE a.doctor_id = @doctor_id
        `;

        const request = pool.request().input('doctor_id', sql.Int, doctorId);

        if (date) {
            queryStr += ' AND a.appointment_date = @date';
            request.input('date', sql.NVarChar, date);
        }

        queryStr += ' ORDER BY a.appointment_date DESC, a.appointment_time DESC';

        const result = await request.query(queryStr);
        res.json(result.recordset);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/doctor/appointments/:id/status
// Cập nhật trạng thái lịch hẹn (Chấp nhận / Từ chối / Hoàn thành)
router.put('/appointments/:id/status', async (req, res) => {
    try {
        const pool = await poolPromise;
        const doctorId = await getDoctorIdFromUid(req.user.uid);
        if (!doctorId) {
            return res.status(403).json({ error: 'Doctor profile not found' });
        }

        const { id } = req.params;
        const { status } = req.body; // status: 'confirmed', 'cancelled', 'completed' (or 'Đã duyệt', 'Đã hủy', 'Đã qua')

        // Map English status back to Vietnamese for consistency
        let statusVN = status;
        if (status === 'confirmed') statusVN = 'Đã duyệt';
        else if (status === 'cancelled') statusVN = 'Đã hủy';
        else if (status === 'completed') statusVN = 'Đã qua';

        // Check if appointment exists and is for this doctor
        const checkResult = await pool.request()
            .input('id', sql.Int, id)
            .query('SELECT doctor_id FROM Appointments WHERE id = @id');

        if (checkResult.recordset.length === 0) {
            return res.status(404).json({ error: 'Appointment not found' });
        }

        if (checkResult.recordset[0].doctor_id !== doctorId) {
            return res.status(403).json({ error: 'Forbidden: Not your appointment' });
        }

        await pool.request()
            .input('id', sql.Int, id)
            .input('status', sql.NVarChar, statusVN)
            .query('UPDATE Appointments SET status = @status WHERE id = @id');

        res.json({ message: 'Appointment status updated successfully', status: statusVN });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/doctor/medical-records
// Bác sĩ viết bệnh án và đơn thuốc sau khi khám xong
router.post('/medical-records', async (req, res) => {
    try {
        const pool = await poolPromise;
        const doctorId = await getDoctorIdFromUid(req.user.uid);
        if (!doctorId) {
            return res.status(403).json({ error: 'Doctor profile not found' });
        }

        const { patientId, appointmentId, diagnosis, prescription, notes } = req.body;

        if (!patientId || !diagnosis) {
            return res.status(400).json({ error: 'patientId and diagnosis are required' });
        }

        // Map prescription array to comma/newline separated string if array, otherwise keep as is
        const prescriptionStr = Array.isArray(prescription) ? prescription.join(', ') : (prescription || '');

        const result = await pool.request()
            .input('doctor_id', sql.Int, doctorId)
            .input('patient_id', sql.NVarChar, patientId)
            .input('appointment_id', sql.Int, appointmentId || null)
            .input('diagnosis', sql.NVarChar, diagnosis)
            .input('prescription', sql.NVarChar, prescriptionStr)
            .input('notes', sql.NVarChar, notes || '')
            .query(`
                INSERT INTO MedicalRecords (doctor_id, patient_id, appointment_id, diagnosis, prescription, notes)
                OUTPUT INSERTED.id
                VALUES (@doctor_id, @patient_id, @appointment_id, @diagnosis, @prescription, @notes)
            `);

        // Cập nhật trạng thái lịch hẹn thành 'Đã qua' (completed)
        if (appointmentId) {
            await pool.request()
                .input('id', sql.Int, appointmentId)
                .query("UPDATE Appointments SET status = N'Đã qua' WHERE id = @id");
        }

        const recordId = result.recordset[0].id;
        res.status(201).json({
            id: recordId,
            doctorId,
            patientId,
            appointmentId: appointmentId || null,
            diagnosis,
            prescription,
            notes,
            createdAt: new Date().toISOString()
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/doctor/schedule
// Đặt lịch rảnh cho bác sĩ
router.post('/schedule', async (req, res) => {
    try {
        const pool = await poolPromise;
        const doctorId = await getDoctorIdFromUid(req.user.uid);
        if (!doctorId) {
            return res.status(403).json({ error: 'Doctor profile not found' });
        }

        const { date, timeSlots } = req.body; // timeSlots: ['08:00', '09:00', '10:00']

        if (!date || !timeSlots || !Array.isArray(timeSlots)) {
            return res.status(400).json({ error: 'date and timeSlots array are required' });
        }

        const timeSlotsStr = timeSlots.join(',');

        // Check if schedule for date already exists
        const checkResult = await pool.request()
            .input('doctor_id', sql.Int, doctorId)
            .input('date', sql.NVarChar, date)
            .query('SELECT id FROM Schedules WHERE doctor_id = @doctor_id AND available_date = @date');

        if (checkResult.recordset.length > 0) {
            // Update
            await pool.request()
                .input('id', sql.Int, checkResult.recordset[0].id)
                .input('slots', sql.NVarChar, timeSlotsStr)
                .query('UPDATE Schedules SET time_slots = @slots, updated_at = GETDATE() WHERE id = @id');
        } else {
            // Insert
            await pool.request()
                .input('doctor_id', sql.Int, doctorId)
                .input('date', sql.NVarChar, date)
                .input('slots', sql.NVarChar, timeSlotsStr)
                .query('INSERT INTO Schedules (doctor_id, available_date, time_slots) VALUES (@doctor_id, @date, @slots)');
        }

        res.json({ message: 'Schedule updated successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
