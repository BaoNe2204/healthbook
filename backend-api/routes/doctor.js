const express = require('express');
const router = express.Router();
const { db } = require('../firebase-config');
const { verifyToken, requireRole } = require('../middleware/auth');

// Tất cả các API trong file này đều yêu cầu quyền 'doctor'
router.use(verifyToken, requireRole('doctor'));

// GET /api/doctor/appointments
// Lấy danh sách lịch hẹn của bác sĩ này
router.get('/appointments', async (req, res) => {
    try {
        const doctorId = req.user.uid;
        // Lấy query parameters (ví dụ: ?date=2024-05-15)
        const date = req.query.date;

        let query = db.collection('appointments').where('doctorId', '==', doctorId);
        
        if (date) {
            query = query.where('date', '==', date);
        }

        const snapshot = await query.get();
        const appointments = [];
        snapshot.forEach(doc => appointments.push({ id: doc.id, ...doc.data() }));

        res.json(appointments);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// PUT /api/doctor/appointments/:id/status
// Cập nhật trạng thái lịch hẹn (Chấp nhận / Từ chối / Hoàn thành)
router.put('/appointments/:id/status', async (req, res) => {
    try {
        const { id } = req.params;
        const { status, reason } = req.body; // status: 'confirmed', 'cancelled', 'completed'

        if (!['confirmed', 'cancelled', 'completed'].includes(status)) {
            return res.status(400).json({ error: 'Invalid status' });
        }

        const appointmentRef = db.collection('appointments').doc(id);
        const doc = await appointmentRef.get();

        if (!doc.exists) {
            return res.status(404).json({ error: 'Appointment not found' });
        }

        // Đảm bảo bác sĩ chỉ được sửa lịch hẹn của chính mình
        if (doc.data().doctorId !== req.user.uid) {
            return res.status(403).json({ error: 'Forbidden: Not your appointment' });
        }

        const updateData = { status };
        if (reason && status === 'cancelled') {
            updateData.cancelReason = reason;
        }

        await appointmentRef.update(updateData);
        res.json({ message: 'Appointment status updated successfully', status });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/doctor/medical-records
// Bác sĩ viết bệnh án và đơn thuốc sau khi khám xong
router.post('/medical-records', async (req, res) => {
    try {
        const doctorId = req.user.uid;
        const { patientId, appointmentId, diagnosis, prescription, notes } = req.body;

        if (!patientId || !diagnosis) {
            return res.status(400).json({ error: 'patientId and diagnosis are required' });
        }

        const recordRef = db.collection('medical_records').doc();
        const recordData = {
            id: recordRef.id,
            doctorId,
            patientId,
            appointmentId: appointmentId || null,
            diagnosis,
            prescription: prescription || [],
            notes: notes || '',
            createdAt: new Date().toISOString()
        };

        await recordRef.set(recordData);

        // Nếu có appointmentId, cập nhật trạng thái appointment thành completed
        if (appointmentId) {
            await db.collection('appointments').doc(appointmentId).update({ status: 'completed' });
        }

        res.status(201).json(recordData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// POST /api/doctor/schedule
// Đặt lịch rảnh cho bác sĩ
router.post('/schedule', async (req, res) => {
    try {
        const doctorId = req.user.uid;
        const { date, timeSlots } = req.body; // timeSlots: ['08:00', '09:00', '10:00']

        if (!date || !timeSlots || !Array.isArray(timeSlots)) {
            return res.status(400).json({ error: 'date and timeSlots array are required' });
        }

        const scheduleId = `${doctorId}_${date}`;
        const scheduleRef = db.collection('schedules').doc(scheduleId);
        
        await scheduleRef.set({
            doctorId,
            date,
            timeSlots,
            updatedAt: new Date().toISOString()
        }, { merge: true });

        res.json({ message: 'Schedule updated successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
